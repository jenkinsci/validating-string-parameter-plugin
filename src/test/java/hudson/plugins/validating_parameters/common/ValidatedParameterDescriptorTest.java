package hudson.plugins.validating_parameters.common;

import hudson.model.ParameterDefinition;
import hudson.util.FormValidation;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidatedParameterDescriptorTest {

    private ValidatedParameterDescriptor descriptor;

    @Before
    public void setUp() {
        descriptor = new TestParameterDefinition.DescriptorImpl();
    }

    @Test
    public void shouldAcceptCorrectRegexOnCheck() {
        // given
        String regex = "^[a-z]*$";

        // when
        FormValidation result = descriptor.doCheckRegex(regex);

        // then
        assertThat(result.kind).isEqualTo(FormValidation.Kind.OK);
        assertThat(result.getMessage()).isNull();
    }

    @Test
    public void shouldRecognizeInvalidRegexOnCheck() {
        // given
        String invalidRegex = "^[a-z*$";

        // when
        FormValidation result = descriptor.doCheckRegex(invalidRegex);

        // then
        assertThat(result.kind).isEqualTo(FormValidation.Kind.ERROR);
        assertThat(result.getMessage()).isEqualTo("Invalid regular expression: Unclosed character class");
    }

    @Test
    public void shouldPassValidationWithRegexMatchingString() {
        // given
        String regex = "^[a-z]*$";
        String value = "test";


        // when
        FormValidation result = descriptor.doValidate(regex, "", value);

        // then
        assertThat(result.kind).isEqualTo(FormValidation.Kind.OK);
        assertThat(result.getMessage()).isNull();
    }

    @Test
    public void shouldFailValidationWithRegexMismatchingStringAndCustomMessage() {
        // given
        String regex = "^[a-z]*$";
        String invalidValue = "000";
        String customFailureMessage = "Test failed";

        // when
        FormValidation result = descriptor.doValidate(regex, customFailureMessage, invalidValue);

        // then
        assertThat(result.kind).isEqualTo(FormValidation.Kind.ERROR);
        assertThat(result.getMessage()).isEqualTo(customFailureMessage);
    }

    @Test
    public void shouldFailValidationWithRegexMismatchingStringAndNullCustomMessage() {
        // given
        String regex = "^[a-z]*$";
        String invalidValue = "000";

        // when
        FormValidation result = descriptor.doValidate(regex, null, invalidValue);

        // then
        assertThat(result.kind).isEqualTo(FormValidation.Kind.ERROR);

        String expectedMessage = String.format("Value entered does not match regular expression: %s", regex);
        assertThat(result.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    public void shouldFailValidationWithRegexMismatchingStringAndBlankCustomMessage() {
        // given
        String regex = "^[a-z]*$";
        String invalidValue = "000";

        // when
        FormValidation result = descriptor.doValidate(regex, "", invalidValue);

        // then
        assertThat(result.kind).isEqualTo(FormValidation.Kind.ERROR);

        String expectedMessage = String.format("Value entered does not match regular expression: %s", regex);
        assertThat(result.getMessage()).isEqualTo(expectedMessage);
    }

    @Test
    public void shouldFailValidationWithInvalidRegex() {
        // given
        String invalidRegex = "^[a-z*$";
        String value = "test";

        // when
        FormValidation result = descriptor.doValidate(invalidRegex, "", value);

        // then
        assertThat(result.kind).isEqualTo(FormValidation.Kind.ERROR);

        String expectedMessage = String.format("Invalid regular expression [%s]: Unclosed character class", invalidRegex);
        assertThat(result.getMessage()).isEqualTo(expectedMessage);
    }

    private static abstract class TestParameterDefinition extends ParameterDefinition {

        private TestParameterDefinition(String name) {
            super(name);
        }

        private static class DescriptorImpl extends ValidatedParameterDescriptor { }
    }
}
