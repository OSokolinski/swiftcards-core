package swiftcards.core.util;

public abstract class Freezable {

    protected volatile boolean isFrozen = false;

    /**
     * Freezing current thread
     */
    protected void freeze() {
        isFrozen = true;

        while (isFrozen) {
            synchronized (this) {
                try {
                    wait();
                }
                catch (InterruptedException e) {
                    System.out.println("Freezing exception: " + e);
                }
            }
        }
    }

    /**
     * Resuming current thread
     */
    protected void resume() {
        isFrozen = false;

        synchronized (this) {
            notify();
        }
    }
}
