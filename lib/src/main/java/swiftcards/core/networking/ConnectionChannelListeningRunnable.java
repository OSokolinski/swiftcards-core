package swiftcards.core.networking;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public final class ConnectionChannelListeningRunnable extends ConnectionChannelRunnable implements Runnable {

    private final Consumer<Object> onObjectReceived;

    public ConnectionChannelListeningRunnable(Socket connectionSocket, Consumer<Object> messageReceivedLambda) throws IOException {
        super(connectionSocket);
        onObjectReceived = messageReceivedLambda;
    }

    @Override
    public void run() {

        while (!isStopped) {

            try {
                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                receiveObject(inputStream.readObject());
            }
            catch (IOException|ClassNotFoundException e) {
                if (onExceptionIssued != null) {
                    onExceptionIssued.accept(e);
                }
            }

        }
    }

    private void receiveObject(Object receivedObject) {
        onObjectReceived.accept(receivedObject);
    }

}
