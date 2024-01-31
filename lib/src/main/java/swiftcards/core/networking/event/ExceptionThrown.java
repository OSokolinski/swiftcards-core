package swiftcards.core.networking.event;

import swiftcards.core.networking.NetworkEventBase;

public class ExceptionThrown extends NetworkEventBase<Exception> {
    public ExceptionThrown(Exception exception) {
        super(exception);
    }
}
