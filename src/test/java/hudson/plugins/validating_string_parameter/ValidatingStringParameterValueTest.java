package hudson.plugins.validating_string_parameter;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ValidatingStringParameterValueTest {

    @Test
    public void equals() {
        ValidatingStringParameterValue v = new ValidatingStringParameterValue("DUMMY", "VALUE");
        assertEquals(v, v);
        ValidatingStringParameterValue v2 = new ValidatingStringParameterValue("DUMMY", "VALUE");
        assertEquals(v2, v);
        assertEquals(v2.hashCode(), v.hashCode());
        v.setRegex("fake_regex");
        assertNotEquals(v2, v);
        assertNotEquals(v2.hashCode(), v.hashCode());
        v2.setRegex("fake_regex");
        assertEquals(v2, v);
        assertEquals(v2.hashCode(), v.hashCode());
    }

    @Test
    public void regex() {
        ValidatingStringParameterValue v = new ValidatingStringParameterValue("DUMMY", "VALUE");
        String regex = "FAKE_REGEX";
        v.setRegex(regex);
        assertEquals(regex, v.getRegex());
    }

    @Test
    public void buildWrapper() {
        ValidatingStringParameterValue v = new ValidatingStringParameterValue("DUMMY", "VALUE");
        assertNull(v.createBuildWrapper(null));
        v.setRegex("abc");
        assertNotNull(v.createBuildWrapper(null));
    }
}
