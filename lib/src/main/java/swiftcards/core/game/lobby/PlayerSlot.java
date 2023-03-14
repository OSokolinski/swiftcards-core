package swiftcards.core.game.lobby;

import java.io.Serializable;

public class PlayerSlot implements Serializable {

    public PlayerSlotStatus status;
    public String playerName;
    public int playerId;
    public int connectionId;
    private boolean isReady;

    public PlayerSlot(PlayerSlotStatus playerSlotStatus) {
        status = playerSlotStatus;
        playerName = null;
        playerId = -1;
        connectionId = -1;
    }

    public String getDescription() {

        return playerId > -1 ? playerName
            : switch (status) {
                case CLOSED -> "Closed";
                case HUMAN -> "You";
                case AI -> "Computer";
                case NETWORK_OPEN -> "Open";
                case NETWORK_USED -> "Network Player";
            };
    }

    public void toggleIsReady() {
        if (status == PlayerSlotStatus.NETWORK_USED) {
            isReady = !isReady;
        }
    }

    public boolean isReady() {
        if (status == PlayerSlotStatus.AI || status == PlayerSlotStatus.HUMAN) {
            return true;
        }
        else if (status == PlayerSlotStatus.NETWORK_USED) {
            return isReady;
        }
        else {
            return false;
        }
    }

    public enum PlayerSlotStatus {
        CLOSED,
        HUMAN,
        AI,
        NETWORK_OPEN,
        NETWORK_USED
    }
}
