package cut;

import org.junit.Assert;
import org.junit.Test;

public class ToWaitForTest {
    
    @Test
    public void testSanity1() {
        ToWaitFor t = new ToWaitFor();
        Assert.assertFalse(t.hasHappened());
    }
    
    @Test
    public void testSanity2() {
        ToWaitFor t = new ToWaitFor();
        t.reportHappened();
        Assert.assertTrue(t.hasHappened());
    }
    
    @Test
    public void testWithThreads1() throws InterruptedException {
        final ToWaitFor t1 = new ToWaitFor();
        
        Thread thread = new Thread(new Runnable() {
            public void run() {
                t1.reportHappened();
            }
        });
        
        thread.start();
        sleep(20);
        
        boolean didHappen = t1.waitForIt(1000);
        
        Assert.assertEquals("did happen?", true, didHappen);
    }
    
    @Test
    public void testWithThreads2() throws InterruptedException {
        final ToWaitFor t1 = new ToWaitFor();
        
        Thread thread = new Thread(new Runnable() {
            public void run() {
                sleep(20);
                t1.reportHappened();
            }
        });
        
        thread.start();
        boolean didHappen = t1.waitForIt(1000);
        
        Assert.assertEquals("did happen?", true, didHappen);
    }
    
    @Test
    public void testInterrupt() throws InterruptedException {
        final ToWaitFor t1 = new ToWaitFor();
        
        Thread thread = new Thread(new Runnable() {
            public void run() {
                boolean happened = t1.waitForIt(Long.MAX_VALUE);
                if (happened) {
                    throw new AssertionError("ToWaitForTest, unexpected!");
                }
            }
        });
        
        thread.start();
        thread.interrupt();
        thread.join(1000);
        
        Assert.assertEquals(false, thread.isAlive());
    }
    
    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // empty;
        }
    }
}
