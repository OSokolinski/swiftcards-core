package swiftcards.core.card;

public interface FunctionalCard extends Card {

    /**
     * @return An information, if the card still affects on current player
     */
    boolean isAffective();

    /**
     * @param affective An information if card is still affective
     */
    void setIsAffective(boolean affective);
}
