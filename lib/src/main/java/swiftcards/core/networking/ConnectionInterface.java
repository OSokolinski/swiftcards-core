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
    
    public ConnectionInterface() {
        connections = new ArrayList<>();
        listeningRunnable = new ConnectionListeningRunnable(7931);
    }

    public void runListener() {

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

        if (instance == null) {
            instance = new ConnectionInterface();
        }

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

}
