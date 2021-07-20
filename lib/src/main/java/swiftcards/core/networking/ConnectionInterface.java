package swiftcards.core.networking;

import swiftcards.core.networking.event.SocketAccepted;
import swiftcards.core.util.ApplicationSettings;
import swiftcards.core.util.Subscriber;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionInterface {

    private final List<ConnectionChannel> connections;
    private final ConnectionListeningRunnable listeningRunnable;

    private static ConnectionInterface instance;

    private final boolean clientMode;
    
    private ConnectionInterface(boolean asClient, int port) {
        connections = new ArrayList<>();
        clientMode = asClient;
        listeningRunnable = new ConnectionListeningRunnable(port);
    }

    public static void initAsClient(String address, int port) throws IOException {
        instance = new ConnectionInterface(true, port);

        ConnectionChannel channel = ConnectionChannel.connect(address, port);
        instance.connections.add(channel);
    }

    public static void initAsServer(int port) {
        instance = new ConnectionInterface(false, port);
    }

    public void runListener() throws ConnectionListenerNotAllowedException {

        if (clientMode) {
            throw new ConnectionListenerNotAllowedException();
        }

        NetworkInternalEventBus.getInstance().on(SocketAccepted.class, new Subscriber<>(
            (Socket s) -> {

                try {
                    connections.add(ConnectionChannel.accept(s, 0));
                }
                catch (IOException e) {
                    System.out.println("Could not accept socket: " + e);
                }

                System.out.println("Client connected");
            }
        ));

        new Thread(listeningRunnable).start();
    }


    public List<ConnectionChannel> getConnections() {
        return connections;
    }

    public static ConnectionInterface getInstance() {

        // TODO consider throwing ConnectionNotConfiguredException
//        if (instance == null) {
//            instance = new ConnectionInterface();
//        }

        return instance;

    }

    public void sendTo(int connectionId, Object dataToSend) {
        connections.get(connectionId).sendObject(dataToSend);
    }

    public void sendToAll(Object dataToSend) {
        for (ConnectionChannel channel : connections) {
            channel.sendObject(dataToSend);
        }
    }

    public static class ConnectionNotConfiguredException extends Exception {
        public ConnectionNotConfiguredException() {
            super("Connection mode has not been specified.");
        }
    }

    public static class ConnectionListenerNotAllowedException extends Exception {
        public ConnectionListenerNotAllowedException() {
            super("Connection interface must not accept foreign connections while running in client mode.");
        }
    }
}
