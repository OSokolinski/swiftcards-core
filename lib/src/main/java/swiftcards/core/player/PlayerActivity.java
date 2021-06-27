package swiftcards.core.player;

import swiftcards.core.card.Card;
import swiftcards.core.card.CardColor;
import swiftcards.core.game.ActivityBase;

public abstract class PlayerActivity<T> extends ActivityBase<T> {

    public PlayerActivity(T subject) {
        super(subject);
    }

}
