package swiftcards.core.networking.event.ingame;

import swiftcards.core.game.lobby.GameSettings;
import swiftcards.core.networking.NetworkEventBase;

public class HostHandshake extends NetworkEventBase<GameSettings> {
    public HostHandshake(GameSettings gameSettings) {
        super(gameSettings);
    }
}
