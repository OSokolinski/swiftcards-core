package swiftcards.core.networking;

import swiftcards.core.networking.event.ServerCannotNotStart;
import swiftcards.core.networking.event.ServerListenerStarted;
import swiftcards.core.networking.event.SocketAccepted;
import swiftcards.core.networking.event.SocketCouldNotBeAccepted;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionListeningRunnable implements Runnable {

    private final int port;
    private volatile boolean keepWaitingForConnections;
    private ServerSocket serverSocket;

    public ConnectionListeningRunnable(int listeningPort) {
        port = listeningPort;
        keepWaitingForConnections = true;
    }

    @Override
    public void run()  {

        try {
            serverSocket = new ServerSocket(port);

            NetworkInternalEventBus.getInstance().emit(new ServerListenerStarted());

            while (keepWaitingForConnections) {
                Socket acceptedSocket = null;

                try {
                    acceptedSocket = serverSocket.accept();
                }
                catch (IOException e0) {
                    NetworkInternalEventBus.getInstance().emit(new SocketCouldNotBeAccepted(e0));
                }

                if (acceptedSocket != null) {
                    NetworkInternalEventBus.getInstance().emit(new SocketAccepted(acceptedSocket));
                }
            }


        }
        catch (IOException e1) {
            NetworkInternalEventBus.getInstance().emit(new ServerCannotNotStart(e1));
        }

    }

    public void stopWaitingForConnections() {
        keepWaitingForConnections = false;

        try {
            serverSocket.close();
        }
        catch (IOException e) {
            System.out.println("Server socket closing error: %s" + e);
        }
    }

}
