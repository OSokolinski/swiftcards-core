package swiftcards.core.networking.event;

import swiftcards.core.networking.NetworkEventBase;

import java.net.Socket;

public class SocketAccepted extends NetworkEventBase<Socket> {
    public SocketAccepted(Socket s) {
        super(s);
    }
}
