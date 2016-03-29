package cut;

import org.junit.Test;

public class SeedGeneratorTest {

	@Test
	public void testSanity() {
		SeedGenerator g = new SeedGenerator();
        g.moreEntropy();
        g.moreEntropy();
        g.getSeed();
	}
}
