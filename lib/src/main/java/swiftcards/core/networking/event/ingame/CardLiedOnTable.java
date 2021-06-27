package swiftcards.core.networking.event.ingame;

import swiftcards.core.card.Card;
import swiftcards.core.networking.NetworkEventBase;

public class CardLiedOnTable extends NetworkEventBase<Card> {
    public CardLiedOnTable(Card card) {
        super(card);
    }
}
