package swiftcards.core.networking;

import swiftcards.core.util.EventBase;

import java.util.Date;

public abstract class NetworkEventBase<T> extends EventBase<T> {

    protected NetworkEventBase() {
        timeStamp = new Date();
    }

    protected NetworkEventBase(T data) {
        this();
        eventData = data;
    }
}