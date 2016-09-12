package cut;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Public static methods (functions) related to time and dates.
 * 
 * @author Frans Lundberg
 */
public class TimeUtil {
    
    /**
     * FORMAT1, yyyy-MM-dd HH:mm:ss.SSS, millisecond resolution.
     */
    private static final SimpleDateFormat FORMAT1 = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
    static {
        FORMAT1.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    
    /**
     * FORMAT2, yyyy-MM-dd HH:mm.
     */
    private static final SimpleDateFormat FORMAT2 = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm", Locale.US);
    static {
        FORMAT2.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    
    public static String toUtcTimeString(Date date) {
        return FORMAT1.format(date);
    }
    
    public static String toUtcTimeString(long millis) {
        return FORMAT1.format(new Date(millis));
    }
    
    /**
     * Converts time in milliseconds since Unix Epoch 
     * to a time string in UTF, minute resolution used.
     */
    public static String toUtcTimeStringMinutes(long millis) {
        return FORMAT2.format(new Date(millis));
    }
}
