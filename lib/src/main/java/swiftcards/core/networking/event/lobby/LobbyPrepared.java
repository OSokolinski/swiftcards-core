package swiftcards.core.networking.event.lobby;

import swiftcards.core.game.lobby.GameSettings;
import swiftcards.core.util.EventBase;

public class LobbyPrepared extends EventBase<GameSettings> {
    public LobbyPrepared(GameSettings gameSettings) {
        super(gameSettings);
    }
}
