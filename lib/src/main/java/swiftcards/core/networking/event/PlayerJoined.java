package swiftcards.core.networking.event;

import swiftcards.core.networking.NetworkEventBase;
import swiftcards.core.player.Player;

public class PlayerJoined extends NetworkEventBase<Player> {
    public PlayerJoined(Player player) {
        super(player);
    }
}
