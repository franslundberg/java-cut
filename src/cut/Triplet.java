package cut;

/**
 * Immutable 3-tuple implementation.
 * 
 * @see Pair
 * @author Frans Lundberg
 */
public class Triplet<T0, T1, T2> {
    private final T0 v0;
    private final T1 v1;
    private final T2 v2;
    
    public Triplet(T0 v0, T1 v1, T2 v2) {
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
    }
    
    public final T0 getValue0() {
        return v0;
    }
    
    public final T1 getValue1() {
        return v1;
    }
    
    public final T2 getValue2() {
        return v2;
    }
}
