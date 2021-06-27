package swiftcards.core.game;

import swiftcards.core.player.Player;

public interface Lobby {

    GameSettings.PlayerSlot[] getPlayerSlots();

    enum LobbyType {
        OFFLINE,
        LAN,
        ONLINE
    }
}
