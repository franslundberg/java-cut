package cut;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    public static byte[] streamToBytes(InputStream in) throws IOException {
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
    

    /**
     * Writes an array of bytes to a file.
     * 
     * @param bytes The bytes to write to file.
     * @param filename The name of the file.
     * @throws IllegalArgumentException if either of the arguments is null or if filename is an 
     * empty string.
     * @throws IOException If there is an IO problem.
     */
    public static void bytesToFile(byte[] bytes, String filename) throws IOException {
        FileOutputStream out;

        if (bytes == null) {
            throw new IllegalArgumentException("bytes == null not allowed");
        }
        if (filename == null) {
            throw new IllegalArgumentException("filename == null not allowed");
        }
        if (filename.length() == 0) {
            throw new IllegalArgumentException(
                    "Empty string not allowed as filename.");
        }

        out = new FileOutputStream(filename);
        try {
            out.write(bytes);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // empty
                }
            }
        }
    }
    
    /**
     * Writes a string to UTF8 bytes in a file.
     */
    public static void stringToFile(String string, String filename) throws IOException {
        bytesToFile(string.getBytes("UTF-8"), filename);
    }
    

    public static byte[] fileToBytes(String filename) throws IOException {
        FileInputStream in;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        if (filename == null) {
            throw new IllegalArgumentException("filename == null not allowed");
        }
        
        if (filename.length() == 0) {
            throw new IllegalArgumentException(
                    "Empty string not allowed as filename.");
        }
        
        in = new FileInputStream(filename);
        try {
            Io.copy(in, out);
        } finally {
            in.close();
            out.close();
        }
        
        return out.toByteArray();
    }
    
    public static String fileToString(String filename) throws IOException {
        return new String(fileToBytes(filename), "UTF-8");
    }
    
    /**
     * Blocks until 'length' bytes have been read to 'dest'.
     */
    public static void readFully(InputStream in, byte[] dest, int offset, final int length) 
            throws IOException {
        int len = length;
        
        while (true) {
            int count = in.read(dest, offset, len);
            if (count == -1) {
                throw new EOFException("EOF reached");
            }
            
            offset += count;
            len -= count;
            if (len == 0) {
                break;
            }
        }
    }
}
