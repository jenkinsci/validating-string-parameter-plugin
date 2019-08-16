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
package hudson.plugins.validating_parameters.common;

import hudson.model.ParameterDefinition;
import hudson.util.FormValidation;
import org.kohsuke.stapler.QueryParameter;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public abstract class ValidatedParameterDescriptor extends ParameterDefinition.ParameterDescriptor {

    /**
     * Check the regular expression entered by the user
     */
    public FormValidation doCheckRegex(
            @QueryParameter final String value) {
        try {
            Pattern.compile(value);
            return FormValidation.ok();
        } catch (PatternSyntaxException pse) {
            String message = String.format("Invalid regular expression: %s", pse.getDescription());
            return FormValidation.error(message);
        }
    }

    /**
     * Called to validate the passed user entered value against the configured regular expression.
     */
    public FormValidation doValidate(
            @QueryParameter("regex") String regex,
            @QueryParameter("failedValidationMessage") final String failedValidationMessage,
            @QueryParameter("value") final String value) {
        try {
            if (Pattern.matches(regex, value)) {
                return FormValidation.ok();
            } else {
                String message = failedValidationMessage == null || "".equals(failedValidationMessage)
                        ? String.format("Value entered does not match regular expression: %s", regex)
                        : failedValidationMessage;
                return FormValidation.error(message);
            }
        } catch (PatternSyntaxException pse) {
            String message = String.format("Invalid regular expression [%s]: %s", regex, pse.getDescription());
            return FormValidation.error(message);
        }
    }
}
