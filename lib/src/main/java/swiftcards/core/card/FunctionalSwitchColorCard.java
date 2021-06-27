package swiftcards.core.card;

public class FunctionalSwitchColorCard extends FunctionalCardBase implements FunctionalCard, ColorChangingCard {

    @Override
    public void chooseColor(CardColor color) {
        cardColor = color;
    }

    @Override
    public boolean canBeCoveredByCard(Card card) {

        if (card instanceof FunctionalPlusFourCard || card instanceof FunctionalSwitchColorCard) {
            return true;
        }
        else return cardColor == card.getCardColor();
    }
}
