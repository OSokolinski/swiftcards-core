package swiftcards.core.networking;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import swiftcards.core.networking.event.DataReceived;
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

    private ConnectionChannel(Socket connectionSocket) throws IOException {
        socket = connectionSocket;
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        listeningRunnable = new ConnectionChannelListeningRunnable(socket, this::handleReceivedObject);

        listeningRunnable.onNetworkExceptionIssued(this::handleListeningError);

    }

    public static ConnectionChannel accept(Socket acceptedSocket, int connectionId) throws IOException {
        ConnectionChannel connectionChannel = new ConnectionChannel(acceptedSocket);
        connectionChannel.runConnectionThreads();
        connectionChannel.connectionId = connectionId;
        connectionChannel.serverMode = true;

        return connectionChannel;
    }

    public static ConnectionChannel connect(String ipAddress, int port) throws IOException {
        ConnectionChannel connectionChannel = new ConnectionChannel(new Socket(ipAddress, port));
        connectionChannel.runConnectionThreads();

        return connectionChannel;
    }

    // TODO Subscribe
    public void close() throws IOException {

        listeningRunnable.stop();
        socket.close();
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

        System.out.println("Exception while looking forward for a data: " + e);
    }

    private void handleReceivedObject(Object object) {
        NetworkInternalEventBus.getInstance().emit(new DataReceived(new ChannelIncomingData(connectionId, object)));
    }

}
