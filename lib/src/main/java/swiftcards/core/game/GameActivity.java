package swiftcards.core.game;

import swiftcards.core.card.Card;

public abstract class GameActivity<T> extends ActivityBase<T> {

    public GameActivity(T subject) {
        super(subject);
    }

}
