package swiftcards.core.networking.event.ingame;

import swiftcards.core.card.Card;
import swiftcards.core.networking.NetworkEventBase;

import java.util.List;

public class PlayerCardsRefreshed extends NetworkEventBase<List<Card>> {
    public PlayerCardsRefreshed(List<Card> cards) {
        super(cards);
    }
}
