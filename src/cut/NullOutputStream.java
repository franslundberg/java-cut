package cut;

import java.io.IOException;

/**
 * Does nothing with the bytes written to it.
 * Sometimes useful for testing.
 * 
 * @author Frans Lundberg
 */
public class NullOutputStream {
	public void write(int b) throws IOException {}
    public void write(byte[] bytes) {}
    public void write(byte[] bytes, int length, int offset) {}
}
