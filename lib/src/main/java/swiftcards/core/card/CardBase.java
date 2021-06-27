package swiftcards.core.card;

public abstract class CardBase implements Cloneable, Card {

    protected CardColor cardColor;
    protected int id = 0;


    @Override
    public CardColor getCardColor() {
        return cardColor;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public final Card clone() throws CloneNotSupportedException {
        return (Card) super.clone();
    }

    @Override
    public final Card apply(int newId) {
        if (id == 0) {
            id = newId;
        }

        return this;
    }

    /**
     * @return String representation of the card.
     */
    public String toString() {
        if (this instanceof StandardCard) {
            return String.format("[StandardCard %s %d]", cardColor.toString(), ((StandardCard) this).getCardPresentation());
        }
        else if (this instanceof ColorChangingCard) {
            return String.format("[%s]", getClass().getSimpleName());
        }
        else {
            return String.format("[%s %s]", getClass().getSimpleName(), getCardColor());
        }

    }
}
