package swiftcards.core.util;

public interface Logger {
    void log(String message, Object... params);
    void logError(String message, Object... params);
    void throwFatalErrorAndExit(String message, Object... params);
}
