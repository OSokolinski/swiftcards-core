package swiftcards.core.networking.event;

import swiftcards.core.networking.ConnectionChannel;
import swiftcards.core.networking.NetworkEventBase;

public class ChannelDisconnected extends NetworkEventBase<ConnectionChannel> {
    public ChannelDisconnected(ConnectionChannel connectionChannel) {
        super(connectionChannel);
    }
}
