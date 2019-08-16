package hudson.plugins.validating_parameters.utils;


import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JsEscapingUtilsTest {

    @Test
    public void shouldEscapeSlashes() {
        // given
        String input = "\\here is the text\\";

        // when
        String output = JsEscapingUtils.jsEscape(input);

        // then
        assertThat(output).isEqualTo("\\\\here is the text\\\\");
    }

    @Test
    public void shouldEscapeQuotes() {
        // given
        String input = "\"here is the text\"";

        // when
        String output = JsEscapingUtils.jsEscape(input);

        // then
        assertThat(output).isEqualTo("\\\\\"here is the text\\\\\"");
    }
}
