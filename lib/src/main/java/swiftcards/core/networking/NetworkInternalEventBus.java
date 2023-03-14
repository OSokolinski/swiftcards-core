package swiftcards.core.networking;

import swiftcards.core.networking.event.DataReceived;
import swiftcards.core.networking.event.IncomingEvent;
import swiftcards.core.util.Event;
import swiftcards.core.util.DefaultEventBus;
import swiftcards.core.util.Subscriber.*;


public class NetworkInternalEventBus extends DefaultEventBus {

    public NetworkInternalEventBus() {
        super();
        on(DataReceived.class, onDataReceived);
    }

    BasicConsumer<ChannelIncomingData> onDataReceived = (dataObject) -> {
        if (dataObject.getData() instanceof Event) {
            emit(new ExternalEventEmitted(new IncomingEvent((Event<?>) dataObject.getData(), dataObject.getConnectionId())));
        }
    };
}
