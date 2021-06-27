package swiftcards.core.card;

public interface PenaltyPullingCard {

    /**
     * @return Amount of cards, that player has to pull
     */
    int getCardToPullAmount();

    /**
     * @param value Value, that will be added to card-to-take amount
     */
    void increaseCardsToPullAmount(int value);

    /**
     * Resetting card-to-take amount to its default value
     */
    void resetCardsToPullAmount();
}
