package swiftcards.core.networking.event.lobby;

import swiftcards.core.game.lobby.GameSettings;
import swiftcards.core.util.EventBase;

public class SettingsUpdated extends EventBase<GameSettings> {
    public SettingsUpdated(GameSettings settings) {
        super(settings);
    }
}
