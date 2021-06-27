package swiftcards.core.networking;

import swiftcards.core.util.EventBase;

import java.util.Date;

public abstract class NetworkEventBase<T> extends EventBase<T> {

    protected int connectionId = 0;

    protected NetworkEventBase() {
        timeStamp = new Date();
    }

    protected NetworkEventBase(T data) {
        this();
        eventData = data;
    }

    protected NetworkEventBase(int networkConnectionId, T data) {
        this();
        connectionId = networkConnectionId;
    }
}
