package cut;

import org.junit.Assert;
import org.junit.Test;

public class HexTest {
    
    @Test
    public void testHex() {
        byte[][] arrays = new byte[][] {
                new byte[0],
                new byte[1],
                null,
                new byte[] {0, 123, (byte) 255, -1}
        };
        
        byte[] b = new byte[256];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) i;
        }
        arrays[2] = b;
        
        for (int i = 0; i < arrays.length; i++) {
            String hex = Hex.create(arrays[i]);
            byte[] back = Hex.toBytes(hex);
            Assert.assertArrayEquals(arrays[i], back);
        }
    }
}
