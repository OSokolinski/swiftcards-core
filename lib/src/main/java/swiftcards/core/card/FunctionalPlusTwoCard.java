package swiftcards.core.card;

public class FunctionalPlusTwoCard extends FunctionalCardBase implements FunctionalCard, PenaltyPullingCard {

    private int cardToPullIncrease;

    /**
     * Initiates plus-two card.
     *
     * @param color Color of a card
     */
    public FunctionalPlusTwoCard(CardColor color) {
        super();
        cardColor = color;
        cardToPullIncrease = 0;
    }

    @Override
    public int getCardToPullAmount() {
        return 2 + cardToPullIncrease;
    }

    @Override
    public void increaseCardsToPullAmount(int value) {
        cardToPullIncrease += value;
    }

    @Override
    public void resetCardsToPullAmount() {
        cardToPullIncrease = 0;
    }

    @Override
    public boolean canBeCoveredByCard(Card card) {

        if (isAffective) return card instanceof FunctionalPlusTwoCard;
        else {
            return cardColor == card.getCardColor()
                || card.getClass().equals(getClass())
                || card instanceof FunctionalPlusFourCard
                || card instanceof FunctionalSwitchColorCard;
        }
    }
}
