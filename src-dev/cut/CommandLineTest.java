package cut;

import org.junit.Assert;
import org.junit.Test;

public class CommandLineTest {
    
    @Test
    public void testNoOptions() {
        CommandLine line = new CommandLine(new String[0]);
        Assert.assertEquals(-1, line.valueCount("opt"));
        Assert.assertEquals(0, line.optionCount());
    }
    
    @Test
    public void testOneOption() {
        CommandLine line = new CommandLine(new String[]{"-groups", "unit"});
        
        Assert.assertEquals("valueCount", 1, line.valueCount("groups"));
        Assert.assertEquals("optionCount", 1, line.optionCount());
        Assert.assertEquals("unit", line.getOne("groups"));
        Assert.assertEquals("unit", line.get("groups")[0]);
    }
    
    @Test
    public void testMultipleValues() {
        CommandLine line = new CommandLine(new String[]{"-groups", "unit", "f"});
        
        Assert.assertEquals(2, line.valueCount("groups"));
        Assert.assertEquals(1, line.optionCount());
        Assert.assertEquals("getOne", "unit", line.getOne("groups"));
        Assert.assertEquals("get0", "unit", line.get("groups")[0]);
        Assert.assertEquals("get1", "f", line.get("groups")[1]);
    }
    
    @Test
    public void valuesWithNoOptionShouldBeIgnored() {
        CommandLine line = new CommandLine(new String[]{"value1", "value2"});
        Assert.assertEquals(0, line.optionCount());
    }
}
