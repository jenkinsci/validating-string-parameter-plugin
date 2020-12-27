package hudson.plugins.validating_string_parameter;

import hudson.Functions;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ValidatingStringParameterDefinitionTest {
    @Test
    public void testSimpleConfiguration() throws Exception {
        ValidatingStringParameterDefinition d = new ValidatingStringParameterDefinition("DUMMY", "foo", ".+", "Your parameter does not match the regular expression!", "Some parameter");
        assertEquals("DUMMY", d.getName());
        assertEquals("foo", d.getDefaultValue());
        assertEquals(".+", d.getRegex());
        assertEquals("Your parameter does not match the regular expression!", d.getFailedValidationMessage());
        assertEquals("Some parameter", d.getDescription());
    }

    @Test
    public void testJavaScriptEncode() throws Exception {
        ValidatingStringParameterDefinition d = new ValidatingStringParameterDefinition("DUMMY", "foo", "\".+", "Your parameter does not match the regular expression!", "Some parameter");
        assertEquals("DUMMY", d.getName());
        assertEquals("foo", d.getDefaultValue());
        assertEquals("\".+", d.getRegex());
        assertEquals("\\\".+", Functions.jsStringEscape(d.getRegex()));
        assertEquals("Your parameter does not match the regular expression!", d.getFailedValidationMessage());
        assertEquals("Some parameter", d.getDescription());
    }
}