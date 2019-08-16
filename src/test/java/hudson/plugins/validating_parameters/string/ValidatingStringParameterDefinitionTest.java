package hudson.plugins.validating_parameters.string;

import hudson.model.Failure;
import hudson.model.ParameterValue;
import hudson.plugins.validating_parameters.utils.JsEscapingUtils;
import net.sf.json.JSONObject;
import org.junit.Test;
import org.kohsuke.stapler.StaplerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValidatingStringParameterDefinitionTest {

    private static final String DEF_NAME = "Name";
    private static final String DEF_DESCRIPTION = "Description";
    private static final String DEF_DEFAULT_VALUE = "foo";
    private static final String DEF_REGEX = "^[a-z]*$";
    private static final String DEF_MESSAGE = "Your parameter does not match the regular expression!";

    @Test
    public void shouldCreateValueFromStaplerRequestWithRegexMatchingString() {
        // given
        String inputValue = "val";
        StaplerRequest req = mock(StaplerRequest.class);
        when(req.getParameterValues(DEF_NAME)).thenReturn(new String[] {inputValue});

        // when
        ValidatingStringParameterDefinition d =
                new ValidatingStringParameterDefinition(
                        DEF_NAME, DEF_DEFAULT_VALUE, DEF_REGEX, DEF_MESSAGE, DEF_DESCRIPTION);
        ParameterValue value = d.createValue(req);

        // then
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(ValidatingStringParameterValue.class);

        ValidatingStringParameterValue specificValue = (ValidatingStringParameterValue)value;
        assertThat(specificValue.getValue()).isEqualTo(inputValue);
        assertThat(specificValue.getName()).isEqualTo(DEF_NAME);
        assertThat(specificValue.getDescription()).isEqualTo(DEF_DESCRIPTION);
        assertThat(specificValue.getRegex()).isEqualTo(DEF_REGEX);
    }

    @Test
    public void shouldReturnDefaultValueFromStaplerRequestWithNoValues() {
        // given
        StaplerRequest req = mock(StaplerRequest.class);
        when(req.getParameterValues(DEF_NAME)).thenReturn(new String[] {});

        // when
        ValidatingStringParameterDefinition d =
                new ValidatingStringParameterDefinition(
                        DEF_NAME, DEF_DEFAULT_VALUE, DEF_REGEX, DEF_MESSAGE, DEF_DESCRIPTION);
        ParameterValue value = d.createValue(req);

        // then
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(ValidatingStringParameterValue.class);

        ValidatingStringParameterValue specificValue = (ValidatingStringParameterValue)value;
        assertThat(specificValue.getValue()).isEqualTo(DEF_DEFAULT_VALUE);
    }

    @Test
    public void shouldReturnDefaultValueFromStaplerRequestWithNullValue() {
        // given
        StaplerRequest req = mock(StaplerRequest.class);
        when(req.getParameterValues(DEF_NAME)).thenReturn(null);

        // when
        ValidatingStringParameterDefinition d =
                new ValidatingStringParameterDefinition(
                        DEF_NAME, DEF_DEFAULT_VALUE, DEF_REGEX, DEF_MESSAGE, DEF_DESCRIPTION);
        ParameterValue value = d.createValue(req);

        // then
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(ValidatingStringParameterValue.class);

        ValidatingStringParameterValue specificValue = (ValidatingStringParameterValue) value;
        assertThat(specificValue.getValue()).isEqualTo(DEF_DEFAULT_VALUE);
    }

    @Test
    public void shouldThrowExceptionForStaplerRequestWithRegexMismatchingString() {
        // given
        String invalidInputValue = "000";
        StaplerRequest req = mock(StaplerRequest.class);
        when(req.getParameterValues(DEF_NAME)).thenReturn(new String[] {invalidInputValue});

        // when
        ValidatingStringParameterDefinition d =
                new ValidatingStringParameterDefinition(
                        DEF_NAME, DEF_DEFAULT_VALUE, DEF_REGEX, DEF_MESSAGE, DEF_DESCRIPTION);

        // then
        assertThatThrownBy(() -> d.createValue(req))
                .isInstanceOf(Failure.class)
                .hasMessage(DEF_MESSAGE);
    }

    @Test
    public void shouldCreateValueFromStaplerRequestAndJSONWithRegexMatchingString() {
        // given
        String inputValue = "val";
        ValidatingStringParameterValue inputParameterValue = new ValidatingStringParameterValue(DEF_NAME, inputValue);
        JSONObject json = new JSONObject();

        StaplerRequest req = mock(StaplerRequest.class);
        when(req.bindJSON(ValidatingStringParameterValue.class, json))
                .thenReturn(inputParameterValue);

        // when
        ValidatingStringParameterDefinition d =
                new ValidatingStringParameterDefinition(
                        DEF_NAME, DEF_DEFAULT_VALUE, DEF_REGEX, DEF_MESSAGE, DEF_DESCRIPTION);
        ParameterValue value = d.createValue(req, json);

        // then
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(ValidatingStringParameterValue.class);

        ValidatingStringParameterValue specificValue = (ValidatingStringParameterValue)value;
        assertThat(specificValue.getValue()).isEqualTo(inputValue);
        assertThat(specificValue.getName()).isEqualTo(DEF_NAME);
        assertThat(specificValue.getDescription()).isEqualTo(DEF_DESCRIPTION);
        assertThat(specificValue.getRegex()).isEqualTo(DEF_REGEX);
    }

    @Test
    public void shouldCreateValueFromStaplerRequestAndJSONWithNoString() {
        // given
        ValidatingStringParameterValue inputParameterValue = new ValidatingStringParameterValue(DEF_NAME, null);
        JSONObject json = new JSONObject();

        StaplerRequest req = mock(StaplerRequest.class);
        when(req.bindJSON(ValidatingStringParameterValue.class, json))
                .thenReturn(inputParameterValue);

        // when
        ValidatingStringParameterDefinition d =
                new ValidatingStringParameterDefinition(
                        DEF_NAME, DEF_DEFAULT_VALUE, DEF_REGEX, DEF_MESSAGE, DEF_DESCRIPTION);
        ParameterValue value = d.createValue(req, json);

        // then
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(ValidatingStringParameterValue.class);

        ValidatingStringParameterValue specificValue = (ValidatingStringParameterValue)value;
        assertThat(specificValue.getValue()).isEqualTo(DEF_DEFAULT_VALUE);
        assertThat(specificValue.getName()).isEqualTo(DEF_NAME);
        assertThat(specificValue.getDescription()).isEqualTo(DEF_DESCRIPTION);
        assertThat(specificValue.getRegex()).isEqualTo(DEF_REGEX);
    }

    @Test
    public void shouldCreateValueFromStaplerRequestAndJSONWithBlankString() {
        // given
        ValidatingStringParameterValue inputParameterValue = new ValidatingStringParameterValue(DEF_NAME, "");
        JSONObject json = new JSONObject();

        StaplerRequest req = mock(StaplerRequest.class);
        when(req.bindJSON(ValidatingStringParameterValue.class, json))
                .thenReturn(inputParameterValue);

        // when
        ValidatingStringParameterDefinition d =
                new ValidatingStringParameterDefinition(
                        DEF_NAME, DEF_DEFAULT_VALUE, DEF_REGEX, DEF_MESSAGE, DEF_DESCRIPTION);
        ParameterValue value = d.createValue(req, json);

        // then
        assertThat(value).isNotNull();
        assertThat(value).isInstanceOf(ValidatingStringParameterValue.class);

        ValidatingStringParameterValue specificValue = (ValidatingStringParameterValue)value;
        assertThat(specificValue.getValue()).isEqualTo(DEF_DEFAULT_VALUE);
        assertThat(specificValue.getName()).isEqualTo(DEF_NAME);
        assertThat(specificValue.getDescription()).isEqualTo(DEF_DESCRIPTION);
        assertThat(specificValue.getRegex()).isEqualTo(DEF_REGEX);
    }

    @Test
    public void shouldThrowExceptionForStaplerRequestAndJSONWithRegexMismatchingString() {
        // given
        String invalidInputValue = "000";
        ValidatingStringParameterValue inputParameterValue = new ValidatingStringParameterValue(DEF_NAME, invalidInputValue);
        JSONObject json = new JSONObject();

        StaplerRequest req = mock(StaplerRequest.class);
        when(req.bindJSON(ValidatingStringParameterValue.class, json))
                .thenReturn(inputParameterValue);

        // when
        ValidatingStringParameterDefinition d =
                new ValidatingStringParameterDefinition(
                        DEF_NAME, DEF_DEFAULT_VALUE, DEF_REGEX, DEF_MESSAGE, DEF_DESCRIPTION);

        // then
        assertThatThrownBy(() -> d.createValue(req, json))
                .isInstanceOf(Failure.class)
                .hasMessage(DEF_MESSAGE);
    }

    @Test
    public void shouldInstantiateDefinitionWithSimpleConfiguration() {
        // given / when
        ValidatingStringParameterDefinition d =
                new ValidatingStringParameterDefinition(
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
        ValidatingStringParameterDefinition d =
                new ValidatingStringParameterDefinition(
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
}
