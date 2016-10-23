package cut;

import org.junit.Assert;
import org.junit.Test;

public class ByteArraysTest {
    @Test
    public void testConcat() {
        byte[] a1 = new byte[]{1};
        byte[] a2 = new byte[]{2, 3};
        Assert.assertArrayEquals(new byte[]{1, 2, 3}, ByteArrays.concat(a1, a2));
    }
    
    @Test
    public void testRange() {
        byte[] a1 = new byte[]{1, 2, 3, 4, 5, 6};
        Assert.assertArrayEquals(new byte[]{2, 3}, ByteArrays.range(a1, 1, 3));
    }
    
    @Test
    public void testIsPrefix1() {
        Assert.assertEquals(true, ByteArrays.isPrefix(new byte[]{1, 2}, new byte[]{1, 2, 3, 4}));
    }
    
    @Test
    public void testIsPrefix2() {
        Assert.assertEquals(false, ByteArrays.isPrefix(new byte[]{1, 9}, new byte[]{1, 2, 3, 4}));
    }
    
    @Test
    public void testIsPrefix3() {
        Assert.assertEquals(false, ByteArrays.isPrefix(new byte[]{1, 2, 3}, new byte[]{1, 2}));
    }
}
