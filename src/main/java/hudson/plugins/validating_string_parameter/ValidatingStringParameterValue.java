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

import com.google.common.base.Objects;
import hudson.AbortException;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.ParameterValue;
import hudson.model.StringParameterValue;
import hudson.tasks.BuildWrapper;
import java.io.IOException;
import java.util.regex.Pattern;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * {@link ParameterValue} created from {@link ValidatingStringParameterDefinition}.
 *
 * @author Peter Hayes
 * @since 1.0
 */
public class ValidatingStringParameterValue extends StringParameterValue {
    private String regex;

    @DataBoundConstructor
    public ValidatingStringParameterValue(String name, String value) {
        this(name, value, null, null);
    }

    public ValidatingStringParameterValue(String name, String value, String regex, String description) {
        super(name, value, description);
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public BuildWrapper createBuildWrapper(AbstractBuild<?, ?> build) {
        if (regex != null && !Pattern.matches(regex, value)) {
            // abort the build within BuildWrapper
            return new BuildWrapper() {
                @Override
                public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
                    throw new AbortException("Invalid value for parameter [" + getName() + "] specified: " + value);
                }
            };
        } else {
            return null;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), regex);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ValidatingStringParameterValue other = (ValidatingStringParameterValue) obj;
        if (regex == null) {
            return other.regex == null;
        } else {
            return regex.equals(other.regex);
        }
    }

    @Override
    public String toString() {
        return "(ValidatingStringParameterValue) " + getName() + "='" + value + "'";
    }
}
