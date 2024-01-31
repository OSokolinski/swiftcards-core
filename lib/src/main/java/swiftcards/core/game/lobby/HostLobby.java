package swiftcards.core.game.lobby;

import swiftcards.core.game.ActivityPropagator;
import swiftcards.core.game.GameController;
import swiftcards.core.networking.event.ExceptionThrown;
import swiftcards.core.networking.event.lobby.GameStarted;
import swiftcards.core.networking.*;
import swiftcards.core.networking.event.ChannelDisconnected;
import swiftcards.core.networking.event.IncomingEvent;
import swiftcards.core.networking.event.ingame.GuestHandshake;
import swiftcards.core.networking.event.ingame.HostHandshake;
import swiftcards.core.networking.event.lobby.LobbyReadinessChanged;
import swiftcards.core.networking.event.lobby.LobbyPrepared;
import swiftcards.core.networking.event.lobby.ReadinessToggled;
import swiftcards.core.networking.event.lobby.SettingsUpdated;
import swiftcards.core.player.AIPlayer;
import swiftcards.core.player.HumanPlayer;
import swiftcards.core.player.NetworkPlayer;
import swiftcards.core.player.Player;
import swiftcards.core.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HostLobby extends Freezable implements Lobby {

    private LobbyType lobbyType;
    private GameController gameController;
    private GameSettings gameSettings;
    private boolean arePlayersReady;

    private Subscriber<IncomingEvent> onIncomingEventSubscriber;
    private Subscriber<ConnectionChannel> onClientDisconnected;

    private ConnectionInterface connectionInterface;
    private NetworkInternalEventBus internalNetworkEventBus;
    private NetworkExternalEventBus externalNetworkEventBus;

    private final DefaultEventBus lobbyEventBus;

    public HostLobby(LobbyType type, Integer port) {
        boolean uiEnabled = type != LobbyType.ONLINE;

        arePlayersReady = false;
        gameSettings = new GameSettings();
        internalNetworkEventBus = new NetworkInternalEventBus();
        lobbyEventBus = new DefaultEventBus();
        lobbyType = type;
        gameSettings.setDefaultPlayerConfig(type);


        if (lobbyType != LobbyType.OFFLINE) {
            onIncomingEventSubscriber = new Subscriber<>(this::onIncomingEvent);
            onClientDisconnected = new Subscriber<>(this::handleChannelDisconnection);
            internalNetworkEventBus.on(ExternalEventEmitted.class, onIncomingEventSubscriber);
            internalNetworkEventBus.on(ChannelDisconnected.class, onClientDisconnected);
            internalNetworkEventBus.on(ExceptionThrown.class, new Subscriber<>(this::emitLobbyException));
            connectionInterface = ConnectionInterface.initAsServer(port, internalNetworkEventBus);
            externalNetworkEventBus = new NetworkExternalEventBus(connectionInterface);
            gameController = new GameController(new ActivityPropagator(uiEnabled, connectionInterface));

            ConfigService.getInstance().log("Listening started. Waiting for players...");
        }
        else {
            gameController = new GameController(new ActivityPropagator(true, null));
        }

        if (uiEnabled) {
            try {
                Player humanPlayer = new HumanPlayer();

                gameSettings.setPlayerOnSlot(gameSettings.players.get(0), humanPlayer);
                gameController.applyPlayer(humanPlayer);
            }
            catch (Exception e) {
                ConfigService.getInstance().throwAndExit("Unable to create host lobby: %s", e);
            }
        }

        ConfigService.getInstance().log("Host lobby initialized successfully");
    }

    @Override
    public void prepare() {
        if (lobbyType == LobbyType.OFFLINE) {
            updateSettings(gameSettings);
        }
        else {
            try {
                connectionInterface.runListener();
            }
            catch (Exception e) {
                lobbyEventBus.emit(new ExceptionThrown(e));
            }
        }
        lobbyEventBus.emit(new LobbyPrepared(gameSettings));
    }

    @Override
    public EventBus getEventBus() {
        return lobbyEventBus;
    }

    @Override
    public void close() {
        if (lobbyType == LobbyType.OFFLINE) return;
        internalNetworkEventBus.unsubscribe(ExternalEventEmitted.class, onIncomingEventSubscriber);
        internalNetworkEventBus.unsubscribe(ChannelDisconnected.class, onClientDisconnected);
    }

    @Override
    public void disconnect() {
        if (lobbyType == LobbyType.OFFLINE) return;

        close();
        for (int i = 0; i < connectionInterface.getConnections().size(); i++) {
            connectionInterface.disconnect(i);
        }
        connectionInterface.stopListener();
    }

    public GameController prepareForStart() {
        broadcastEvent(new GameStarted());
        if (lobbyType != LobbyType.OFFLINE) {
            connectionInterface.stopListener();
        }
        return gameController;
    }

    @Override
    public ConnectionInterface getConnectionInterface() {
        return connectionInterface;
    }

    private synchronized void onIncomingEvent(IncomingEvent event) {
        if (event.getEvent() instanceof GuestHandshake) {
            GuestHandshake handshakeEvent = (GuestHandshake) event.getEvent();
            PlayerCredentials credentials = handshakeEvent.getEventData();
            NetworkPlayer player = new NetworkPlayer(event.getConnectionId(), internalNetworkEventBus, externalNetworkEventBus);
            player.setDisplayName(credentials.getDisplayName());
            addNetworkPlayer(player);
        }
        else if (event.getEvent() instanceof ReadinessToggled) {
            gameSettings.players
                .stream()
                .filter(p -> p.connectionId == event.getConnectionId())
                .collect(Collectors.toList())
                .get(0)
                .toggleIsReady();
            updateSettings(gameSettings);

            boolean isEveryPlayerReady = gameSettings.isEveryPlayerReady();
            if (arePlayersReady != isEveryPlayerReady) {
                arePlayersReady = isEveryPlayerReady;
                lobbyEventBus.emit(new LobbyReadinessChanged(isEveryPlayerReady));
            }
        }

    }

    private void addNetworkPlayer(Player player) {

        PlayerSlot networkSlot = gameSettings.getAvailableNetworkPlayerSlot();

        if (networkSlot == null) {
            connectionInterface.disconnect(((NetworkPlayer) player).getConnectionId());
            return;
        }

        NetworkPlayer joinedPlayer = (NetworkPlayer) gameController.applyPlayer(player);
        gameSettings.setPlayerOnSlot(networkSlot, joinedPlayer);
        externalNetworkEventBus.emit(new HostHandshake(gameSettings), joinedPlayer.getConnectionId());
        updateSettings(gameSettings);
        ConfigService.getInstance().log("Player '%s' (Connection ID: %d) joined to the lobby %n", joinedPlayer.getDisplayName(), joinedPlayer.getConnectionId());
    }

    private void handleChannelDisconnection(ConnectionChannel channel) {
        gameSettings.kickDisconnectedPlayer(channel.getConnectionId());
        updateSettings(gameSettings);
    }


    public void updateSettings(GameSettings settings) {
        gameSettings = settings;
        removeKickedPlayersFromGameController();
        addAIPlayersToGameController();
        lobbyEventBus.emit(new SettingsUpdated(settings));
        broadcastEvent(new SettingsUpdated(settings));
    }

    private void removeKickedPlayersFromGameController() {
        List<Player> playersToKick = new ArrayList<>();

        for (Player player : gameController.getPlayers()) {
            boolean playerKicked = gameSettings.players
                .stream()
                .filter(p -> p.playerId == player.getId())
                .count() != 1;

            if (playerKicked) {
                playersToKick.add(player);

                if (player instanceof NetworkPlayer) {
                    connectionInterface.disconnect(((NetworkPlayer) player).getConnectionId());
                }
            }
        }
        gameController.getPlayers().removeAll(playersToKick);
    }

    private void addAIPlayersToGameController() {
        for (PlayerSlot slot : gameSettings.getNotAppliedAIPlayerSlots()) {
            AIPlayer player = (AIPlayer) gameController.applyPlayer(new AIPlayer());
            player.setDisplayName("AI Player " + player.getId());
            gameSettings.setPlayerOnSlot(slot, player);
        }
    }

    private void broadcastEvent(Event<?> event) {
        if (lobbyType != LobbyType.OFFLINE) {
            externalNetworkEventBus.emit(event);
        }
    }

    private void emitLobbyException(Exception e) {
        lobbyEventBus.emit(new ExceptionThrown(e));
    }
}
