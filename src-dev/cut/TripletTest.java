package cut;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class TripletTest {
	@Test
	public void test0() {
		Triplet<String, Long, Date> t = new Triplet<String, Long, Date>("s", 111222333444L, new Date(100));
		Assert.assertEquals("s", t.getValue0());
		Assert.assertEquals(111222333444L, (long) t.getValue1());
		Assert.assertEquals(new Date(100), t.getValue2());
	}
}
