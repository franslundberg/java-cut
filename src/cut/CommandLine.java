package cut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A very simple command line parser.
 * Example: -group a -verbose -users alan peter
 * Three options, one with a single value (-group), one with zero values (-verbose)
 * and one with two values (-users).
 * 
 * There are more capable implementations of command line parsing in 
 * other libraries, but this is a very simple, short (100 lines) implementation 
 * in the public domain. It may avoid a dependency or two.
 * 
 * @author Frans Lundberg
 */
public class CommandLine {
    private static final String[] empty = new String[0];
    private final HashMap<String, String[]> map;
    private final String[] args;
    
    /**
     * Creates an immutable CommandLine object from args.
     */
    public CommandLine(String[] args) {
        this.args = args.clone();
        map = new HashMap<String, String[]>();
        
        String name = null;
        List<String> values = null;
        
        for (String arg : args) {
            if (isName(arg)) {
                putOptionToMap(name, values);
                
                name = createName(arg);
                values = new ArrayList<String>();
            } else {
                if (name != null) {
                    values.add(arg);
                }
            }
        }
        
        putOptionToMap(name, values);
    }
    
    /**
     * Has zero of more values with the given option name.
     */
    public boolean has(String name) {
        return valueCount(name) >= 0;
    }
    
    public boolean hasOne(String name) {
        return valueCount(name) == 1;
    }
    
    public String[] get(String name) {
        return map.get(name);
    }
    
    public String getOne(String name) {
        return valueCount(name) > 0 ? get(name)[0] : null;
    }
    
    /**
     * Number of values given option name.
     * 
     * @return The number of values or -1 if the option does not exist.
     */
    public int valueCount(String name) {
        String[] arr = map.get(name);
        return arr == null ? -1 : arr.length;
    }
    
    public int optionCount() {
        return map.size();
    }
    
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (String s : args) {
            b.append(s + " ");
        }
        
        return b.toString().trim();
    }

    private void putOptionToMap(String name, List<String> values) {
        if (name != null) {
            map.put(name, values.toArray(empty));
        }
    }

    private boolean isName(String arg) {
        return arg.length() > 1 && arg.startsWith("-");
    }

    private String createName(String arg) {
        assert isName(arg);
        return arg.substring(1);
    }
}