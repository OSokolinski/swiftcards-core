package swiftcards.core.card;

public abstract class FunctionalCardBase extends CardBase implements FunctionalCard {

    protected boolean isAffective;

    /**
     * Initiates card and sets to not affective by default
     */
    public FunctionalCardBase() {
        isAffective = true;
    }

    @Override
    public void setIsAffective(boolean affective) {
        isAffective = affective;
    }

    @Override
    public boolean isAffective() {
        return isAffective;
    }

    @Override
    public boolean canBeCoveredByCard(Card card) {

        if (isAffective) return false;
        else {
            return cardColor == card.getCardColor()
                || card.getClass().equals(getClass())
                || card instanceof FunctionalPlusFourCard
                || card instanceof FunctionalSwitchColorCard;
        }
    }
}
