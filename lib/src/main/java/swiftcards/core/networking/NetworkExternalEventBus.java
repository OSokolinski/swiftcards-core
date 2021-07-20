package swiftcards.core.networking;

import swiftcards.core.util.Event;
import swiftcards.core.util.EventBusBase;

public final class NetworkExternalEventBus extends EventBusBase {

    private static NetworkExternalEventBus instance = null;

    @Override
    public <T> void emit(Event<T> event) {

        ConnectionInterface.getInstance().sendToAll(event);

        super.emit(event);
    }

    public <T> void emit(Event<T> event, int connectionId) {

        ConnectionInterface.getInstance().sendTo(connectionId, event);

        super.emit(event);
    }

    public static NetworkExternalEventBus getInstance() {
        if (instance == null) {
            instance = new NetworkExternalEventBus();
        }

        return instance;
    }

}
