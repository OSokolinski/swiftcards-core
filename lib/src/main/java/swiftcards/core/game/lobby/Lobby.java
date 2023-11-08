package swiftcards.core.game.lobby;

import swiftcards.core.networking.ConnectionInterface;
import swiftcards.core.util.EventBus;

public interface Lobby {

    /**
     * Establishes network connection and sets default lobby behaviour
     *
     */
    void prepare();

    /**
     * @return Event bus for events emitted by lobby
     */
    EventBus getEventBus();

    /**
     * Unsubscribes lobby events
     */
    void close();

    /**
     * Destroy all network connections
     */
    void disconnect();

    ConnectionInterface getConnectionInterface();

    enum LobbyType {
        OFFLINE,
        LAN,
        ONLINE
    }
}
