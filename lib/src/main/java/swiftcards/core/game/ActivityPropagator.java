package swiftcards.core.game;

import swiftcards.core.networking.ConnectionInterface;
import swiftcards.core.player.activities.CardLiedOnTable;
import swiftcards.core.util.ConfigService;

public class ActivityPropagator {

    private final ConnectionInterface connectionInterface;
    private ActivityHandler localActivityHandler = null;

    public ActivityPropagator(boolean propagateInUI, ConnectionInterface connectionInterface) {
        this.connectionInterface = connectionInterface;
        if (propagateInUI) {
            localActivityHandler = new ActivityHandler();
        }
    }

    void propagate(Activity<?> activity) {
        if (connectionInterface != null) {
            connectionInterface.sendToAll(activity);
        }
        if (localActivityHandler != null) {
           localActivityHandler.handle(activity);
        }

        try {
            Thread.sleep(ConfigService.getInstance().getActivityDelayTime());
        }
        catch (Exception e) {
            System.out.println("Sleep error: " + e);
        }
    }
}
