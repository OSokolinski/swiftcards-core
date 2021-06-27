package swiftcards.core.game;

import swiftcards.core.util.EventBase;
import swiftcards.core.game.GameSettings.PlayerSlot;

public class LobbySlotChanged extends EventBase<PlayerSlot> {
    LobbySlotChanged(PlayerSlot slot) {
        super(slot);
    }
}
