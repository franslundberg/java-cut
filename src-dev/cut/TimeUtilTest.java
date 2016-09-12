package cut;

import org.junit.Assert;
import org.junit.Test;

public class TimeUtilTest {
    @Test
    public void testUtcTimeString1() {
        // According to http://www.onlineconversion.com/unix_time.htm:
        // 1500000000 seconds = Fri, 14 Jul 2017 02:40:00 GMT
        String s = TimeUtil.toUtcTimeString(1500000000L * 1000L);
        String expected = "2017-07-14 02:40:00.000";
        Assert.assertEquals(expected, s);
    }
    
    @Test
    public void testUtcTimeString2() {
        // According to http://www.onlineconversion.com/unix_time.htm:
        // 1500000000 seconds = Fri, 14 Jul 2017 02:40:00 GMT
        String s = TimeUtil.toUtcTimeString(1500000002003L);
        String expected = "2017-07-14 02:40:02.003";
        Assert.assertEquals(expected, s);
    }
}
