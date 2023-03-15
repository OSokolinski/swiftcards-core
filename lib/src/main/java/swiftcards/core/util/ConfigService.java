package swiftcards.core.util;

import swiftcards.core.networking.NetworkActivityPresenter;
import swiftcards.core.ui.cli.ConsolePlayerPrompter;
import swiftcards.core.player.PlayerPrompter;

public class ConfigService {

    private static ConfigService instance = null;

    private Logger logger;

    public static ConfigService getInstance() {
        if (instance == null) {
            instance = new ConfigService();
        }

        return instance;
    }

    public ConfigService() {
        logger = new ConsoleLogger();
    }

    public Class<? extends PlayerPrompter> getPlayerPrompterClass() {
        return ConsolePlayerPrompter.class;
    }

    public PlayerPrompter getPlayerPrompter() {
        return new ConsolePlayerPrompter();
    }

    public NetworkActivityPresenter getNetworkActivityPresenter() {
        return null;
    }

    public String getPlayerName() {
        return "Player";
    }

    public void log(String message, Object... args) {
        logger.log(message, args);
    }

    public void logError(String message, Object... args) {
        logger.log(message, args);
    }

    public void throwAndExit(String message, Object... args) {
        logger.throwFatalErrorAndExit(message, args);
    }
}
