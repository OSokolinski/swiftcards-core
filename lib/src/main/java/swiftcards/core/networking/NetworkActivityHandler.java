package swiftcards.core.networking;

import swiftcards.core.game.Activity;
import swiftcards.core.game.ActivityHandler;
import swiftcards.core.networking.event.DataReceived;
import swiftcards.core.util.Subscriber;

public class NetworkActivityHandler {

    private final Subscriber<ChannelIncomingData> onActivityReceived;
    private final NetworkInternalEventBus networkInternalEventBus;
    private final ActivityHandler localActivityHandler;

    public void stop() {
        networkInternalEventBus.unsubscribe(DataReceived.class, onActivityReceived);
    }

    public NetworkActivityHandler(NetworkInternalEventBus networkInternalEventBus) {
        this.networkInternalEventBus = networkInternalEventBus;
        localActivityHandler = new ActivityHandler();

        onActivityReceived = new Subscriber<>((rvdData) -> {

            if (!(rvdData.getData() instanceof Activity)) {
                return;
            }
            Activity<?> activity = (Activity<?>) rvdData.getData();
            localActivityHandler.handle(activity);
        });

        this.networkInternalEventBus.on(DataReceived.class, onActivityReceived);
    }
}
