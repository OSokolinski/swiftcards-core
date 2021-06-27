package swiftcards.core.card;

public class StandardCard extends CardBase {

    private final int cardPresentation;

    /**
     * Initiating standard card.
     *
     * @param color Color of the card
     * @param presentation Number presentation of the card
     */
    public StandardCard(CardColor color, int presentation) {
        cardColor = color;
        cardPresentation = presentation;
    }

    /**
     * @return Numeric card representation
     */
    public int getCardPresentation() {
        return cardPresentation;
    }

    @Override
    public boolean canBeCoveredByCard(Card card) {

        if (card instanceof StandardCard) {
            return ((StandardCard) card).getCardPresentation() == cardPresentation || card.getCardColor() == cardColor;
        }
        else if (card instanceof FunctionalCard) {

            if (!(card instanceof FunctionalSwitchColorCard) && !(card instanceof FunctionalPlusFourCard)) {
                return card.getCardColor() == cardColor;
            }
            else return true;
        }

        return false;
    }
}
