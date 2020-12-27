package hudson.plugins.validating_string_parameter;

import hudson.model.ParametersDefinitionProperty;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class ValidatingStringParameterTest {

    @Rule
    public JenkinsRule r = new JenkinsRule();

    @Test
    public void pipeline() throws Exception {
        WorkflowJob p = r.createProject(WorkflowJob.class);
        assertThat(p.getProperty(ParametersDefinitionProperty.class), is(nullValue()));
        p.setDefinition(new CpsFlowDefinition("properties([\n" +
                "  parameters([\n" +
                "    validatingString(name: 'Test', description: 'Choose the name of your fileset that you want to create', regex: '^[^,.]$', failedValidationMessage: 'Please use integers without , or .')\n" +
                "  ])\n" +
                "])", true));
        r.buildAndAssertSuccess(p);
        assertThat(p.getProperty(ParametersDefinitionProperty.class), is(notNullValue()));
    }
}
