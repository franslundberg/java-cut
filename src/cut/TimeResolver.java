package cut;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * <p>The methods resolve() and resolveAbsolute() resolve a <i>time string</i> to a time value
 * in clock units (100 ns units since epoch).
 * The format of the time string loosely follows ISO 8601.
 * This class is not thread-safe. The Gregorian calendar is used (the ordinary Western calendar).
 * Leap seconds are never considered. One day is always exactly 24*3600 seconds, 
 * one minute is 60 seconds and one hour is always 3600 seconds.</p>
 * 
 * <pre>
 * The following examples with comments define the time format.
 * 
 * ABSOLUTE-TIME
 * 
 * "2013-08-19 14:29:10.1234567"    -- 19 Aug 2013 at 14:29:10.1234567.
 * "2013-08-19T14:29:10.1234567"    -- 19 Aug 2013 at 14:29:10.1234567.
 * "2013-08-19 14:29"               -- 19 Aug 2013 at 14:29:00. 
 * "2013-08-19"                     -- 19 Aug 2013 at 00:00:00.
 * "2013-08"                        -- 1 Aug 2013 at 00:00:00.
 * "2013-01"                        -- 1 Jan 2013 at 00:00:00.
 * 
 * TIME-OF-DAY
 * 
 * "14:29:10.1234567"               -- Today at 14:29:10.1234567.
 * "14:29"                          -- Today at 14:29.
 * 
 * MINUS-TIME
 * 
 * "Minus-time" is a negative offset to the current time in one of the following units: s, m, h, 
 * d (second, minute, hour, day).
 * 
 * "-1.5 s"                         -- 1.5 seconds ago.
 * "-30 m"                          -- 30 minutes ago.
 * "-2 h"                           -- 2 hours ago. 
 * "-3 days"                        -- 3 days ago (1 day = 24.0 hours).
 * 
 * The unit "months" is always 30 days; the unit "years" is always 365 days.
 * 
 * PLUS-TIME
 * 
 * "Plus-time" is a positive offset to the current time. Similar to minus-time.
 * 
 * MINUS-TIME PLUS TIME-OF-DAY
 * 
 * A minus-time expression can be followed by a TIME-OF-DAY. In this case, the resulting time
 * is the same as if the negative time duration was first subtracted from the current time, 
 * and then the resulting time was used together with the TIME-OF-DAY to resolve the time.
 * 
 * "-2 d 14:29"                     -- The day before yesterday at 14:29:00.
 * 
 * SPECIAL
 * 
 * "now"                            -- The current time.
 * 
 * MORE FORMAL
 * The syntax for a time-string can be more formally be described by this ABNF:
 * 
 * time-string = absolute / time-of-day / plus-minus / "now" 
 * absolute = absolute-date [space-or-t time-of-day]
 * absolute-date = "YYYY-MM-DD" / "YYYY-MM"
 * time-of-day = "HH:MM:SS.d" / "HH:MM"
 * space-or-t = SPACE / "T"
 * plus-minus = ("+" / "-") number SPACE unit [SPACE time]
 * unit = "s" / "m" / "h" / "days" / "weeks" / "months" / "years"
 * </pre>
 * 
 * @author Frans Lundberg
 */
public class TimeResolver {
    //
    // Let's try to implement the whole thing as a function. 
    // resolve(currentTime, timeZone, timeString)... Would be thread-safe.
    //
    
    /** UTC time zone. */
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    public static final long UNITS_PER_SECOND = 10*1000000;
    public static final long UNITS_PER_MILLISECOND = UNITS_PER_SECOND / 1000;
    private final TimeData td;
    private final GregorianCalendar calendar;
    
    /**
     * Stores parse state.
     */
    private static class TimeData {
        int year;
        int month;
        int date;
        int hour;
        int minute;
        double second;
    }
    
    /**
     * Creates a TimeResolver with the given time zone.
     */
    private TimeResolver(TimeZone timeZone) {
        if (timeZone == null) {
            throw new IllegalArgumentException("timeZone == null not allowed");
        }
        this.td = new TimeData();
        this.calendar = new GregorianCalendar(timeZone, Locale.US);
    }
    
    /**
     * This method resolves a time string (like "yesterday", "2013-08-30" or "now") to a time
     * value in database time units (100 ns units since epoch).
     * 
     * @param currentTime  In database units.
     * @param timeString  The time string to resolve.
     * @return The resolved time in database time units.
     * @throws BadFormatException
     */
    public static long resolve(TimeZone timeZone, long currentTime, String timeString) throws BadFormatException {
        return new TimeResolver(timeZone).reallyResolve(currentTime, timeString);
    }
    
    /**
     * Resolves a time string the must be absolute; no current time is needed.
     */
    public static long resolveAbsolute(TimeZone timeZone, String timeString) {
        return new TimeResolver(timeZone).reallyResolveAbsolute(timeString);
    }
    
    private long reallyResolve(long currentTime, String timeString) throws BadFormatException {
        if (timeString == null) {
            throw new IllegalArgumentException("timeString == null is not allowed");
        }
        
        int length = timeString.length();
        
        if (length == 0) {
            throw new BadFormatException("Empty string is not allowed for a timeString.");
        }
        
        if (length < 3) {
            throw new BadFormatException("Time string is too short, was: " + timeString + ".");
        }
        
        if (length == 3 && timeString.equals("now")) {
            return currentTime;
        }
        
        char first = timeString.charAt(0);
        if (first == '-' || first == '+') {
            return plusOrMinus(currentTime, timeString);
        }
        
        if (length > 4 && timeString.charAt(4) == '-') {
            return reallyResolveAbsolute(timeString);
        } else {
            return timeOfDay(currentTime, timeString);
        }
    }
    
    private long reallyResolveAbsolute(String timeString) {
        long length = timeString.length();
        
        if (length < 7) {
            throw new BadFormatException("Bad time string, too short, was: '" + timeString + "'.");
        }
        
        if (timeString.charAt(4) != '-') {
            throw new BadFormatException("Bad time string, expected '-' as 5th char, was: '" + timeString + "'.");
        }
        
        parseYear(timeString);
        
        parseMonth(timeString);
        
        if (length > 7) {
            if (length < 10) {
                throw new BadFormatException("Bad absolute time string, was: '" + timeString + "'.");
            }
            
            if (timeString.charAt(7) != '-') {
                throw new BadFormatException("Bad absolute time string, was: '" + timeString + "'.");
            }
            
            parseDate(timeString);
        } else {
            td.date = 1;
        }
        
        if (length > 10) {
            if (length < 16) {
                throw new BadFormatException("Bad absolute time string, was: '" + timeString + "'.");
            }
            
            char c = timeString.charAt(10);
            if (c != ' ' && c != 'T') {
                throw new BadFormatException("Bad absolute time string, was: '" + timeString + "'.");
            }
            
            parseTime(timeString, 11);
            
        } else {
            td.hour = 0;
            td.minute = 0;
            td.second = 0.0;
        }
        
        return computeTimeUnits();
    }

    private void parseDate(String timeString) {
        td.date = parseZeroPadded(timeString.substring(8, 10), timeString);
        // Let's allowed "overflow" for now, like: 2013-02-50".
    }

    private void parseMonth(String timeString) {
        td.month = parseZeroPadded(timeString.substring(5, 7), timeString);
        if (td.month < 0) {
            throw new BadFormatException("Bad absolute time string, bad month, was: '" + timeString + "'.");
        }
    }

    private void parseYear(String timeString) {
        try {
            td.year = Integer.parseInt(timeString.substring(0, 4));
        } catch (NumberFormatException e) {
            throw new BadFormatException("Bad time string, was: '" + timeString + "'.");
        }
    }
    
    /**
     * Converts from TimeData to GregorianCalendar to time units.
     */
    private long computeTimeUnits() {
        calendar.setTimeInMillis(0);
        calendar.set(Calendar.YEAR, td.year);
        calendar.set(Calendar.MONTH, td.month - 1);
        calendar.set(Calendar.DATE, td.date);
        calendar.set(Calendar.HOUR_OF_DAY, td.hour);
        calendar.set(Calendar.MINUTE, td.minute);
        
        double floor = Math.floor(td.second);
        int second = (int) floor;
        double fraction = td.second - floor;
        if (fraction > 1.0) {
            throw new AssertionError("fraction: " + fraction + ", td.second = " + td.second);
        }
        
        fraction = 1000.0 * fraction;
        double milliFloor = Math.floor(fraction);
        double milliFraction = fraction - milliFloor;
        if (milliFraction > 1.0) {
            throw new AssertionError("milliFraction: " + milliFraction + ", td.second = " + td.second);
        }
        
        int millis = (int) milliFloor;
        long change = Math.round(milliFraction * UNITS_PER_MILLISECOND);
        
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millis);
        
        long result = calendar.getTimeInMillis() * UNITS_PER_MILLISECOND + change;
        return result;
    }
    
    /**
     * Converts from TimeData to GregorianCalendar to time units.
     * currentTime is used to compute "today".
     */
    private long computeTimeUnitsB(long currentTime) {
        calendar.setTimeInMillis(currentTime / TimeResolver.UNITS_PER_MILLISECOND);
        calendar.set(Calendar.HOUR_OF_DAY, td.hour);
        calendar.set(Calendar.MINUTE, td.minute);
        
        double floor = Math.floor(td.second);
        int second = (int) floor;
        double fraction = td.second - floor;
        if (fraction > 1.0) {
            throw new AssertionError("fraction: " + fraction + ", td.second = " + td.second);
        }
        
        fraction = 1000.0 * fraction;
        double milliFloor = Math.floor(fraction);
        double milliFraction = fraction - milliFloor;
        if (milliFraction > 1.0) {
            throw new AssertionError("milliFraction: " + milliFraction + ", td.second = " + td.second);
        }
        
        int millis = (int) milliFloor;
        long change = Math.round(milliFraction * UNITS_PER_MILLISECOND);
        
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, millis);
        
        long result = calendar.getTimeInMillis() * UNITS_PER_MILLISECOND + change;
        return result;
    }
    
    private long timeOfDay(long currentTime, String timeString) {
        parseTime(timeString, 0);
        return computeTimeUnitsB(currentTime);
    }
    
    /**
     * Parses a time string of the format HH:MM[:SS.sssssss], writes result to this.td.
     * Sets td.hour, td.minute, td.second or throws BadFormatException.
     */
    private void parseTime(String timeString, int offset) throws BadFormatException {
        int length = timeString.length() - offset;
        
        if (length < 5) {
            throw new BadFormatException("Bad time string, was: '" + timeString + "'.");
        }
        
        td.hour = parseZeroPadded(timeString.substring(offset + 0, offset + 2), timeString);
        offset += 2;
        
        if (timeString.charAt(offset) != ':') {
            throw new BadFormatException("Bad time string, expected ':', was: '" + timeString + "'.");
        }
        offset += 1;
        
        td.minute = parseZeroPadded(timeString.substring(offset, offset + 2), timeString);
        offset += 2;
        
        // If there are seconds
        if (length > 5) {
            if (length < 8) {
                throw new BadFormatException("Bad time string, was: '" + timeString + "'.");
            }
            
            if (timeString.charAt(offset) != ':') {
                throw new BadFormatException("Bad time string, expected ':', was: '" + timeString + "'.");
            }
            offset += 1;
            
            td.second = parseZeroPaddedDouble(timeString.substring(offset), timeString);
        } else {
            td.second = 0.0;
        }
    }
    
    private long plusOrMinus(long currentTime, String timeString) {
        String[] parts = timeString.split(" ");
        if (parts.length < 2) {
            throw new BadFormatException("Bad plus/minus time string: '" + timeString + "'.");
        }
        
        double number = parseNumber(parts[0], timeString);
        
        double factor = parseUnit(parts[1], timeString);
        
        double deltaTime = number * factor;
        long time = currentTime + ((long) deltaTime);
        
        if (time < 0) {
            throw new BadFormatException("Time out of range (negative: " + time 
                    + "), time string was: '" + timeString + "'.");
        }
        
        // If time of day comes after plus-minus expression.
        if (parts.length > 2) {
            parseTime(timeString, parts[0].length() + 1 + parts[1].length() + 1);
            time = computeTimeUnitsB(time);
        }
        
        return time;
    }

    private double parseNumber(String numberString, String timeString) {
        double number = 0.0;
        try {
            number = Double.parseDouble(numberString);
        } catch (NumberFormatException e) {
            throw new BadFormatException("Bad plus/minus time string: '" + timeString + "'.");
        }
        
        return number;
    }

    private double parseUnit(String unit, String timeString) {
        double factor;
        
        switch (unit) {
        case "s":
            factor = UNITS_PER_SECOND;
            break;
        case "m":
            factor = UNITS_PER_SECOND * 60.0;
            break;
        case "h":
            factor = UNITS_PER_SECOND * 3600.0;
            break;
        case "day":
        case "days":
            factor = UNITS_PER_SECOND * 3600.0 * 24.0;
            break;
        case "week":
        case "weeks":
            factor = UNITS_PER_SECOND * 3600.0 * 24.0 * 7;
            break;
        case "month":
        case "months":
            factor = UNITS_PER_SECOND * 3600.0 * 24.0 * 30;
            break;
        case "year":
        case "years":
            factor = UNITS_PER_SECOND * 3600.0 * 24.0 * 365;
            break;
        
        default:
            throw new BadFormatException("Bad plus-minus time string, bad unit, was: '" + timeString + "'.");
        }
        return factor;
    }
    
    /**
     * Parse a possibly zero-padded integer. Max one zero as padding assumed.
     */
    private int parseZeroPadded(String s, String timeString) {
        if (s.startsWith("0")) {
            s = s.substring(1);
        }
        
        int result = -1;
        try {
            result = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new BadFormatException("Bad time string: '" + timeString + "'.");
        }
        
        return result;
    }
    
    /**
     * Parse a possibly zero-padded integer. Max one zero as padding assumed.
     */
    private double parseZeroPaddedDouble(String s, String timeString) {
        if (s.startsWith("0")) {
            s = s.substring(1);
        }
        
        double result = -1;
        try {
            result = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new BadFormatException("Bad time string: '" + timeString + "'.");
        }
        
        return result;
    }
    
    public static class BadFormatException extends RuntimeException {
        public BadFormatException(String message) {
        	super(message);
		}

		private static final long serialVersionUID = 1L;
    }
}

