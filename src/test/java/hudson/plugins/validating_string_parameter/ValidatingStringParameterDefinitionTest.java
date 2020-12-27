package hudson.plugins.validating_string_parameter;

import hudson.cli.CLICommand;
import hudson.cli.ConsoleCommand;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ValidatingStringParameterDefinitionTest {

    @Rule
    public JenkinsRule r = new JenkinsRule();

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
        assertEquals("\\\\\".+", d.getJsEncodedRegex());
        assertEquals("Your parameter does not match the regular expression!", d.getFailedValidationMessage());
        assertEquals("Some parameter", d.getDescription());
    }

    @Test
    public void testCLICommand() throws IOException, InterruptedException {
        ValidatingStringParameterDefinition d = new ValidatingStringParameterDefinition("DUMMY", "foo", "\".+", "Your parameter does not match the regular expression!", "Some parameter");
        CLICommand cliCommand = new ConsoleCommand();
        assertEquals(d.getDefaultParameterValue(),d.createValue(cliCommand, null));
        assertEquals(new ValidatingStringParameterValue("DUMMY", "\"hello"), d.createValue(cliCommand, "\"hello"));
    }
}