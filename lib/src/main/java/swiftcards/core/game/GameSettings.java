package swiftcards.core.game;

import swiftcards.core.player.PlayerPrompter;

public class GameSettings {

    public PlayerSlot[] players;

    public GameSettings() {
        players = new PlayerSlot[10];
    }

    public void setDefaultPlayerConfig(boolean multiplayerMode) {
        for (int i = 0; i< players.length; i++) {

            if (multiplayerMode) {
                players[i] = new PlayerSlot(PlayerSlotStatus.NETWORK_OPEN);
            }
            else {
                if (i == 0) {
                    players[i] = new PlayerSlot(PlayerSlotStatus.HUMAN);
                }
                else if (i < 4) {
                    players[i] = new PlayerSlot(PlayerSlotStatus.AI);
                }
                else {
                    players[i] = new PlayerSlot(PlayerSlotStatus.CLOSED);
                }
            }
        }
    }

    public enum PlayerSlotStatus {
        CLOSED,
        HUMAN,
        AI,
        NETWORK_OPEN,
        NETWORK_USED
    }

    public static class PlayerSlot {

        public PlayerSlotStatus status = PlayerSlotStatus.CLOSED;

        public PlayerSlot(PlayerSlotStatus playerSlotStatus) {
            status = playerSlotStatus;
        }

        public String getDescription() {
            return switch (status) {
                case CLOSED -> "Closed";
                case HUMAN -> "You";
                case AI -> "Computer";
                case NETWORK_OPEN -> "Open";
                case NETWORK_USED -> "Network Player";
            };
        }


    }


}
