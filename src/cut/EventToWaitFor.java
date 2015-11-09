package cut;

/**
 * An event that a thread can wait for.
 *
 * @author Frans Lundberg
 */
public class EventToWaitFor {
    private boolean hasHappened = false;

    /**
     * Tells this object that the event has occurred.
     */
    public synchronized void happened() {
        hasHappened = true;
        notifyAll();
    }

    /**
     * Waits for event to occur, or returns immediately if it has occurred. The
     * method also returns immediately if the calling thread is interrupted.
     * Waits indefinitely.
     */
    public synchronized void waitForEvent() {
        waitForEvent(0);
    }

    /**
     * Waits for event to occur, or returns immediately if it has occurred. The
     * method also returns immediately if the calling thread is interrupted.
     * Waiting is constrained by the time parameter.
     * 
     * @param time Maximum time to wait in milliseconds.
     */
    public synchronized void waitForEvent(long time) {
        try {
            if (!hasHappened) {
                wait(time);
                if (!hasHappened) {
                    return;
                }
            }
        } catch (InterruptedException e) {
            // silently ignore. We were stopped by another thread
        }
    }

    /**
     * Returns true if the event actually happened.
     * 
     * @return true if happened.
     */
    public synchronized boolean didItHappen() {
        return hasHappened;
    }
}
