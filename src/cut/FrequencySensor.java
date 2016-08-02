package cut;

/**
 * Counts how often something happens in occurrences per second. An exponentially decreasing mean is
 * used. A time scale in nanoseconds is used by this class. Use System.nanoTime() possibly.
 * This class does not use a clock internally, we want deterministic behaviour.
 * 
 * @author Frans Lundberg
 */
public class FrequencySensor {
    /** Half life in seconds. */
    private double halfLife;

    /** Current value. */
    private double value;

    /** The time at which the value was last computed. */
    private long valueTime;

    /**
     * A new counter. To measure frequency accurately, the average time between events should be
     * many times smaller than halfLife. The frequency value estimate start at 0.0.
     * 
     * @param halfLifeInSeconds
     *            Half life for averaging in seconds. 
     * @param startTime
     *            Start time (current time) of the measurement in nano seconds.
     */
    public FrequencySensor(double halfLifeInSeconds, long startTimeInNanos) {        
        if (halfLifeInSeconds <= 0.0) {
            throw new IllegalArgumentException();
        }
        this.halfLife = halfLifeInSeconds;
        this.value = 0.0;
        this.valueTime = startTimeInNanos;
    }

    /**
     * Call this every time an event occurs.
     */
    public synchronized void tick() {
        tick(System.nanoTime());
    }

    /**
     * Call this method every time an event occurs. This method takes a nano second time value.
     * Useful for deterministic testing and other scenarios when the system nano time
     * (System.nanoTime()) should not be used.
     * 
     * @param nanoTime
     *            Current time in nano seconds.
     */
    public synchronized void tick(long nanoTime) {
        long now = nanoTime;
        double diff = 1e-9 * Math.abs(now - valueTime); // time diff in secs
        double decayFactor = Math.pow(2.0, -diff / halfLife);

        // frequency estimate since last event is 1.0 / <time since last event>
        double fEstimate = 1.0 / diff;

        this.value = decayFactor * this.value + (1.0 - decayFactor) * fEstimate;
        this.valueTime = now;
    }

    /**
     * Returns the frequency in occurrences per second.
     * 
     * @param nanoTime
     *            Current time in nano seconds.
     */
    public synchronized double getFrequency(long nanoTime) {
        double diff = 1e-9 * Math.abs(nanoTime - valueTime);
        double decayFactor = Math.pow(2.0, -diff / halfLife);
        return value * decayFactor;
    }

    public String toString() {
        return "f=" + getFrequency(System.nanoTime());
    }
}

/*
 * http://en.wikipedia.org/wiki/Exponential_decay. N(t) = N0 * 2 ^ (-t / t_half)
 * 
 * Our strategy is to be keep only two numbers and still get a current estimate of frequency. value
 * is the frequency estimate at time valueTime. The frequency estimate any time in the future is:
 * 
 * decayFactor = 2 ^ (-(now - valueTime) / halfLife) valueNow = value * decayFactor
 * 
 * When an event occurs, we update the value by a weighted average based on the old frequency value
 * and the new estimate which is:
 * 
 * fEstimate = 1.0 / <time elapsed since last event>
 * 
 * The weighted average is:
 * 
 * this.value = decayFactor * this.value + (1.0 - decayFactor) * fEstimate;
 * 
 * The decayFactor is computed from time since last event and the halfLife constant.
 */