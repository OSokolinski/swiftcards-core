package swiftcards.core.networking;

import swiftcards.core.networking.event.IncomingEvent;

public class ExternalEventEmitted extends NetworkEventBase<IncomingEvent> {
    public ExternalEventEmitted(IncomingEvent incomingEvent) {
        super(incomingEvent);
    }
}
