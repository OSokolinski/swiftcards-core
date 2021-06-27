package swiftcards.core.networking.event.ingame;

import swiftcards.core.card.CardColor;
import swiftcards.core.networking.NetworkEventBase;

public class ColorSelected extends NetworkEventBase<CardColor> {
    public ColorSelected(CardColor color) {
        super(color);
    }
}
