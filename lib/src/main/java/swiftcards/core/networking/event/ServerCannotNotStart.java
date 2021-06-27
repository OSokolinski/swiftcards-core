package swiftcards.core.networking.event;

import swiftcards.core.networking.NetworkEventBase;

import java.io.IOException;

public class ServerCannotNotStart extends NetworkEventBase<IOException> {
    public ServerCannotNotStart(IOException exception) {
        super(exception);
    }
}
