package swiftcards.core.networking;

import java.net.Socket;
import java.util.function.Consumer;

public abstract class ConnectionChannelRunnable {

    protected volatile boolean isStopped = false;
    protected volatile Socket socket;
    protected Consumer<Exception> onExceptionIssued = null;

    protected ConnectionChannelRunnable(Socket connectionSocket) {
        socket = connectionSocket;
    }

    public void stop() {
        isStopped = true;
    }

    public void onNetworkExceptionIssued(Consumer<Exception> handler) {
        onExceptionIssued = handler;
    }
}
