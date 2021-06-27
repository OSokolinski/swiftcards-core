package swiftcards.core.player.activities;

import swiftcards.core.card.Card;
import swiftcards.core.player.PlayerActivity;

public class CardLiedOnTable extends PlayerActivity<CardLiedOnTable.CardLiedByPlayer> {
    public CardLiedOnTable(Card card, int playerId) {
        super(new CardLiedByPlayer(card, playerId));
    }

    public static class CardLiedByPlayer {

        Card card;
        int playerId;

        public CardLiedByPlayer(Card liedCard, int activePlayerId) {
            card = liedCard;
            playerId = activePlayerId;
        }

        public Card getCard() {
            return card;
        }

        public int getPlayerId() {
            return playerId;
        }
    }
}
