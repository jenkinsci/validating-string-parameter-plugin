package hudson.plugins.validating_parameters.password;

import hudson.model.Failure;
import hudson.model.ParameterValue;
import hudson.plugins.validating_parameters.utils.JsEscapingUtils;
import hudson.util.Secret;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kohsuke.stapler.StaplerRequest;
import org.mockito.BDDMockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Secret.class)
public class ValidatingPasswordParameterDefinitionTest {

    private static final String DEF_NAME = "Name";
    private static final String DEF_DESCRIPTION = "Description";
    private static final String DEF_DEFAULT_VALUE = "foo";
    private static final String DEF_REGEX = "^[a-z]*$";
    private static final String DEF_MESSAGE = "Your parameter does not match the regular expression!";

    @Before
    public void setUp() {
        PowerMockito.mockStatic(Secret.class);
        mockSecretFor(DEF_DEFAULT_VALUE);
    }

    @Test
    public void shouldCreateValueFromRegexMatchingString() {
        // given
        String validInput = "val";
        mockSecretFor(validInput);

        // when
        ValidatingPasswordParameterDefinition d =
                new ValidatingPasswordParameterDefinition(
                        DEF_NAME, DEF_DEFAULT_VALUE, DEF_REGEX, DEF_MESSAGE, DEF_DESCRIPTION);
        ParameterValue value = d.createValue(validInput);

        // then
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(ValidatingPasswordParameterValue.class);

        ValidatingPasswordParameterValue specificValue = (ValidatingPasswordParameterValue)value;
        assertThat(specificValue.getValue().getPlainText()).isEqualTo(validInput);
        assertThat(specificValue.getName()).isEqualTo(DEF_NAME);
        assertThat(specificValue.getDescription()).isEqualTo(DEF_DESCRIPTION);
        assertThat(specificValue.getRegex()).isEqualTo(DEF_REGEX);
    }

    @Test
    public void shouldReturnDefaultValueFromNull() {
        // given / when
        ValidatingPasswordParameterDefinition d =
                new ValidatingPasswordParameterDefinition(
                        DEF_NAME, DEF_DEFAULT_VALUE, DEF_REGEX, DEF_MESSAGE, DEF_DESCRIPTION);
        ParameterValue value = d.createValue((String)null);

        // then
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(ValidatingPasswordParameterValue.class);

        ValidatingPasswordParameterValue specificValue = (ValidatingPasswordParameterValue)value;
        assertThat(specificValue.getValue().getPlainText()).isEqualTo(DEF_DEFAULT_VALUE);
    }

    @Test
    public void shouldReturnDefaultValueFromBlankString() {
        // given / when
        ValidatingPasswordParameterDefinition d =
                new ValidatingPasswordParameterDefinition(
                        DEF_NAME, DEF_DEFAULT_VALUE, DEF_REGEX, DEF_MESSAGE, DEF_DESCRIPTION);
        ParameterValue value = d.createValue("");

        // then
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(ValidatingPasswordParameterValue.class);

        ValidatingPasswordParameterValue specificValue = (ValidatingPasswordParameterValue) value;
        assertThat(specificValue.getValue().getPlainText()).isEqualTo(DEF_DEFAULT_VALUE);
    }

    @Test
    public void shouldThrowExceptionForRegexMismatchingString() {
        // given
        String invalidInput = "000";
        mockSecretFor(invalidInput);

        // when
        ValidatingPasswordParameterDefinition d =
                new ValidatingPasswordParameterDefinition(
                        DEF_NAME, DEF_DEFAULT_VALUE, DEF_REGEX, DEF_MESSAGE, DEF_DESCRIPTION);

        // then
        assertThatThrownBy(() -> d.createValue(invalidInput))
                .isInstanceOf(Failure.class)
                .hasMessage(DEF_MESSAGE);
    }

    @Test
    public void shouldCreateValueFromStaplerRequestAndJSONWithRegexMatchingString() {
        // given
        String inputValue = "val";
        mockSecretFor(inputValue);

        ValidatingPasswordParameterValue inputParameterValue =
                new ValidatingPasswordParameterValue(DEF_NAME, inputValue);
        JSONObject json = new JSONObject();

        StaplerRequest req = mock(StaplerRequest.class);
        when(req.bindJSON(ValidatingPasswordParameterValue.class, json))
                .thenReturn(inputParameterValue);

        // when
        ValidatingPasswordParameterDefinition d =
                new ValidatingPasswordParameterDefinition(
                        DEF_NAME, DEF_DEFAULT_VALUE, DEF_REGEX, DEF_MESSAGE, DEF_DESCRIPTION);
        ParameterValue value = d.createValue(req, json);

        // then
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(ValidatingPasswordParameterValue.class);

        ValidatingPasswordParameterValue specificValue = (ValidatingPasswordParameterValue)value;
        assertThat(specificValue.getValue().getPlainText()).isEqualTo(inputValue);
        assertThat(specificValue.getName()).isEqualTo(DEF_NAME);
        assertThat(specificValue.getDescription()).isEqualTo(DEF_DESCRIPTION);
        assertThat(specificValue.getRegex()).isEqualTo(DEF_REGEX);
    }

    @Test
    public void shouldCreateValueFromStaplerRequestAndJSONWithDefaultString() {
        // given
        String defaultPlaceholderValue = "<DEFAULT>";
        mockSecretFor(defaultPlaceholderValue);

        ValidatingPasswordParameterValue inputParameterValue =
                new ValidatingPasswordParameterValue(DEF_NAME, defaultPlaceholderValue);
        JSONObject json = new JSONObject();

        StaplerRequest req = mock(StaplerRequest.class);
        when(req.bindJSON(ValidatingPasswordParameterValue.class, json))
                .thenReturn(inputParameterValue);

        // when
        ValidatingPasswordParameterDefinition d =
                new ValidatingPasswordParameterDefinition(
                        DEF_NAME, DEF_DEFAULT_VALUE, DEF_REGEX, DEF_MESSAGE, DEF_DESCRIPTION);
        ParameterValue value = d.createValue(req, json);

        // then
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(ValidatingPasswordParameterValue.class);

        ValidatingPasswordParameterValue specificValue = (ValidatingPasswordParameterValue)value;
        assertThat(specificValue.getValue().getPlainText()).isEqualTo(DEF_DEFAULT_VALUE);
        assertThat(specificValue.getName()).isEqualTo(DEF_NAME);
        assertThat(specificValue.getDescription()).isEqualTo(DEF_DESCRIPTION);
        assertThat(specificValue.getRegex()).isEqualTo(DEF_REGEX);
    }

    @Test
    public void shouldCreateValueFromStaplerRequestAndJSONWithBlankString() {
        // given
        String blankInputValue = "";
        mockSecretFor(blankInputValue);

        ValidatingPasswordParameterValue inputParameterValue =
                new ValidatingPasswordParameterValue(DEF_NAME, blankInputValue);
        JSONObject json = new JSONObject();

        StaplerRequest req = mock(StaplerRequest.class);
        when(req.bindJSON(ValidatingPasswordParameterValue.class, json))
                .thenReturn(inputParameterValue);

        // when
        ValidatingPasswordParameterDefinition d =
                new ValidatingPasswordParameterDefinition(
                        DEF_NAME, DEF_DEFAULT_VALUE, DEF_REGEX, DEF_MESSAGE, DEF_DESCRIPTION);
        ParameterValue value = d.createValue(req, json);

        // then
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(ValidatingPasswordParameterValue.class);

        ValidatingPasswordParameterValue specificValue = (ValidatingPasswordParameterValue)value;
        assertThat(specificValue.getValue().getPlainText()).isEqualTo(DEF_DEFAULT_VALUE);
        assertThat(specificValue.getName()).isEqualTo(DEF_NAME);
        assertThat(specificValue.getDescription()).isEqualTo(DEF_DESCRIPTION);
        assertThat(specificValue.getRegex()).isEqualTo(DEF_REGEX);
    }

    @Test
    public void shouldThrowExceptionForStaplerRequestAndJSONWithRegexMismatchingString() {
        // given
        String invalidInputValue = "000";
        mockSecretFor(invalidInputValue);

        ValidatingPasswordParameterValue inputParameterValue =
                new ValidatingPasswordParameterValue(DEF_NAME, invalidInputValue);
        JSONObject json = new JSONObject();

        StaplerRequest req = mock(StaplerRequest.class);
        when(req.bindJSON(ValidatingPasswordParameterValue.class, json))
                .thenReturn(inputParameterValue);

        // when
        ValidatingPasswordParameterDefinition d =
                new ValidatingPasswordParameterDefinition(
                        DEF_NAME, DEF_DEFAULT_VALUE, DEF_REGEX, DEF_MESSAGE, DEF_DESCRIPTION);

        // then
        assertThatThrownBy(() -> d.createValue(req, json))
                .isInstanceOf(Failure.class)
                .hasMessage(DEF_MESSAGE);
    }

    @Test
    public void shouldInstantiateDefinitionWithSimpleConfiguration() {
        // given / when
        ValidatingPasswordParameterDefinition d =
                new ValidatingPasswordParameterDefinition(
                        DEF_NAME, DEF_DEFAULT_VALUE, DEF_REGEX, DEF_MESSAGE, DEF_DESCRIPTION);

        // then
        assertThat(d.getName()).isEqualTo(DEF_NAME);
        assertThat(d.getDescription()).isEqualTo(DEF_DESCRIPTION);
        assertThat(d.getDefaultValue()).isEqualTo(DEF_DEFAULT_VALUE);
        assertThat(d.getRegex()).isEqualTo(DEF_REGEX);
        assertThat(d.getJsEncodedRegex()).isEqualTo(DEF_REGEX);
        assertThat(d.getFailedValidationMessage()).isEqualTo(DEF_MESSAGE);
        assertThat(d.getJsEncodedFailedValidationMessage()).isEqualTo(DEF_MESSAGE);
    }

    @Test
    public void shouldEncodeDefinitionProperties() {
        // given
        String regex = "\".+";
        String message = "Your parameter does not match the regular expression: \".+";

        // when
        ValidatingPasswordParameterDefinition d =
                new ValidatingPasswordParameterDefinition(
                        DEF_NAME, DEF_DEFAULT_VALUE, regex, message, DEF_DESCRIPTION);

        // then
        assertThat(d.getName()).isEqualTo(DEF_NAME);
        assertThat(d.getDescription()).isEqualTo(DEF_DESCRIPTION);
        assertThat(d.getDefaultValue()).isEqualTo(DEF_DEFAULT_VALUE);
        assertThat(d.getRegex()).isEqualTo(regex);
        assertThat(d.getJsEncodedRegex()).isEqualTo(JsEscapingUtils.jsEscape(regex));
        assertThat(d.getFailedValidationMessage()).isEqualTo(message);
        assertThat(d.getJsEncodedFailedValidationMessage()).isEqualTo(JsEscapingUtils.jsEscape(message));
    }

    private void mockSecretFor(String value) {
        Secret s = mock(Secret.class);
        when(s.getPlainText()).thenReturn(value);
        BDDMockito.given(Secret.fromString(value))
                .willReturn(s);
    }
}
