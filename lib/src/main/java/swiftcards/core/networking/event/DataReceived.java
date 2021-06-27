package swiftcards.core.networking.event;

import swiftcards.core.networking.ChannelIncomingData;
import swiftcards.core.networking.NetworkEventBase;

public class DataReceived extends NetworkEventBase<ChannelIncomingData> {
    public DataReceived(ChannelIncomingData data) {
        super(data);
    }
}
