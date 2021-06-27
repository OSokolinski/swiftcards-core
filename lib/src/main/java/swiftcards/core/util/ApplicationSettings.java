package swiftcards.core.util;

public class ApplicationSettings {
    private static boolean hostMode;

    public ApplicationSettings() {
        hostMode = false;
    }

    public static boolean getHostMode() {
        return hostMode;
    }


}
