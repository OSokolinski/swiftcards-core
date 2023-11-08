package swiftcards.core.networking;

import swiftcards.core.networking.event.ChannelDisconnected;
import swiftcards.core.networking.event.SocketAccepted;
import swiftcards.core.util.ApplicationSettings;
import swiftcards.core.util.ConfigService;
import swiftcards.core.util.Logger;
import swiftcards.core.util.Subscriber;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnectionInterface {

    private final List<ConnectionChannel> connections;
    private final ConnectionListeningRunnable listeningRunnable;
    private final boolean clientMode;

    private final NetworkInternalEventBus networkInternalEventBus;

    private ConnectionInterface(boolean asClient, int port, NetworkInternalEventBus networkInternalEventBus) {
        connections = new ArrayList<>();
        clientMode = asClient;
        listeningRunnable = new ConnectionListeningRunnable(port, networkInternalEventBus);
        this.networkInternalEventBus = networkInternalEventBus;

        this.networkInternalEventBus.on(ChannelDisconnected.class, new Subscriber<>(connections::remove));
    }

    public static ConnectionInterface initAsClient(String address, int port, NetworkInternalEventBus networkInternalEventBus) throws IOException {
        ConnectionInterface instance = new ConnectionInterface(true, port, networkInternalEventBus);

        ConnectionChannel channel = ConnectionChannel.connect(address, port, networkInternalEventBus);
        instance.connections.add(channel);

        return instance;
    }

    public static ConnectionInterface initAsServer(int port, NetworkInternalEventBus networkInternalEventBus) {
        return new ConnectionInterface(false, port, networkInternalEventBus);
    }

    public void runListener() throws ConnectionListenerNotAllowedException {

        if (clientMode) {
            throw new ConnectionListenerNotAllowedException();
        }

        networkInternalEventBus.on(SocketAccepted.class, new Subscriber<>(
            (Socket s) -> {
                try {
                    synchronized (connections) {
                        connections.add(ConnectionChannel.accept(s, connections.size(), networkInternalEventBus));
                    }

                }
                catch (IOException e) {
                    ConfigService.getInstance().logError("Network Socket Error. Could not accept client connection: %s", e);
                }

                ConfigService.getInstance().log("Client successfully connected");
            }
        ));

        new Thread(listeningRunnable).start();
    }


    public List<ConnectionChannel> getConnections() {
        return connections;
    }


    public void sendTo(int connectionId, Object dataToSend) {
        connections.get(connectionId).sendObject(dataToSend);
    }

    public synchronized void sendToAll(Object dataToSend) {
        synchronized (connections) {
            for (ConnectionChannel channel : connections) {
                channel.sendObject(dataToSend);
            }
        }
    }

    public void disconnect(int connectionId) {
        try {
            connections.get(connectionId).close();
            ConfigService.getInstance().log("Connection ID: %d ended by force", connectionId);
        }
        catch (Exception e) {
            ConfigService.getInstance().logError("Unable to disconnect: %s", e);
        }
        synchronized (connections) {
            if (connections.get(connectionId) != null) {
                connections.remove(connectionId);
            }
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
