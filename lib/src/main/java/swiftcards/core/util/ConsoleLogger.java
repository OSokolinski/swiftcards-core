package swiftcards.core.util;

public class ConsoleLogger implements Logger {

    public static boolean keepLogging = true;

    @Override
    public void log(String message, Object... params) {
        printf("[LOG]: " + message + "%n", params);
    }

    @Override
    public void logError(String message, Object... params) {
        printf("[ERROR]: " + message + "%n", params);
    }

    @Override
    public void throwFatalErrorAndExit(String message, Object... params) {
        printf("[FATAL ERROR]: " + message + "%n", params);
        System.exit(1);
    }

    private void printf(String log, Object... params) {
        if (keepLogging) {
            System.out.printf(log, params);
        }
    }
}
