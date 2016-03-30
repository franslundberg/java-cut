package cut;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Static utility methods for creating objects.
 * 
 * @author Frans Lundberg
 */
public class Create {
    private Create() {}
    
    /**
     * Creates and returns set of items.
     */
    public static <T> Set<T> set(T... items) {
        HashSet<T> set = new HashSet<T>(items.length);
        
        for (T item : items) {
            set.add(item);
        }
        
        return set;
    }
    
    /**
     * Creates and returns a list of items.
     */
    public static <T> List<T> list(T... items) {
        ArrayList<T> list = new ArrayList<T>(items.length);
        for (T item : items) {
            list.add(item);
        }
        
        return list;
    }
}
