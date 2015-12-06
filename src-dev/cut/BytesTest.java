package cut;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

public class BytesTest {
    
    @Test
    public void testFloatLE() {
        float[] values = new float[] {1.0f, 0.0f, Float.MIN_VALUE, Float.MAX_VALUE, Float.NaN, 
                123.45f, -0.0012f};
        byte[] array = new byte[10 + values.length];
        
        for (int i = 0; i < values.length; i++) {
            Bytes.floatToBytesLE(values[i], array, i);
            float d = Bytes.bytesToFloatLE(array, i);
            
            if (Float.isNaN(values[i]) && Float.isNaN(d)) {
                continue;
            }
            
            Assert.assertTrue("values[i]:" + values[i] + ", d:" + d, values[i] == d);
        }
    }
    
    @Test
    public void testDoubleLE() {
        double[] values = new double[] {1.0, 0.0, Double.MIN_VALUE, Double.MAX_VALUE, Double.NaN, 
                123.45, -0.0012};
        byte[] array = new byte[10 + values.length];
        
        for (int i = 0; i < values.length; i++) {
            Bytes.doubleToBytesLE(values[i], array, i);
            double d = Bytes.bytesToDoubleLE(array, i);
            
            if (Double.isNaN(values[i]) && Double.isNaN(d)) {
                continue;
            }
            
            Assert.assertTrue(values[i] == d);
        }
    }
    
    @Test
    public void testShortLE() {
        short[] shorts = new short[] {0, Short.MIN_VALUE, Short.MAX_VALUE, 12345, -1, -32000, 32000};
        byte[] array = new byte[10 + shorts.length];
        
        for (int i = 0; i < shorts.length; i++) {
            short s = shorts[i];
            Bytes.shortToBytesLE(s, array, i);
            short s2 = Bytes.bytesToShortLE(array, i);
            Assert.assertEquals(s, s2);
        }
    }
    
    @Test
    public void testLongsToBytesLE() {
        long[] longs = new long[] {123, 111222333444L, -1000};
        byte[] bytes1 = new byte[longs.length * 8];
        ByteBuffer buffer = ByteBuffer.wrap(bytes1);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.asLongBuffer().put(longs);
        
        byte[] bytes2 = Bytes.longsToBytesLE(longs);
        
        Assert.assertArrayEquals(bytes1, bytes2);
    }
    
    
    @Test
    public void testLongsToBytesLE2() {
        // Using array offsets.
        
        long[] longs = new long[] {123, 111222333444L, -1000};
        byte[] bytes = new byte[40];
        
        Bytes.longsToBytesLE(longs, 1, 2, bytes, 19);
        
        Assert.assertEquals(111222333444L, Bytes.bytesToLongLE(bytes, 19));
        Assert.assertEquals(-1000, Bytes.bytesToLongLE(bytes, 19+8));
    }
 
    @Test
    public void testLongToBytesBE() {
        long long1;
        byte[] bytes1 = new byte[12];
        byte[] bytes2 = new byte[12];
        ByteBuffer buffer = ByteBuffer.wrap(bytes2, 3, 8);

        buffer.order(ByteOrder.BIG_ENDIAN);
        long1 = (long) ((Math.random() * 2.0 - 1.0) * Long.MAX_VALUE);
        Bytes.longToBytesBE(long1, bytes1, 3);
        buffer.putLong(3, long1);
        for (int i = 0; i < 12; i++) {
            if (bytes1[i] != bytes2[i]) {
                throw new RuntimeException("i=" + i);
            }
        }
    }
    
    @Test
    public void testLongToBytesLE() {
        long long1;
        byte[] bytes1 = new byte[12];
        byte[] bytes2 = new byte[12];
        ByteBuffer buffer = ByteBuffer.wrap(bytes2, 3, 8);

        buffer.order(ByteOrder.LITTLE_ENDIAN);
        long1 = (long) ((Math.random() * 2.0 - 1.0) * Long.MAX_VALUE);
        Bytes.longToBytesLE(long1, bytes1, 3);
        buffer.putLong(3, long1);
        for (int i = 0; i < 12; i++) {
            if (bytes1[i] != bytes2[i]) {
                throw new RuntimeException("i=" + i);
            }
        }
    }
    
    @Test
    public void testLongBE() {
        long long3 = Long.MAX_VALUE - 1000;
        long long4 = 0;
        byte[] temp = new byte[16];
        Bytes.longToBytesBE(28000000L, temp, 0);
        Bytes.longToBytesBE(long3, temp, 8);
        long4 = Bytes.bytesToLongBE(temp, 8);

        if (long3 != long4) {
            throw new RuntimeException("long3 != long4, " + long3 + " != "
                    + long4);
        }
    }

    @Test
    public void testLongBE2() {
        long long1, long2;
        byte[] bytes = new byte[12];

        for (int i = 0; i < 10; i++) {
            long1 = (long) ((Math.random() * 2.0 - 1.0) * Long.MAX_VALUE);
            Bytes.longToBytesBE(long1, bytes, 3);
            long2 = Bytes.bytesToLongBE(bytes, 3);
            if (long1 != long2) {
                throw new RuntimeException(long1 + " != " + long2 + ", i=" + i);
            }
        }
    }
    
    @Test
    public void testLongLE() {
        long long1, long2;
        byte[] bytes = new byte[12];

        for (int i = 0; i < 10; i++) {
            long1 = (long) ((Math.random() * 2.0 - 1.0) * Long.MAX_VALUE);
            Bytes.longToBytesLE(long1, bytes, 3);
            long2 = Bytes.bytesToLongLE(bytes, 3);
            if (long1 != long2) {
                throw new RuntimeException(long1 + " != " + long2 + ", i=" + i);
            }
        }
    }

    @Test
    public void testLongLE2() {
        long long1, long2;
        byte[] bytes = new byte[110];
        Random random = new Random(0);
        int offset;
        
        for (int i = 0; i < 100; i++) {
            offset = (i % (bytes.length - 10));
            long1 = random.nextLong();
            Bytes.longToBytesLE(long1, bytes, offset);
            long2 = Bytes.bytesToLongLE(bytes, offset);
            if (long1 != long2) {
                throw new RuntimeException(long1 + " != " + long2 + ", i=" + i);
            }
        }
    }
    
    @Test
    public void testIntLE() {
        Random r = new Random(0);
        byte[] buffer = new byte[110];
        
        for (int i = 0; i < 100; i++) {
            int myInt = r.nextInt();
            int offset = i % 100;
            Bytes.intToBytesLE(myInt, buffer, offset);
            Assert.assertEquals("i=" + i, myInt, Bytes.bytesToIntLE(buffer, offset));
        }
    }
    
    @Test
    public void testIntBE() {
        Random r = new Random(0);
        byte[] buffer = new byte[110];
        
        for (int i = 0; i < 100; i++) {
            int myInt = r.nextInt();
            int offset = i % 100;
            Bytes.intToBytesBE(myInt, buffer, offset);
            Assert.assertEquals("i=" + i, myInt, Bytes.bytesToIntBE(buffer, offset));
        }
    }
    

    @Test
    public void testUShortLE() {
        byte[] bytes = new byte[5];
        int value = (int) (Math.random() * (1 << 16));
        Bytes.ushortToBytesLE(value, bytes, 1);
        int value1 = Bytes.bytesToUShortLE(bytes, 1);
        if (value1 != value) {
            throw new RuntimeException(value1 + " != " + value);
        }
    }

    @Test
    public void testUShortLE2() {
        for (int i = 0; i < 20; i++) {
            testUShortLE();
        }
    }
    
    @Test
    public void testUnsigned() {
        Assert.assertEquals(123, Bytes.unsigned((byte) 123));
        Assert.assertEquals(255, Bytes.unsigned((byte) -1));
        Assert.assertEquals(127, Bytes.unsigned((byte) 127));
        Assert.assertEquals(128, Bytes.unsigned((byte) -128));
    }
}
