package swiftcards.core.game.lobby.event;

import swiftcards.core.game.lobby.PlayerSlot;
import swiftcards.core.util.EventBase;

public class LobbySlotChanged extends EventBase<PlayerSlot> {
    LobbySlotChanged(PlayerSlot slot) {
        super(slot);
    }
}
