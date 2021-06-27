package swiftcards.core.player;

import swiftcards.core.card.Card;
import swiftcards.core.card.CardColor;

import java.util.List;

public interface PlayerPrompter {

    void showCardOnTable(Card cardOnTable);
    void showPlayerCards(final List<Integer> eligibleCardIds);
    Card selectCard();
    CardColor selectCardColor();
    void refreshCards(List<Card> cards);
}
