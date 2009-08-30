package hudson.plugins.validating_string_parameter;

import hudson.Extension;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.StringParameterValue;
import hudson.util.FormValidation;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

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
    
    public StringParameterValue getDefaultParameterValue() {
        StringParameterValue v = new StringParameterValue(getName(), defaultValue, getDescription());
        return v;
    }

    @Extension
    public static class DescriptorImpl extends ParameterDescriptor {
        @Override
        public String getDisplayName() {
            return "Validating String Parameter";
        }

        @Override
        public String getHelpFile() {
            return "/plugin/validating-string-parameter/help.html";
        }
        
        /**
         * Chcek the regular expression entered by the user
         */
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
        public FormValidation doValidate(@QueryParameter("regex") String regex, 
        		@QueryParameter("failedValidationMessage") final String failedValidationMessage,
        		@QueryParameter("value") final String value) {
        	try {
        		if (Pattern.matches(regex, value)) {
        			return FormValidation.ok();
        		} else {
        			return failedValidationMessage == null || "".equals(failedValidationMessage) ?
        					FormValidation.error("Value entered does not match regular expression: " + regex)
        					: FormValidation.error(failedValidationMessage);
        		}
        	} catch (PatternSyntaxException pse) {
        		return FormValidation.error("Invalid regular expression [" + regex + "]: " + pse.getDescription());
        	}
        }
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
        StringParameterValue value = req.bindJSON(StringParameterValue.class, jo);
        value.setDescription(getDescription());
        return value;
    }

	@Override
	public ParameterValue createValue(StaplerRequest req) {
        String[] value = req.getParameterValues(getName());
        if (value == null) {
        	return getDefaultParameterValue();
        } else if (value.length != 1) {
        	throw new IllegalArgumentException("Illegal number of parameter values for " + getName() + ": " + value.length);
        } else 
        	return new StringParameterValue(getName(), value[0], getDescription());
	}
}
