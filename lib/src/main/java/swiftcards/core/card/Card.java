package swiftcards.core.card;

public interface Card extends Cloneable {

    /**
     * @return ID of the card
     */
    int getId();

    /**
     * Getting color of a card.
     *
     * @return Card color.
     */
    CardColor getCardColor();

    /**
     * Assigning ID to the card.
     *
     * @param newId New card ID
     * @return Card instance
     */
    Card apply(int newId);

    /**
     * @return Cloned card instance, not referring to the original one.
     * @throws CloneNotSupportedException Thrown, when cloning has not succeeded.
     */
    Card clone() throws CloneNotSupportedException;

    /**
     * Checking if this card can be covered by another one.
     *
     * @param card Card candidate.
     * @return True, if it can be covered, false otherwise.
     */
    boolean canBeCoveredByCard(Card card);

}
