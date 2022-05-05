import com.google.common.primitives.Longs;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author Lion Wagner
 */
public class ByteConverTest {

    private static byte[] longToBytes(long l) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(l);
        return buffer.array();
    }


    @Test
    public void defaultTest() {
        long Long = System.currentTimeMillis();
        var b1 = Longs.toByteArray(Long);
        var b2 = longToBytes(Long);
        System.out.println(Long);
        System.out.println(Arrays.toString(b1));
        System.out.println(Arrays.toString(b2));
    }
}
