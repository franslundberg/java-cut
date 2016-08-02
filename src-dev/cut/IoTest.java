package cut;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class IoTest {
    
    private static byte[] useCopy(byte[] inputBytes) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(inputBytes);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        Io.copy(in, out);
        in.close();
        out.close();
        
        return out.toByteArray();
    }
    
    @Test
    public void testCopy1() throws IOException {
        Assert.assertArrayEquals(new byte[0], useCopy(new byte[0]));
    }
    
    @Test
    public void testCopy2() throws IOException {
        byte[] arr = new byte[]{1, 2, 10};
        Assert.assertArrayEquals(new byte[]{1, 2, 10}, useCopy(arr));
    }
    
    @Test
    public void testCopy3() throws IOException {
        // Something bigger, exceeding buffer size.
        
        byte[] arr = new byte[12*1000];
        Assert.assertArrayEquals(new byte[12*1000], useCopy(arr));
    }
    
    @Test
    public void testStreamToBytes() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(new byte[]{1, 2, 3});
        byte[] bytes = Io.streamToBytes(in);
        Assert.assertArrayEquals(new byte[]{1, 2, 3}, bytes);
    }
}
