package swiftcards.core.player.activities;

import swiftcards.core.card.CardColor;
import swiftcards.core.player.PlayerActivity;

public class CardColorChosen extends PlayerActivity<CardColor> {
    public CardColorChosen(CardColor cardColor) {
        super(cardColor);
    }
}
