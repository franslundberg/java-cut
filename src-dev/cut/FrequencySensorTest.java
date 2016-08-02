package cut;

import org.junit.Assert;
import org.junit.Test;

public class FrequencySensorTest {
    
    @Test
    public void test1() {
        long e9 = 1000L*1000*1000;
        FrequencySensor sensor = new FrequencySensor(10.0, 0);
        
        for (int i = 1; i <= 100; i++) {
            sensor.tick(i*e9);
        }
        
        double diff = Math.abs(1.0 - sensor.getFrequency(100*e9));
        Assert.assertTrue("diff:" + diff, diff < 0.01);
   }
    
   @Test
   public void test2() {
        long stepTime = 1000L*1000*1000 / 3;
        FrequencySensor sensor = new FrequencySensor(5.0, 0);
        
        for (int i = 1; i <= 100; i++) {
            sensor.tick(i*stepTime);
        }
        
        double f = sensor.getFrequency(101*stepTime);
        double diff = Math.abs(3.0 - f);
        Assert.assertTrue("f=" + f, diff < 0.3);
   }
}
