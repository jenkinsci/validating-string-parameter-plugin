/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hudson.plugins.validating_string_parameter;

import hudson.model.StringParameterValue;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 *
 * @author a237728
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

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 71;
        int result = super.hashCode();
        result = prime * result;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (ValidatingStringParameterValue.class != obj.getClass()) {
            return false;
        }
        ValidatingStringParameterValue other = (ValidatingStringParameterValue) obj;
        if (value == null) {
            if (other.value != null) {
                return false;
            }
        } else if (!value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "(ValidatingStringParameterValue) " + getName() + "='" + value + "'";
    }
}
