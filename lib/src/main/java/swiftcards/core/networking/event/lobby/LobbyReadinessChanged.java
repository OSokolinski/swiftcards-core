package swiftcards.core.networking.event.lobby;

import swiftcards.core.util.EventBase;

public class LobbyReadinessChanged extends EventBase<Boolean> {
    public LobbyReadinessChanged(Boolean arePlayersReady) {
        super(arePlayersReady);
    }
}
