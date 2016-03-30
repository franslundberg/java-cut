package cut;

import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class CreateTest {
    @Test
    public void testSet() {
        Set<String> strings = Create.set("hello", "hi", "hej");
        Assert.assertEquals(3, strings.size());
        Assert.assertTrue(strings.contains("hej"));
    }
    
    @Test
    public void testList() {
        List<String> strings = Create.list("hello", "hi");
        Assert.assertEquals(2, strings.size());
        Assert.assertEquals("hi", strings.get(1));
    }
}
