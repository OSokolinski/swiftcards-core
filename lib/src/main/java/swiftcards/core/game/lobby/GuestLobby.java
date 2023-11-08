package swiftcards.core.game.lobby;

import swiftcards.core.networking.*;
import swiftcards.core.networking.event.ChannelDisconnected;
import swiftcards.core.networking.event.lobby.ConnectedSuccessfully;
import swiftcards.core.networking.event.IncomingEvent;
import swiftcards.core.networking.event.ingame.GuestHandshake;
import swiftcards.core.networking.event.ingame.HostHandshake;
import swiftcards.core.networking.event.lobby.ReadinessToggled;
import swiftcards.core.networking.event.lobby.SettingsUpdated;
import swiftcards.core.util.*;

public class GuestLobby extends Freezable implements Lobby {

    private final Subscriber<IncomingEvent> onIncomingEventSubscriber;
    private final Subscriber<ConnectionChannel> onDisconnectionSubscriber;
    private final PlayerCredentials handshakeCredentials;

    private Integer port = 56677;
    private ConnectionInterface connectionInterface;
    private final NetworkInternalEventBus internalNetworkEventBus;
    private NetworkExternalEventBus externalNetworkEventBus;

    private final DefaultEventBus lobbyEventBus;

    public GuestLobby(PlayerCredentials playerCredentials) {
        this(playerCredentials, null);
    }

    public GuestLobby(PlayerCredentials playerCredentials, Integer customPort) {
        if (customPort != null) {
            port = customPort;
        }

        handshakeCredentials = playerCredentials;
        internalNetworkEventBus = new NetworkInternalEventBus();
        onIncomingEventSubscriber = new Subscriber<>(this::onIncomingEvent);
        onDisconnectionSubscriber = new Subscriber<>(this::passDisconnectionInfo);
        lobbyEventBus = new DefaultEventBus();
    }

    @Override
    public void prepare() {
        try {
            connectionInterface = ConnectionInterface.initAsClient("127.0.0.1", port, internalNetworkEventBus);
            ConfigService.getInstance().log("Establishing connection...");
        }
        catch (Exception e) {
            // Could not connect to the server
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
    }

    private void passDisconnectionInfo(ConnectionChannel channel) {
        lobbyEventBus.emit(new ChannelDisconnected(channel));
    }
}
