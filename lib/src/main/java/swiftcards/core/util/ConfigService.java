package swiftcards.core.util;

import swiftcards.core.networking.ActivityPresenter;
import swiftcards.core.player.PlayerPrompter;

// TODO Make this class abstract - force client applications to have their own classes of config services
public class ConfigService {

    private static ConfigService instance = null;

    private ActivityPresenter networkActivityPresenter = null;
    private PlayerPrompter playerPrompter = null;
    private String playerName = null;

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

    public int getInitialPlayerCardAmount() {
        return 7;
    }

    public int getDefaultPort() {
        return 56677;
    }

    public void setPlayerPrompter(PlayerPrompter playerPrompter) {
        this.playerPrompter = playerPrompter;
    }

    public PlayerPrompter getPlayerPrompter() {
        return playerPrompter;
    }

    public void setNetworkActivityPresenter(ActivityPresenter networkActivityPresenter) {
        this.networkActivityPresenter = networkActivityPresenter;
    }

    public ActivityPresenter getNetworkActivityPresenter() {
        return networkActivityPresenter;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getActivityDelayTime() {
        return 500;
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
