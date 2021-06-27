package swiftcards.core.networking;

import swiftcards.core.game.Activity;
import swiftcards.core.game.ActivityPropagator;

public class NetworkActivityPropagator implements ActivityPropagator {

    @Override
    public void propagate(Activity<?> activity) {
        ConnectionInterface.getInstance().sendToAll(activity);
    }
}
