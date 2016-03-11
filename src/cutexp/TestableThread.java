package cutexp;

/**
 * An instance of this class can be used to create deterministic tests for 
 * multi-threaded software. Use it by inserting statements like this:
 * 
 * assert TestableThread.breakpoint("breakA");
 * 
 * in the code to be tested. Then run the code using threads that
 * are TestableThread instances. Enable assertions (-ea).
 * The threads can be controlled with go-to-break-point functionality.
 * 
 * @author Frans Lundberg
 */
public class TestableThread extends Thread {
    private final Object sync = new Object();
    private volatile String breakName;
    
    public TestableThread(Runnable r) {
        super(r);
    }
    
    /**
     * Run thread until it hits the named breakpoint or exits.
     */
    public void goTo(String breakName) {
        synchronized (sync) {
            this.breakName = breakName;
            sync.notifyAll();
        }
        
        if (getState() == Thread.State.NEW) {
            start();
        }
    }
    
    /**
     * Run thread, not stopping at any break points.
     */
    public void go() {
        goTo(null);
    }
    
    public static boolean breakpoint(String breakName) {
        if (breakName == null) {
            throw new IllegalArgumentException("breakName == null not allowed");
        }
        
        Thread thread = Thread.currentThread();
        if (thread instanceof TestableThread) {
            TestableThread tt = (TestableThread) thread;
            synchronized (tt.sync) {
                while (tt.breakName != null && tt.breakName.equals(breakName)) {
                    try {
                        tt.sync.wait();
                    } catch (InterruptedException e) {
                        throw new Error("not expected: " + e);
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * Try running with and without -ea.
     */
    public static void main(String[] args) throws InterruptedException {
        TestableThread t1 = new TestableThread(new Runnable() {
            public void run() {
                for (int i = 0; i < 3; i++) {
                    System.out.println("t1: Before " + i);
                    assert TestableThread.breakpoint("break-" + i);
                    System.out.println("t1: After " + i);
                    System.out.println();
                }
            }
        });
        
        System.out.println("m: Go to break-0.");
        t1.goTo("break-0");
        Thread.sleep(2000);
        
        System.out.println("m: Go to break-2.");
        t1.goTo("break-2");
        Thread.sleep(2000);
        
        System.out.println("m: go()");
        t1.go();
        
        System.out.println("m: Done!");
    }
}
