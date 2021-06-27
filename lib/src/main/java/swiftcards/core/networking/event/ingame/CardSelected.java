package swiftcards.core.networking.event.ingame;

import swiftcards.core.card.Card;
import swiftcards.core.util.EventBase;

public class CardSelected extends EventBase<Card> {
    public CardSelected(Card card) {
        super(card);
    }
}
