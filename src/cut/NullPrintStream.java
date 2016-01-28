package cut;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * A PrintStream that is like a black hole. It receives data, but nothing gets away from
 * it; it is consumed by the "black hole".
 * 
 * @author Frans Lundberg
 */
public class NullPrintStream extends PrintStream {
    public NullPrintStream() {
        super(new InnerNullOutputStream());
    }
    
    private static class InnerNullOutputStream extends OutputStream {
        public void write(int b) throws IOException {}
        public void write(byte[] bytes) {}
        public void write(byte[] bytes, int length, int offset) {}
    }
}
