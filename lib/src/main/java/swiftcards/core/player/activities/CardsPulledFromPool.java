package swiftcards.core.player.activities;

import swiftcards.core.player.PlayerActivity;

import java.io.Serializable;

public class CardsPulledFromPool extends PlayerActivity<CardsPulledFromPool.CardsPulledByPlayer> {
    public CardsPulledFromPool(int amount, int playerId) {
        super(new CardsPulledByPlayer(amount, playerId));
    }

    public static class CardsPulledByPlayer implements Serializable {
        int amount;
        int playerId;

        public CardsPulledByPlayer(int amount, int activePlayerId) {
            this.amount = amount;
            playerId = activePlayerId;
        }

        public int getAmount() {
            return amount;
        }

        public int getPlayerId() {
            return playerId;
        }
    }
}
