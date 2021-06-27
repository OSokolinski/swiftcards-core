package swiftcards.core.card;

public class FunctionalPlusFourCard extends FunctionalCardBase implements FunctionalCard, ColorChangingCard, PenaltyPullingCard {

    private int cardToPullIncrease;

    @Override
    public int getCardToPullAmount() {
        return 4 + cardToPullIncrease;
    }

    @Override
    public void chooseColor(CardColor color) {
        cardColor = color;
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

        if (isAffective) {
            return card instanceof FunctionalPlusFourCard;
        }
        else {

            if (card instanceof FunctionalPlusFourCard || card instanceof FunctionalSwitchColorCard) {
                return true;
            }
            else return cardColor == card.getCardColor();
        }
    }
}
