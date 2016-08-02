package cut;

import org.junit.Assert;
import org.junit.Test;

public class EventToWaitForTest {
	
	@Test
    public void test1() {
        EventToWaitFor event = new EventToWaitFor();
        Assert.assertFalse(event.didItHappen());
        event.happened();
        Assert.assertTrue(event.didItHappen());
    }

	@Test
    public void test2() {
        final EventToWaitFor event = new EventToWaitFor();
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // empty
                }
                
                event.happened();
            }
        });
        t.setPriority(Thread.MAX_PRIORITY);
        
        Assert.assertFalse(event.didItHappen());
        t.start();
        Assert.assertFalse(event.didItHappen());
        event.waitForEvent();
        Assert.assertTrue(event.didItHappen());
    }
}
