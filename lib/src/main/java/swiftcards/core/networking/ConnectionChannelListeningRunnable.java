package swiftcards.core.networking;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.function.Consumer;

public final class ConnectionChannelListeningRunnable extends ConnectionChannelRunnable implements Runnable {

    private final Consumer<Object> onObjectReceived;
    private final ObjectInputStream inputStream;

    public ConnectionChannelListeningRunnable(Socket connectionSocket, Consumer<Object> messageReceivedLambda) throws IOException {
        super(connectionSocket);
        onObjectReceived = messageReceivedLambda;
        inputStream = new ObjectInputStream(socket.getInputStream());
    }


    @Override
    public void run() {

        while (!isStopped) {

            try {
                receiveObject(inputStream.readObject());
            }
            catch (IOException|ClassNotFoundException e) {
                System.out.printf("[%d] Error", (new Date()).getTime());
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
