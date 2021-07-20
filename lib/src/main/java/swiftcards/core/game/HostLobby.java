package swiftcards.core.game;

import swiftcards.core.networking.ConnectionInterface;
import swiftcards.core.networking.NetworkActivityPropagator;
import swiftcards.core.networking.NetworkInternalEventBus;
import swiftcards.core.networking.event.PlayerJoined;
import swiftcards.core.player.HumanPlayer;
import swiftcards.core.player.Player;
import swiftcards.core.player.PlayerPrompter;
import swiftcards.core.util.Subscriber;

public class HostLobby implements Lobby {

    private LobbyType lobbyType;
    private GameController gameController;
    private GameSettings gameSettings;

    private Subscriber<Player> onPlayerJoined;

    private HostLobby(LobbyType type, GameSettings settings) {
        gameSettings = settings;
        lobbyType = type;
        gameController = new GameController(type != LobbyType.OFFLINE ? new NetworkActivityPropagator() : null);
        gameSettings.setDefaultPlayerConfig(type == LobbyType.ONLINE);
    }

    public static HostLobby createLan(GameSettings settings, Class<? extends PlayerPrompter> playerPrompterClass) throws Exception {
        HostLobby lobby = new HostLobby(LobbyType.LAN, settings);

        lobby.gameController.addPlayer(new HumanPlayer(playerPrompterClass));
        lobby.gameSettings.players[0] = new GameSettings.PlayerSlot(GameSettings.PlayerSlotStatus.HUMAN);

        return lobby;
    }

    public static HostLobby createOnline(GameSettings settings) {
        return new HostLobby(LobbyType.ONLINE, settings);
    }

    public static HostLobby createOffline(GameSettings settings, Class<? extends PlayerPrompter> playerPrompterClass) throws Exception {
        HostLobby lobby = new HostLobby(LobbyType.OFFLINE, settings);

        lobby.gameController.addPlayer(new HumanPlayer(playerPrompterClass));
        lobby.gameSettings.players[0] = new GameSettings.PlayerSlot(GameSettings.PlayerSlotStatus.HUMAN);

        return lobby;
    }



    public void prepare() throws Exception {
        if (lobbyType != LobbyType.OFFLINE) {

            onPlayerJoined = new Subscriber<>(gameController::addPlayer);
            NetworkInternalEventBus.getInstance().on(PlayerJoined.class, onPlayerJoined);

            ConnectionInterface.getInstance().runListener();
        }
    }

    public GameController getGameController() {
        return gameController;
    }

    public void addPlayer(Player player)  {
        gameController.addPlayer(player);
    }

    @Override
    public GameSettings.PlayerSlot[] getPlayerSlots() {
        return gameSettings.players;
    }

}
