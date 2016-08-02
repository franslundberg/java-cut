package cut;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * This class is able to generate random data (not evenly distributed) with
 * enough entropy to use it as a seed for a random number generator. The reason
 * for creating this class is that /dev/random on some Linux systems may not 
 * generate new random data fast enough and block seeding for SecureRandom and Random classes.
 * 
 * @author Frans Lundberg
 */
public class SeedGenerator {
    // Note, the actual amount of entropy generated has not been studied.
    //
    
    private byte[] seed;
    private int offset;

    /**
     * Initializes seed generator with some entropy based on current time, JMX data, 
     * JVM memory available and so forth.
     */
    public SeedGenerator() {
        seed = new byte[128];
        addTimeData();
        addMachineData();
        addRandomOffset();
        addRuntimeData();
        addTimeData();
        addRandomOffset();
        addJmxData();
        addRuntimeData();
        addTimeData();
        addRuntimeData();
        addTimeData();
    }
    
    /**
     * Slower than constructor, but generates more entropy.
     */
    public synchronized void moreEntropy() {
        addTimeData();
        addRandomOffset();
        addRuntimeData();
        addJmxData();
        
        addTimeData();
        addRandomOffset();
        addRuntimeData();
        addJmxData();
        
        addTimeData();
        addRuntimeData();
        addStackTrace();
        addTimeData();
    }

    /**
     * Adds some entropy and returns a copy of the current seed data (128 byte array).
     */
    public synchronized byte[] getSeed() {
        addTimeData();
        addRandomOffset();
        return (byte[]) seed.clone();
    }

    /**
     * Adds data based on system clock and system counter.
     */
    private void addTimeData() {
        addData(System.nanoTime());
        addData(System.currentTimeMillis());
        addData(System.nanoTime());
    }
    
    /**
     * Adds data from Runtime state.
     */
    private void addRuntimeData() {
        Runtime rt = Runtime.getRuntime();
        addData(rt.freeMemory());
        addData(rt.maxMemory());
        addData(rt.totalMemory());
    }

    private void addStackTrace() {
        // The strange try-clause is needed for it to work with IKVM.
        
        Map<Thread, StackTraceElement[]> traces = null;
        
        try {
            traces = Thread.getAllStackTraces();
        } catch (Throwable e) {
            // IKVM (sometimes) says:
            // "cli.System.NotImplementedException: The requested feature is not implemented."
            
            if (!e.getClass().getSimpleName().equals("NotImplementedException")) {
                throw new RuntimeException(e);
            }
            
            return;
        }
        
        Set<Thread> threads = traces.keySet();
        
        addData(threads.size());
        
        for (Thread t : threads) {
            StackTraceElement[] elements = traces.get(t);
            addData(elements.length);
            
            for (StackTraceElement element : elements) {
                addData(element.getLineNumber());
                addData(element.getMethodName());
                addData(element.getFileName());
            }
        }
    }

    /**
     * Adds data specific to this machine.
     */
    private void addMachineData() {
        ByteArrayOutputStream bout;
        DataOutputStream out;
        Enumeration<Object> keys;

        bout = new ByteArrayOutputStream();
        out = new DataOutputStream(bout);

        try {
            Properties p = System.getProperties();
            keys = p.keys();
            while (keys.hasMoreElements()) {
                out.writeUTF(p.getProperty((String) keys.nextElement()));
            }
            out.close();
        } catch (IOException e) {
            throw new Error("neven happens, writing to memory");
        }

        addData(bout.toByteArray());
    }
    
    /**
     * Adds data from a JMX - java management extension objects.
     */
    private void addJmxData() {
        ClassLoadingMXBean clBean;
        ThreadMXBean tBean;
        long[] ids;

        clBean = ManagementFactory.getClassLoadingMXBean();
        tBean = ManagementFactory.getThreadMXBean();

        addData(clBean.getTotalLoadedClassCount());
        addData(clBean.getUnloadedClassCount());
        addData(clBean.getLoadedClassCount());

        addData(tBean.getCurrentThreadCpuTime());
        addData(tBean.getCurrentThreadUserTime());
        addData(tBean.getTotalStartedThreadCount());
        addData(tBean.getDaemonThreadCount());
        addData(tBean.getPeakThreadCount());
        addData(tBean.getThreadCount());
        addRandomOffset();

        ids = tBean.getAllThreadIds();
        for (int i = 0; i < ids.length; i++) {
            addData(ids[i]);
        }
    }
    
    private void addData(String s) {
        // 123456 is completely arbitrary.
        
        if (s == null) {
            addData(123456);
        } else {
            for (int i = 0; i < s.length(); i++) {
                addData(s.charAt(i));
            }
        }
    }

    private void addData(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            seed[offset] += bytes[i];
            offset++;
            if (offset >= seed.length) {
                offset = 0;
            }
        }
    }

    private void addData(long value) {
        byte[] bytes = new byte[8];
        Bytes.longToBytesBE(value, bytes, 0);
        addData(bytes);
    }

    /**
     * Just to avoid not writing much to certain offsets.
     */
    private void addRandomOffset() {
        offset = offset + (int) (Math.random() * 8.0);
        if (offset >= seed.length) {
            offset = offset % seed.length;
        }
    }
}
