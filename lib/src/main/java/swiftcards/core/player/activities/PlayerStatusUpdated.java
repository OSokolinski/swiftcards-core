package swiftcards.core.player.activities;

import swiftcards.core.player.PlayerActivity;

import java.io.Serializable;

public class PlayerStatusUpdated extends PlayerActivity<PlayerStatusUpdated.PlayerStatusData> {

    public PlayerStatusUpdated(int playerId, int cardAmount, boolean hasFinishedGame) {
        super(new PlayerStatusUpdated.PlayerStatusData(playerId, cardAmount, hasFinishedGame));
    }

    public static class PlayerStatusData implements Serializable {
        int cardAmount;
        int playerId;
        boolean hasFinishedGame;

        public PlayerStatusData(int playerId, int cardAmount, boolean hasFinishedGame) {
            this.cardAmount = cardAmount;
            this.playerId = playerId;
            this.hasFinishedGame = hasFinishedGame;
        }

        public int getCardAmount() {
            return cardAmount;
        }

        public int getPlayerId() {
            return playerId;
        }

        public boolean hasFinishedGame() {
            return hasFinishedGame;
        }
    }
}
