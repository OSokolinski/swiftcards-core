package swiftcards.core.game.lobby;

import swiftcards.core.player.AIPlayer;
import swiftcards.core.player.NetworkPlayer;
import swiftcards.core.player.Player;
import swiftcards.core.game.lobby.Lobby.*;
import swiftcards.core.game.lobby.PlayerSlot.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameSettings implements Serializable {

    public List<PlayerSlot> players;
    public LobbyStatus status;

    private final int playerAmountLimit = 10;
    private int playerAmount = 4;

    public GameSettings() {
        players = new ArrayList<>(10);
        for (int i = 0; i < playerAmountLimit; i++) {
            players.add(new PlayerSlot(PlayerSlotStatus.CLOSED));
        }

        status = LobbyStatus.OPEN;
    }

    public void setDefaultPlayerConfig(LobbyType lobbyType) {
        for (int i = 0; i < players.size(); i++) {

            if (lobbyType == LobbyType.ONLINE) {
                players.set(i, new PlayerSlot(PlayerSlotStatus.NETWORK_OPEN));
            }
            else {
                if (i == 0) {
                    players.set(i, new PlayerSlot(PlayerSlotStatus.HUMAN));
                }
                else if (i < playerAmount) {
                    PlayerSlotStatus defaultSlotStatus = lobbyType == LobbyType.OFFLINE
                        ? PlayerSlotStatus.AI : PlayerSlotStatus.NETWORK_OPEN;

                    players.set(i, new PlayerSlot(defaultSlotStatus));
                }
                else {
                    players.set(i, new PlayerSlot(PlayerSlotStatus.CLOSED));
                }
            }
        }
    }

    public void setPlayerAmount(int newPlayerAmount) {
        playerAmount = newPlayerAmount;
    }

    public int getPlayerAmount() {
        return playerAmount;
    }

    public int getPlayerAmountLimit() {
        return playerAmountLimit;
    }

    public PlayerSlot getAvailableNetworkPlayerSlot() {
        List<PlayerSlot> playerSlots = players
            .stream()
            .filter(s -> s.status == PlayerSlotStatus.NETWORK_OPEN)
            .collect(Collectors.toList());

        return playerSlots.size() > 0 ? playerSlots.get(0) : null;
    }

    public List<PlayerSlot> getNotAppliedAIPlayerSlots() {
        return players
            .stream()
            .filter(s -> s.status == PlayerSlotStatus.AI && s.playerId == -1)
            .collect(Collectors.toList());
    }

    public void setPlayerOnSlot(PlayerSlot playerSlot, Player player) {
        playerSlot.playerId = player.getId();
        playerSlot.playerName = player.getDisplayName();

        if (player instanceof AIPlayer) {
            playerSlot.status = PlayerSlotStatus.AI;
        }
        else if (player instanceof NetworkPlayer) {
            playerSlot.connectionId = ((NetworkPlayer) player).getConnectionId();
            playerSlot.status = PlayerSlotStatus.NETWORK_USED;
        }
    }

    public void kickDisconnectedPlayer(int connectionId) {
        PlayerSlot playerSlot = players.stream()
            .filter(p -> p.connectionId == connectionId)
            .collect(Collectors.toMap(p -> 0, p -> p))
            .getOrDefault(0, null);

        if (playerSlot == null) {
            return;
        }

        playerSlot.playerId = -1;
        playerSlot.connectionId = -1;
        playerSlot.status = PlayerSlotStatus.NETWORK_OPEN;
        playerSlot.playerName = null;
    }

    public boolean isEveryPlayerReady() {
        return players.stream()
            .filter(p -> p.status == PlayerSlotStatus.NETWORK_USED)
            .allMatch(PlayerSlot::isReady);
    }

    public enum LobbyStatus {
        OPEN,
        CLOSED,
        READY
    }


}
