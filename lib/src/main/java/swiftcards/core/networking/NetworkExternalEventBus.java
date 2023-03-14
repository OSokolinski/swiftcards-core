package swiftcards.core.networking;

import swiftcards.core.util.Event;
import swiftcards.core.util.DefaultEventBus;

public final class NetworkExternalEventBus extends DefaultEventBus {

    private final ConnectionInterface connectionInterface;

    @Override
    public <T> void emit(Event<T> event) {

        connectionInterface.sendToAll(event);

        super.emit(event);
    }

    public <T> void emit(Event<T> event, int connectionId) {

        connectionInterface.sendTo(connectionId, event);

        super.emit(event);
    }

    public NetworkExternalEventBus(ConnectionInterface connectionInterface) {
        this.connectionInterface = connectionInterface;
    }

}
