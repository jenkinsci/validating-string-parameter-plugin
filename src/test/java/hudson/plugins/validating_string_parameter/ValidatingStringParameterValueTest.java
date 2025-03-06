package hudson.plugins.validating_string_parameter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ValidatingStringParameterValueTest {

    @Test
    void equals() {
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
    void regex() {
        ValidatingStringParameterValue v = new ValidatingStringParameterValue("DUMMY", "VALUE");
        String regex = "FAKE_REGEX";
        v.setRegex(regex);
        assertEquals(regex, v.getRegex());
    }

    @Test
    void buildWrapper() {
        ValidatingStringParameterValue v = new ValidatingStringParameterValue("DUMMY", "VALUE");
        assertNull(v.createBuildWrapper(null));
        v.setRegex("abc");
        assertNotNull(v.createBuildWrapper(null));
    }
}
