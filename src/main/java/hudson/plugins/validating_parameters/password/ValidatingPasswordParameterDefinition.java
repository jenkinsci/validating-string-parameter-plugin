/*
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi, Luca Domenico Milanesio, Tom Huybrechts
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.plugins.validating_parameters.password;

import com.google.common.base.Strings;
import hudson.Extension;
import hudson.model.Failure;
import hudson.model.ParameterValue;
import hudson.model.PasswordParameterDefinition;
import hudson.model.PasswordParameterValue;
import hudson.plugins.validating_parameters.common.ValidatedParameterDescriptor;
import hudson.plugins.validating_parameters.utils.JsEscapingUtils;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import java.util.regex.Pattern;

/**
 * Password based parameter that supports setting a regular expression to validate the
 * user's entered value, giving real-time feedback on the value.
 *
 * @author Krzysztof Reczek
 * @since 2.5
 * @see {@link PasswordParameterDefinition}
 */
public class ValidatingPasswordParameterDefinition extends PasswordParameterDefinition {

    private static final long serialVersionUID = 1L;

    private String defaultValue;
    private String regex;
    private String failedValidationMessage;

    @DataBoundConstructor
    public ValidatingPasswordParameterDefinition(
            String name, String defaultValue, String regex, String failedValidationMessage, String description) {
        super(name, defaultValue, description);
        this.defaultValue = defaultValue;
        this.regex = regex;
        this.failedValidationMessage = failedValidationMessage;
    }

    public ValidatingPasswordParameterDefinition(
            String name, String defaultValue, String regex, String failedValidationMessage) {
        this(name, defaultValue, regex, failedValidationMessage, null);
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getRegex() {
        return regex;
    }

    public String getJsEncodedRegex() {
        return JsEscapingUtils.jsEscape(regex);
    }

    public String getFailedValidationMessage() {
        return failedValidationMessage;
    }

    public String getJsEncodedFailedValidationMessage() {
        return JsEscapingUtils.jsEscape(failedValidationMessage);
    }

    @Override
    public ValidatingPasswordParameterValue getDefaultParameterValue() {
        return new ValidatingPasswordParameterValue(getName(), defaultValue, getRegex(), getDescription());
    }

    @CheckForNull
    @Override
    public ParameterValue createValue(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return new ValidatingPasswordParameterValue(getName(), defaultValue, regex, getDescription());
        }

        ValidatingPasswordParameterValue passwordValue =
                new ValidatingPasswordParameterValue(getName(), value, regex, getDescription());
        if (!Pattern.matches(regex, passwordValue.getValue().getPlainText())) {
            throw new Failure(failedValidationMessage);
        }

        return passwordValue;
    }

    @CheckForNull
    @Override
    public PasswordParameterValue createValue(StaplerRequest req, JSONObject jo) {
        ValidatingPasswordParameterValue value = req.bindJSON(ValidatingPasswordParameterValue.class, jo);
        String reqValue = value.getValue().getPlainText();

        if (Strings.isNullOrEmpty(reqValue) || DEFAULT_VALUE.equals(reqValue)) {
            return getDefaultParameterValue();
        }

        if (!Pattern.matches(regex, reqValue)) {
            throw new Failure(failedValidationMessage);
        }

        value.setDescription(getDescription());
        value.setRegex(regex);
        return value;
    }

    @Extension
    @Symbol("validatingPassword")
    public static class DescriptorImpl extends ValidatedParameterDescriptor {

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Validating Password Parameter";
        }

        @Override
        public String getHelpFile() {
            return "/plugin/validating-string-parameter/help-password.html";
        }
    }
}
