package hudson.plugins.validating_string_parameter;

import hudson.AbortException;
import hudson.cli.CLICommand;
import hudson.model.Failure;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.stapler.StaplerRequest2;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ValidatingStringParameterDefinitionTest {

    @Mock
    private StaplerRequest2 req;

    @Mock
    private CLICommand cliCommand;

    @Test
    void simpleConfiguration() {
        ValidatingStringParameterDefinition d = new ValidatingStringParameterDefinition("DUMMY", "foo", ".+", "Your parameter does not match the regular expression!", "Some parameter");
        assertEquals("DUMMY", d.getName());
        assertEquals("foo", d.getDefaultValue());
        assertEquals(".+", d.getRegex());
        assertEquals("Your parameter does not match the regular expression!", d.getFailedValidationMessage());
        assertEquals("Some parameter", d.getDescription());
        Mockito.when(req.getParameterValues("DUMMY")).thenReturn(new String[]{"test"});
        ValidatingStringParameterValue v1 = new ValidatingStringParameterValue("DUMMY", "test", ".+", "");
        assertEquals(v1, d.createValue(req));
        JSONObject jo = new JSONObject();
        ValidatingStringParameterValue v2 = new ValidatingStringParameterValue("DUMMY", "test2");
        Mockito.when(req.bindJSON(ValidatingStringParameterValue.class, jo)).thenReturn(v2);
        assertEquals(v2, d.createValue(req, jo));
    }

    @Test
    void failedCreateValueStapler() {
        ValidatingStringParameterDefinition d = new ValidatingStringParameterDefinition("DUMMY", "foo", ".+", "Your parameter does not match the regular expression!", "Some parameter");
        Mockito.when(req.getParameterValues("DUMMY")).thenReturn(new String[]{""});
        assertThrows(Failure.class, () -> d.createValue(req));
    }

    @Test
    void failedCreateValueJSONObject() {
        ValidatingStringParameterDefinition d = new ValidatingStringParameterDefinition("DUMMY", "foo", ".+", "Your parameter does not match the regular expression!", "Some parameter");
        ValidatingStringParameterValue v = new ValidatingStringParameterValue("DUMMY", "");
        JSONObject jo = new JSONObject();
        Mockito.when(req.bindJSON(ValidatingStringParameterValue.class, jo)).thenReturn(v);
        assertThrows(Failure.class, () -> d.createValue(req, jo));
    }

    @Test
    void cliCommand() throws IOException, InterruptedException {
        ValidatingStringParameterDefinition d = new ValidatingStringParameterDefinition("DUMMY", "foo", "\".+", "Your parameter does not match the regular expression!", "Some parameter");
        assertEquals(d.getDefaultParameterValue(),d.createValue(cliCommand, null));
        Mockito.verifyNoInteractions(cliCommand);
        ValidatingStringParameterValue v = new ValidatingStringParameterValue("DUMMY", "\"hello");
        v.setRegex("\".+");
        assertEquals(v, d.createValue(cliCommand, "\"hello"));
        Mockito.verifyNoInteractions(cliCommand);
    }

    @Test
    void cliCommandFailure() {
        ValidatingStringParameterDefinition d = new ValidatingStringParameterDefinition("DUMMY", "foo", "\".+", "Your parameter does not match the regular expression!", "Some parameter");
        assertThrows(AbortException.class, () -> d.createValue(cliCommand, "hello"));
        Mockito.verifyNoInteractions(cliCommand);
    }

    @Test
    void regexCheck() {
        ValidatingStringParameterDefinition.DescriptorImpl d = new ValidatingStringParameterDefinition.DescriptorImpl();
        assertEquals(FormValidation.Kind.OK, d.doCheckRegex("abc").kind);
        assertEquals(FormValidation.Kind.ERROR, d.doCheckRegex("(dddd").kind);
    }

    @Test
    void validationCheck() {
        ValidatingStringParameterDefinition.DescriptorImpl d = new ValidatingStringParameterDefinition.DescriptorImpl();
        assertEquals(FormValidation.Kind.OK, d.doValidate("abc", "failed", "abc").kind);
        assertEquals(FormValidation.Kind.ERROR, d.doValidate("abc", "failed", "").kind);
    }
}