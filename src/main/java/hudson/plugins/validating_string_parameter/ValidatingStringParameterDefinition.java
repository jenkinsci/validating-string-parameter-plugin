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
package hudson.plugins.validating_string_parameter;

import hudson.AbortException;
import hudson.Extension;
import hudson.cli.CLICommand;
import hudson.model.Failure;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.util.FormValidation;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.verb.POST;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * String based parameter that supports setting a regular expression to validate the
 * user's entered value, giving real-time feedback on the value.
 * 
 * @author Peter Hayes
 * @since 1.0
 * @see ParameterDefinition
 */
public class ValidatingStringParameterDefinition extends ParameterDefinition {

    private static final long serialVersionUID = 1L;
    private final String defaultValue;
    private final String regex;
    private final String failedValidationMessage;

    @DataBoundConstructor
    public ValidatingStringParameterDefinition(String name, String defaultValue, String regex, String failedValidationMessage, String description) {
        super(name, description);
        this.defaultValue = defaultValue;
        this.regex = regex;
        this.failedValidationMessage = failedValidationMessage;
    }

    public ValidatingStringParameterDefinition(String name, String defaultValue, String regex, String failedValidationMessage) {
        this(name, defaultValue, regex, failedValidationMessage, null);
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getRegex() {
        return regex;
    }

    public String getFailedValidationMessage() {
        return failedValidationMessage;
    }

    public String getRootUrl() {
        return Jenkins.get().getRootUrl();
    }

    @Override
    public ValidatingStringParameterValue getDefaultParameterValue() {
        ValidatingStringParameterValue v = new ValidatingStringParameterValue(getName(), defaultValue, getRegex(), getDescription());
        return v;
    }

    @Extension @Symbol("validatingString")
    public static class DescriptorImpl extends ParameterDescriptor {

        @Override
        public String getDisplayName() {
            return "Validating String Parameter";
        }

        /**
         * Check the regular expression entered by the user
         */
        @POST
        public FormValidation doCheckRegex(@QueryParameter final String value) {
            try {
                Pattern.compile(value);
                return FormValidation.ok();
            } catch (PatternSyntaxException pse) {
                return FormValidation.error("Invalid regular expression: " + pse.getDescription());
            }
        }

        /**
         * Called to validate the passed user entered value against the configured regular expression.
         */
        @POST
        public FormValidation doValidate(@QueryParameter("regex") String regex,
                @QueryParameter("failedValidationMessage") final String failedValidationMessage,
                @QueryParameter("value") final String value) {
            try {
                if (Pattern.matches(regex, value)) {
                    return FormValidation.ok();
                } else {
                    return failedValidationMessage == null || "".equals(failedValidationMessage)
                            ? FormValidation.error("Value entered does not match regular expression: " + regex)
                            : FormValidation.error(failedValidationMessage);
                }
            } catch (PatternSyntaxException pse) {
                return FormValidation.error("Invalid regular expression [" + regex + "]: " + pse.getDescription());
            }
        }
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        ValidatingStringParameterValue value = req.bindJSON(ValidatingStringParameterValue.class, jo);
        String req_value = value.getValue();
        
        if (!Pattern.matches(regex, req_value)) {
            throw new Failure("Invalid value for parameter [" + getName() + "] specified: " + req_value);
        }
        
        value.setDescription(getDescription());
        value.setRegex(regex);
        return value;
    }

    @Override
    public ParameterValue createValue(StaplerRequest req) {
        String[] value = req.getParameterValues(getName());
        
        if (value == null || value.length < 1) {
            return getDefaultParameterValue();
        } else {
            if (!Pattern.matches(regex, value[0])) {
                throw new Failure("Invalid value for parameter [" + getName() + "] specified: " + value[0]);
            }
            return new ValidatingStringParameterValue(getName(), value[0], regex, getDescription());
        }
    }

    @Override
    public ParameterValue createValue(CLICommand command, String value) throws IOException, InterruptedException {
        if (value == null || value.length() == 0) {
            return getDefaultParameterValue();
        } else {
            if (!Pattern.matches(regex, value)) {
                throw new AbortException("Invalid value for parameter [" + getName() + "] specified: " + value);
            }
            return new ValidatingStringParameterValue(getName(), value, regex, getDescription());
        }
    }
}
