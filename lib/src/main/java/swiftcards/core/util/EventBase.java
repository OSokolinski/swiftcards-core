package swiftcards.core.util;

import java.util.Date;

public abstract class EventBase<T> implements Event<T> {

    protected Date timeStamp;
    protected T eventData;

    protected EventBase() {
        timeStamp = new Date();
    }

    protected EventBase(T data) {
        this();
        eventData = data;
    }

    public T getEventData() {
        return eventData;
    }
}
