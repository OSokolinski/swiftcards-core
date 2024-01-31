package swiftcards.core.networking;

import swiftcards.core.networking.event.ExceptionThrown;
import swiftcards.core.networking.event.ServerListenerStarted;
import swiftcards.core.networking.event.SocketAccepted;
import swiftcards.core.util.ConfigService;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionListeningRunnable implements Runnable {

    private final int port;
    private volatile boolean keepWaitingForConnections;
    private ServerSocket serverSocket;
    private final NetworkInternalEventBus networkInternalEventBus;

    public ConnectionListeningRunnable(int listeningPort, NetworkInternalEventBus networkInternalEventBus) {
        port = listeningPort;
        keepWaitingForConnections = true;
        this.networkInternalEventBus = networkInternalEventBus;
    }

    @Override
    public void run()  {

        try {
            serverSocket = new ServerSocket(port);

            networkInternalEventBus.emit(new ServerListenerStarted());

            while (keepWaitingForConnections) {
                Socket acceptedSocket = serverSocket.accept();

                if (acceptedSocket != null) {
                    networkInternalEventBus.emit(new SocketAccepted(acceptedSocket));
                }
            }
        }
        catch (Exception e) {
            if (!keepWaitingForConnections && e.getMessage().equals("Socket closed")) {
                return;
            }
            ConfigService.getInstance().logError("Exception while waiting for clients: %s", e.getMessage());
            networkInternalEventBus.emit(new ExceptionThrown(e));
        }

    }

    public void stopWaitingForConnections() {
        keepWaitingForConnections = false;

        try {
            serverSocket.close();
        }
        catch (IOException e) {
            ConfigService.getInstance().logError("Server socket closing error: %s", e.getMessage());
            networkInternalEventBus.emit(new ExceptionThrown(e));
        }
    }

}
