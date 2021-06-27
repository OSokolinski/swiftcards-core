package swiftcards.core.networking;

public class ChannelIncomingData {

    private final int connectionId;
    private final Object data;

    public ChannelIncomingData(int connId, Object receivedData) {
        connectionId = connId;
        data = receivedData;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public Object getData() {
        return data;
    }
}
