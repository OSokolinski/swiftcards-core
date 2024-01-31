package swiftcards.core.game;

import swiftcards.core.player.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Summary implements Serializable {

    private int turnCount;
    private final LinkedHashMap<Integer, PlayerSummary> playerSummary = new LinkedHashMap<>();
    private final ArrayList<Integer> winnerList = new ArrayList<>();

    public int getTurnCount() {
        return turnCount;
    }

    public ArrayList<Integer> getWinnerList() {
        return winnerList;
    }

    public void addLastPlayer(Player player) {
       winnerList.add(player.getId());
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }

    public void updatePlayerSummary(Player player) {
        if (!playerSummary.containsKey(player.getId())) {
            playerSummary.put(player.getId(), new PlayerSummary());
        }
        PlayerSummary summary = playerSummary.get(player.getId());

        summary.turnCount++;
        int currentCardAmount = player.getCardPool().getCards().size();
        if (summary.maxCardAmount < currentCardAmount) {
            summary.maxCardAmount = currentCardAmount;
        }

        if (player.hasFinishedPlay() && !winnerList.contains(player.getId())) {
            winnerList.add(player.getId());
        }
    }

    public LinkedHashMap<Integer, PlayerSummary> getPlayerSummary() {
        return playerSummary;
    }

    public static class PlayerSummary implements Serializable {
        private int turnCount = 0;
        private int maxCardAmount = 0;

        public int getTurnCount() {
            return turnCount;
        }

        public int getMaxCardAmount() {
            return maxCardAmount;
        }
    }
}
