package swiftcards.core.game.activities;

import swiftcards.core.game.GameActivity;
import swiftcards.core.game.Summary;

public class GameFinished extends GameActivity<Summary> {
    public GameFinished(Summary summary) {
        super(summary);
    }
}
