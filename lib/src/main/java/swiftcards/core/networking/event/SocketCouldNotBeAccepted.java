package swiftcards.core.networking.event;

import swiftcards.core.networking.NetworkEventBase;

public class SocketCouldNotBeAccepted extends NetworkEventBase<Exception> {
    public SocketCouldNotBeAccepted(Exception e) {
        super(e);
    }
}
