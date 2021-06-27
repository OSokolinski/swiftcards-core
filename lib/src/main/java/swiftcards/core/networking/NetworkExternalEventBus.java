package swiftcards.core.networking;

import swiftcards.core.util.Event;
import swiftcards.core.util.EventBusBase;

public class NetworkExternalEventBus extends EventBusBase {


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

        return (NetworkExternalEventBus) instance;
    }

}
