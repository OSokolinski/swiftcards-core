package swiftcards.core.game;

import swiftcards.core.networking.ConnectionInterface;

public class ActivityPropagator {

    private final boolean uIPropagation;
    private final ConnectionInterface connectionInterface;

    public ActivityPropagator(boolean propagateInUI, ConnectionInterface connectionInterface) {
        this.connectionInterface = connectionInterface;
        uIPropagation = propagateInUI;
    }

    void propagate(Activity<?> activity) {

        if (connectionInterface != null) {
            connectionInterface.sendToAll(activity);
        }
        if (uIPropagation) {

        }
    }
}
