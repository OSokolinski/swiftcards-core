package swiftcards.core.ui.cli.scenery.stagedata;

import swiftcards.core.game.lobby.Lobby.*;

public class LobbySettings {

    private final LobbyType lobbyMode;

    public LobbySettings(LobbyType mode) {
        lobbyMode = mode;
    }

    public LobbyType getLobbyMode() {
        return lobbyMode;
    }
}
