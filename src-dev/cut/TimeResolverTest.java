package cut;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;

public class TimeResolverTest {
	private static TimeZone UTC = TimeResolver.UTC;
	private static long UNITS_PER_SECOND = TimeResolver.UNITS_PER_SECOND;
	private static long UNITS_PER_MILLISECOND = TimeResolver.UNITS_PER_MILLISECOND;
	
    @Test
    public void testAbsolute0() {
        String[] ok = new String[]{
                "2013-01-02 12:02:12.1234567",
                "2013-01-02T12:02:12.1234567",
                "2013-01-02", 
                "2013-12-12 12:00", 
                "1999-02-10 12:00:01"};
        
        String[] bad = new String[]{
                "", 
                "2013-01-02W12:02:12.1234567",
                "2013-x1", 
                "2013-03-10 12:33-30", 
                "2013-03-10 12",
                "2013-03-10 12:30:",
                "2013-03-10 "};
        
        long currentTime = 1000*1000*1000 * TimeResolver.UNITS_PER_MILLISECOND;  // not needed for absolute times
        
        for (String s : ok) {
            TimeResolver.resolve(TimeResolver.UTC, currentTime, s);
        }
        
        Exception ex = null;
        for (String s : bad) {
            ex = null;
            try {
                TimeResolver.resolve(TimeResolver.UTC, currentTime, s);
            } catch (TimeResolver.BadFormatException e) {
                ex = e;
            }
            
            if (ex == null) {
                throw new AssertionError("Exception expected for string: " + s);
            }
        }
    }
    
    @Test
    public void testAbsolute1() {
        long millis = getMillisSinceEpoch(UTC, 2013, 3, 14, 16, 10, 20, 500);        
        String ts = "2013-03-14 16:10:20.500";
        Assert.assertEquals(millis * TimeResolver.UNITS_PER_MILLISECOND, TimeResolver.resolve(UTC, 0, ts));
    }
    
    @Test
    public void testAbsolute2() {
        long millis = getMillisSinceEpoch(UTC, 2000, 1, 1, 0, 0, 0, 0);   
        
        String ts = "2000-01-01 00:00:00.000";
        Assert.assertEquals(millis * UNITS_PER_MILLISECOND, TimeResolver.resolve(UTC, 0, ts));
        
        ts = "2000-01-01 00:00:00";
        Assert.assertEquals(millis * UNITS_PER_MILLISECOND, TimeResolver.resolveAbsolute(UTC, ts));
        
        ts = "2000-01-01 00:00";
        Assert.assertEquals(millis * UNITS_PER_MILLISECOND, TimeResolver.resolve(UTC, 0, ts));
    }
    
    @Test
    public void testAbsolute3() {
        // Resolves units - 10,000 time units in one millisecond.
        
        long millis = getMillisSinceEpoch(UTC, 2000, 1, 1, 0, 0, 0, 0);    
        
        String ts = "2000-01-01 00:00:00.0000123";
        Assert.assertEquals(millis * UNITS_PER_MILLISECOND + 123, TimeResolver.resolve(UTC, 0, ts));
        
        ts = "2000-01-01 00:00:00.0000123456";
        Assert.assertEquals(millis * UNITS_PER_MILLISECOND + 123, TimeResolver.resolve(UTC, 0, ts));
        
        ts = "2000-01-01 00:00:00.00001239";
        Assert.assertEquals(millis * UNITS_PER_MILLISECOND + 124, TimeResolver.resolve(UTC, 0, ts));
    }
    
    @Test
    public void testSpecial0() {
        long current = 1 * 1000*1000*1000 * UNITS_PER_MILLISECOND + 123;
        long result = TimeResolver.resolve(UTC, current, "now");
        Assert.assertEquals(current, result);
    }
    
    @Test
    public void tSpecial1() {
        long current = System.currentTimeMillis();
        current = current * UNITS_PER_MILLISECOND;
        long result = TimeResolver.resolve(UTC, current, "now");
        Assert.assertEquals(current, result);
    }
    
    @Test
    public void testTimeOfDay0() {
        long t1 = TimeResolver.resolve(UTC, 0, "2010-03-14 13:20:10.0010023");
        long t2 = TimeResolver.resolve(UTC, t1, "09:20:40.5000123");
        
        long millis = getMillisSinceEpoch(UTC, 2010, 3, 14, 9, 20, 40, 500);
        Assert.assertEquals(millis * UNITS_PER_MILLISECOND + 123, t2);
    }
    
    @Test
    public void testTimeOfDay1() {
        long t1 = TimeResolver.resolve(UTC, 0, "2010-03-14 03:01");
        long t2 = TimeResolver.resolve(UTC, t1, "09:20:40.5000123");
        
        long millis = getMillisSinceEpoch(UTC, 2010, 3, 14, 9, 20, 40, 500);
        Assert.assertEquals(millis * UNITS_PER_MILLISECOND + 123, t2);
    }
    
    @Test
    public void testTimeOfDay2() {
        long t1 = TimeResolver.resolve(UTC, 0, "2010-03-14 03:01");
        long t2 = TimeResolver.resolve(UTC, t1, "09:20");
        
        long millis = getMillisSinceEpoch(UTC, 2010, 3, 14, 9, 20, 0, 0);
        Assert.assertEquals(millis * UNITS_PER_MILLISECOND, t2);
    }
    
    @Test
    public void testTimeOfDay3() {
        long t1 = TimeResolver.resolve(UTC, 0, "1975-03-14");
        long t2 = TimeResolver.resolve(UTC, t1, "09:20");
        long t3 = TimeResolver.resolve(UTC, t1, "09:20:00.00000019");
        Assert.assertEquals(2, t3 - t2);
    }
    
    @Test
    public void testMinus0() {
        long t1 = TimeResolver.resolve(UTC, 0, "2010-03-14 03:01:10");
        long t2 = TimeResolver.resolve(UTC, t1, "-10 s");
        Assert.assertEquals(10 * UNITS_PER_SECOND, t1 - t2);
    }
    
    @Test
    public void testMinus1() {
        long t1 = TimeResolver.resolve(UTC, 0, "2010-03-14 03:01:10");
        long t2 = TimeResolver.resolve(UTC, t1, "-10 m");
        Assert.assertEquals(UNITS_PER_SECOND * 600, t1 - t2);
    }
    
    @Test
    public void testMinus2() {
        long t1 = TimeResolver.resolve(UTC, 0, "2010-03-14 03:01:10");
        long t2 = TimeResolver.resolve(UTC, t1, "-1 d 13:00:01");
        long t3 = TimeResolver.resolve(UTC, 0, "2010-03-13 13:00:01");
        Assert.assertEquals(t3, t2);
    }
    
    @Test
    public void testMinus3() {
        long t1 = TimeResolver.resolve(UTC, 0, "2013-09-01 03:01:10");
        long t2 = TimeResolver.resolve(UTC, t1, "-1 d 09:00");
        long t3 = TimeResolver.resolve(UTC, 0, "2013-08-31 09:00:00");
        Assert.assertEquals(t3, t2);
    }

    @Test
    public void testOneUnit() {
        long units = TimeResolver.resolveAbsolute(UTC, "1970-01-01 00:00:00.0000001");
        Assert.assertEquals(1L, units);
    }
    
    /**
     * Returns milliseconds since Epoch for a certain UTC date/time.
     * 
     * @param year    The year.
     * @param month   The month, January = 1
     * @param date    The date of the month.
     * @param hour    The hour (0-23)
     * @param min     The minutes (0-59)
     * @param sec     The second (0-29)
     * @param millis  Milliseconds of the second (0-999).
     * @return The number of milliseconds since Epoch (1 Jan 1970 UTC).
     */
    public static long getMillisSinceEpoch(TimeZone zone, 
            int year, int month, int date, 
            int hour, int min, int sec, int millis) {
        GregorianCalendar c = new GregorianCalendar(zone, Locale.US);
        c.setTimeInMillis(0);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DATE, date);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, min);
        c.set(Calendar.SECOND, sec);
        c.set(Calendar.MILLISECOND, millis);
        return c.getTimeInMillis();
    }
}
