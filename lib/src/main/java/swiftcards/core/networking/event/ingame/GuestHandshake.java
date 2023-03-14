package swiftcards.core.networking.event.ingame;

import swiftcards.core.networking.NetworkEventBase;
import swiftcards.core.networking.PlayerCredentials;

public class GuestHandshake extends NetworkEventBase<PlayerCredentials> {
    public GuestHandshake(PlayerCredentials playerCredentials) {
        super(playerCredentials);
    }
}
