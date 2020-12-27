package hudson.plugins.validating_string_parameter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ValidatingStringParameterValueTest {

    @Test
    public void equals() {
        ValidatingStringParameterValue v = new ValidatingStringParameterValue("DUMMY", "VALUE");
        assertEquals(v, v);
        v.setRegex("lol");
        ValidatingStringParameterValue v2 = new ValidatingStringParameterValue("DUMMY", "VALUE");
        assertEquals(v2, v);
        assertEquals(v2.hashCode(), v.hashCode());
    }
}
