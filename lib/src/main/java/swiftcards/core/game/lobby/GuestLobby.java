package swiftcards.core.game.lobby;

import swiftcards.core.networking.*;
import swiftcards.core.networking.event.ChannelDisconnected;
import swiftcards.core.networking.event.ExceptionThrown;
import swiftcards.core.networking.event.lobby.ConnectedSuccessfully;
import swiftcards.core.networking.event.IncomingEvent;
import swiftcards.core.networking.event.ingame.GuestHandshake;
import swiftcards.core.networking.event.ingame.HostHandshake;
import swiftcards.core.networking.event.lobby.GameStarted;
import swiftcards.core.networking.event.lobby.ReadinessToggled;
import swiftcards.core.networking.event.lobby.SettingsUpdated;
import swiftcards.core.player.NetworkPlayerPrompterHandler;
import swiftcards.core.util.*;

public class GuestLobby extends Freezable implements Lobby {

    private final Subscriber<IncomingEvent> onIncomingEventSubscriber;
    private final Subscriber<ConnectionChannel> onDisconnectionSubscriber;
    private final PlayerCredentials handshakeCredentials;

    private Integer port;
    private String ipAddress;
    private ConnectionInterface connectionInterface;
    private final NetworkInternalEventBus internalNetworkEventBus;
    private NetworkExternalEventBus externalNetworkEventBus;

    private final DefaultEventBus lobbyEventBus;

    public GuestLobby(PlayerCredentials playerCredentials, String ipAddress, Integer port) {
        this.port = port;
        this.ipAddress = ipAddress;

        handshakeCredentials = playerCredentials;
        internalNetworkEventBus = new NetworkInternalEventBus();
        onIncomingEventSubscriber = new Subscriber<>(this::onIncomingEvent);
        onDisconnectionSubscriber = new Subscriber<>(this::passDisconnectionInfo);
        lobbyEventBus = new DefaultEventBus();
    }

    @Override
    public void prepare() {
        try {
            ConfigService.getInstance().log("Establishing connection...");
            connectionInterface = ConnectionInterface.initAsClient(ipAddress, port, internalNetworkEventBus);
        }
        catch (Exception e) {
            lobbyEventBus.emit(new ExceptionThrown(e));
        }

        externalNetworkEventBus = new NetworkExternalEventBus(connectionInterface);
        internalNetworkEventBus.on(ExternalEventEmitted.class, onIncomingEventSubscriber);
        internalNetworkEventBus.on(ChannelDisconnected.class, onDisconnectionSubscriber);
        externalNetworkEventBus.emit(new GuestHandshake(handshakeCredentials));
        freeze();

        lobbyEventBus.emit(new ConnectedSuccessfully());
    }

    @Override
    public EventBus getEventBus() {
        return lobbyEventBus;
    }

    @Override
    public void close() {
        internalNetworkEventBus.unsubscribe(ExternalEventEmitted.class, onIncomingEventSubscriber);
        lobbyEventBus.clear();
        externalNetworkEventBus.clear();
    }

    @Override
    public ConnectionInterface getConnectionInterface() {
        return connectionInterface;
    }

    @Override
    public void disconnect() {
        close();
        connectionInterface.disconnect(0);
        internalNetworkEventBus.clear();
    }

    public NetworkActivityHandler createNetworkActivityHandler() {
        return new NetworkActivityHandler(internalNetworkEventBus);
    }

    public NetworkPlayerPrompterHandler createNetworkPlayerPrompterHandler() {
        return new NetworkPlayerPrompterHandler(internalNetworkEventBus, externalNetworkEventBus);
    }

    public void toggleIsReady() {
        externalNetworkEventBus.emit(new ReadinessToggled());
    }

    private void onIncomingEvent(IncomingEvent event) {
        if (event.getEvent() instanceof HostHandshake) {
            ConfigService.getInstance().log("Joined to lobby! Getting lobby settings...");
            resume();
        }
        else if (event.getEvent() instanceof SettingsUpdated) {
            ConfigService.getInstance().log("Received updated lobby settings");
            lobbyEventBus.emit(new SettingsUpdated((GameSettings) event.getEvent().getEventData()));
        }
        else if (event.getEvent() instanceof GameStarted) {
            ConfigService.getInstance().log("Host started the game");
            lobbyEventBus.emit(event.getEvent());
        }
    }

    private void passDisconnectionInfo(ConnectionChannel channel) {
        lobbyEventBus.emit(new ChannelDisconnected(channel));
    }
}
