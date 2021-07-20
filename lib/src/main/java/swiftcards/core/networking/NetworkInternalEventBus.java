package swiftcards.core.networking;

import swiftcards.core.networking.event.DataReceived;
import swiftcards.core.networking.event.IncomingEvent;
import swiftcards.core.networking.event.PlayerJoined;
import swiftcards.core.player.NetworkPlayer;
import swiftcards.core.player.Player;
import swiftcards.core.util.Event;
import swiftcards.core.util.EventBusBase;
import swiftcards.core.util.Subscriber.*;


public class NetworkInternalEventBus extends EventBusBase {

    private static NetworkInternalEventBus instance = null;

    public NetworkInternalEventBus() {
        super();
        on(DataReceived.class, onDataReceived);
    }

    public static NetworkInternalEventBus getInstance() {
        if (instance == null) {
            instance = new NetworkInternalEventBus();
        }

        return instance;
    }

    BasicConsumer<ChannelIncomingData> onDataReceived = (dataObject) -> {

        if (dataObject.getData() instanceof PlayerCredentials) {

            PlayerCredentials credentials = (PlayerCredentials) dataObject.getData();

            NetworkPlayer player = new NetworkPlayer(dataObject.getConnectionId());
            player.setDisplayName(credentials.getDisplayName());

            emit(new PlayerJoined(player));
        }

        else if (dataObject.getData() instanceof Event) {
            emit(new ExternalEventEmitted(new IncomingEvent((Event<?>) dataObject.getData(), dataObject.getConnectionId())));
        }
    };
}
