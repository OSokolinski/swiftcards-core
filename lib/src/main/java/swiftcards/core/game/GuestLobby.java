package swiftcards.core.game;

import swiftcards.core.game.GameSettings.PlayerSlot;

public class GuestLobby implements Lobby {

    private PlayerSlot[] slots;

    public GuestLobby() {
        slots = new PlayerSlot[10];
    }

    @Override
    public PlayerSlot[] getPlayerSlots() {
        return slots;
    }
}
