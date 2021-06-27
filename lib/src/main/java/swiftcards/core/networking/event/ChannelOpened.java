package swiftcards.core.networking.event;

import swiftcards.core.networking.ConnectionChannel;
import swiftcards.core.networking.NetworkEventBase;

public class ChannelOpened extends NetworkEventBase<ConnectionChannel> {
    public ChannelOpened(ConnectionChannel channel) {
        super(channel);
    }
}
