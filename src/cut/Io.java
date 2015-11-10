package cut;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * IO utility functions.
 * 
 * @author Frans Lundberg
 */
public class Io {
    
    /**
     * Reads the whole input stream to memory and returns a byte array.
     * Note, be careful, this method allocates an unbound amount of memory,
     * don't use it for any untrusted input.
     * The input stream is not closed by this method.
     * 
     * @throws IOException 
     */
    public static byte[] toBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        copy(in, out);
        return out.toByteArray();
    }
    
    /**
     * Copies data from in to out as long as there is still data to read from
     * in. The streams are not closed by this method.
     */
    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer;
        int n;

        buffer = new byte[4 * 1024];
        while (true) {
            n = in.read(buffer);
            if (n == -1) {
                break;
            }
            out.write(buffer, 0, n);
        }
    }
}
