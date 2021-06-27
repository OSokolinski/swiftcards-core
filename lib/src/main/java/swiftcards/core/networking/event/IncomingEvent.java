package swiftcards.core.networking.event;

import swiftcards.core.util.Event;

public class IncomingEvent {

    private final Event<?> event;
    private final int connectionId;

    public IncomingEvent(Event<?> e, int connId) {
        event = e;
        connectionId = connId;
    }

    public Event<?> getEvent() {
        return event;
    }

    public int getConnectionId() {
        return connectionId;
    }
}
