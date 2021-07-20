package swiftcards.core.player;

import swiftcards.core.card.Card;
import swiftcards.core.card.CardColor;

import java.util.List;

public interface PlayerPrompter {

    /**
     * Showing player a card, that is on the table
     *
     * @param cardOnTable Card on table
     */
    void showCardOnTable(Card cardOnTable);

    /**
     * Showing player his cards, and marking those ones, that he can select in the pending turn
     *
     * @param eligibleCardIds Card numbers, that the player can select
     */
    void showPlayerCards(final List<Integer> eligibleCardIds);

    /**
     * Prompting the player to make a turn and select a card from his pool
     *
     * @return Selected card
     */
    Card selectCard();

    /**
     * Prompting the player to choose the card color
     *
     * @return Card color
     */
    CardColor selectCardColor();

    /**
     * Refreshing player cards. Cards are going to be cached and shown by another method
     *
     * @param cards New player card pool
     */
    void refreshCards(List<Card> cards);
}
