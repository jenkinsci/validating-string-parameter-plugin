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
package hudson.plugins.validating_parameters.string;

import com.google.common.base.Strings;
import hudson.Extension;
import hudson.model.Failure;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
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
 * String based parameter that supports setting a regular expression to validate the
 * user's entered value, giving real-time feedback on the value.
 * 
 * @author Peter Hayes
 * @since 1.0
 * @see {@link ParameterDefinition}
 */
public class ValidatingStringParameterDefinition extends ParameterDefinition {

    private static final long serialVersionUID = 1L;

    private String defaultValue;
    private String regex;
    private String failedValidationMessage;

    @DataBoundConstructor
    public ValidatingStringParameterDefinition(
            String name, String defaultValue, String regex, String failedValidationMessage, String description) {
        super(name, description);
        this.defaultValue = defaultValue;
        this.regex = regex;
        this.failedValidationMessage = failedValidationMessage;
    }

    public ValidatingStringParameterDefinition(
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
    public ValidatingStringParameterValue getDefaultParameterValue() {
        return new ValidatingStringParameterValue(getName(), defaultValue, getRegex(), getDescription());
    }

    @CheckForNull
    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        ValidatingStringParameterValue value = req.bindJSON(ValidatingStringParameterValue.class, jo);
        String reqValue = value.getValue();

        if (Strings.isNullOrEmpty(reqValue)) {
            return getDefaultParameterValue();
        }

        if (!Pattern.matches(regex, reqValue)) {
            throw new Failure(failedValidationMessage);
        }
        
        value.setDescription(getDescription());
        value.setRegex(regex);
        return value;
    }

    @CheckForNull
    @Override
    public ParameterValue createValue(StaplerRequest req) {
        String[] values = req.getParameterValues(getName());

        if (values == null || values.length < 1) {
            return getDefaultParameterValue();
        }

        String reqValue = values[0];
        if (!Pattern.matches(regex, reqValue)) {
            throw new Failure(failedValidationMessage);
        }

        return new ValidatingStringParameterValue(getName(), reqValue, regex, getDescription());
    }

    @Extension
    @Symbol("validatingString")
    public static class DescriptorImpl extends ValidatedParameterDescriptor {

        @Nonnull
        @Override
        public String getDisplayName() {
            return "Validating String Parameter";
        }

        @Override
        public String getHelpFile() {
            return "/plugin/validating-string-parameter/help-string.html";
        }
    }
}
