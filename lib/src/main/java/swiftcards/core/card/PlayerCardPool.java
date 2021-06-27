package swiftcards.core.card;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerCardPool implements CardPool {

    List<Card> cards;

    /**
     * Initiating player card pool
     */
    public PlayerCardPool() {
        cards = new LinkedList<>();
    }

    @Override
    public List<Card> getCards() {
        return cards;
    }

    /**
     * @param liedCard Card, that should be covered
     * @return List of cards, that might cover the card from the parameter
     */
    public List<Card> pullMatchingCards(Card liedCard) {

        return cards
            .stream()
            .filter(liedCard::canBeCoveredByCard)
            .collect(Collectors.toList());
    }

    /**
     * @param cardToRemove Card, that should be removed from the card pool of the player
     */
    public void removeCardFromPool(Card cardToRemove) {

        int index = -1;
        int iterator = 0;

        for (Card c : cards) {
            if (c.getId() == cardToRemove.getId()) {
                index = iterator;
                break;
            }

            iterator++;
        }

        if (index > -1) {
            cards.remove(index);
        }
    }
}
