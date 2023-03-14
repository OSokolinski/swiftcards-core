package swiftcards.core.networking;

import java.io.Serializable;

public class PlayerCredentials implements Serializable {

    private final String displayName;

    public PlayerCredentials(String name) {
        displayName = name;
    }

    public String getDisplayName() {
        return displayName;
    }
}
