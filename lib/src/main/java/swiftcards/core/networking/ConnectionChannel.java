package swiftcards.core.networking;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import swiftcards.core.networking.event.ChannelDisconnected;
import swiftcards.core.networking.event.DataReceived;
import swiftcards.core.util.ConfigService;
import swiftcards.core.util.Event;

public class ConnectionChannel {

    private String remoteIPAddress;
    private final Socket socket;
    private Thread listeningThread;
    private ConnectionChannelListeningRunnable listeningRunnable;
    private int connectionId;
    private boolean isReady = false;
    private boolean serverMode = false;
    private ObjectOutputStream objectOutputStream;
    private NetworkInternalEventBus networkInternalEventBus;

    private ConnectionChannel(Socket connectionSocket, NetworkInternalEventBus networkInternalEventBus) throws IOException {
        socket = connectionSocket;
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        listeningRunnable = new ConnectionChannelListeningRunnable(socket, this::handleReceivedObject);
        this.networkInternalEventBus = networkInternalEventBus;

        listeningRunnable.onNetworkExceptionIssued(this::handleListeningError);

    }

    public static ConnectionChannel accept(Socket acceptedSocket, int connectionId, NetworkInternalEventBus networkInternalEventBus) throws IOException {
        ConnectionChannel connectionChannel = new ConnectionChannel(acceptedSocket, networkInternalEventBus);
        connectionChannel.runConnectionThreads();
        connectionChannel.connectionId = connectionId;
        connectionChannel.serverMode = true;

        return connectionChannel;
    }

    public static ConnectionChannel connect(String ipAddress, int port, NetworkInternalEventBus networkInternalEventBus) throws IOException {
        ConnectionChannel connectionChannel = new ConnectionChannel(new Socket(ipAddress, port), networkInternalEventBus);
        connectionChannel.runConnectionThreads();

        return connectionChannel;
    }

    // TODO Subscribe
    public void close() throws IOException {

        listeningRunnable.stop();
        socket.close();
    }

    public int getConnectionId() {
        return connectionId;
    }

    public void dispatchEvent(Event<?> event) {

    }

    public void sendObject(Object objectToSend) {

        try {
            objectOutputStream.writeObject(objectToSend);
            objectOutputStream.flush();
            objectOutputStream.reset();
        }
        catch (IOException e) {
            handleSendingError(e);
        }

    }

    private void runConnectionThreads() {

        listeningThread = new Thread(listeningRunnable);
        listeningThread.start();
    }

    private void handleSendingError(Exception e) {
        if (e instanceof SocketException &&
            (e.getMessage().equals("Socket closed") || e.getMessage().equals("Connection reset"))
        ) {

            try {
                close();
            }
            catch (IOException ioe) {
                System.out.println("Could not exit connection channel: " + ioe);
            }

            System.out.printf("Connection ID: %d closed.%n", connectionId);
            return;
        }

        System.out.println("Exception while sending data over network: " + e);
    }

    private void handleListeningError(Exception e) {

        ConfigService.getInstance().logError("Exception while looking forward for a data: " + e);

        if (e instanceof SocketException &&
            (e.getMessage().equals("Socket closed") || e.getMessage().equals("Connection reset"))
        ) {

            try {
                close();
                ConfigService.getInstance().log("Connection ID: %d properly closed.", connectionId);
            }
            catch (IOException ioe) {
                ConfigService.getInstance().logError("Could not exit connection channel: " + ioe);
            }
        }
        else if (e instanceof EOFException) {
            try {
                close();
                ConfigService.getInstance().log("Connection ID: %d ended by remote.", connectionId);
            }
            catch (IOException ioe) {
                ConfigService.getInstance().logError("Could not exit connection channel: " + ioe);
            }
        }
        networkInternalEventBus.emit(new ChannelDisconnected(this));

    }

    private void handleReceivedObject(Object object) {
        networkInternalEventBus.emit(new DataReceived(new ChannelIncomingData(connectionId, object)));
    }

}
