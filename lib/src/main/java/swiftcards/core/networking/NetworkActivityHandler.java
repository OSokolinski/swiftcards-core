package swiftcards.core.networking;

import swiftcards.core.game.Activity;
import swiftcards.core.networking.event.DataReceived;
import swiftcards.core.util.Subscriber;

public class NetworkActivityHandler {

    Subscriber<ChannelIncomingData> onActivityReceived;

    public NetworkActivityHandler() {

        onActivityReceived = new Subscriber<>((rvdData) -> {

            if (!(rvdData.getData() instanceof Activity)) {
                return;
            }


        });


        NetworkInternalEventBus.getInstance().on(DataReceived.class, onActivityReceived);
    }
}
