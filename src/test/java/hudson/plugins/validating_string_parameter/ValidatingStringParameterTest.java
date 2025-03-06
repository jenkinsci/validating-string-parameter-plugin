package hudson.plugins.validating_string_parameter;

import hudson.Functions;
import hudson.model.FreeStyleProject;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.ParametersDefinitionProperty;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import org.htmlunit.html.HtmlButton;
import org.htmlunit.html.HtmlPage;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@WithJenkins
class ValidatingStringParameterTest {

    @Test
    void pipeline(JenkinsRule r) throws Exception {
        WorkflowJob p = r.createProject(WorkflowJob.class);
        assertThat(p.getProperty(ParametersDefinitionProperty.class), is(nullValue()));
        p.setDefinition(new CpsFlowDefinition("""
                properties([
                  parameters([
                    validatingString(name: 'Test', description: 'Choose the name of your fileset that you want to create', regex: '^[^,.]$', failedValidationMessage: 'Please use integers without , or .')
                  ])
                ])""", true));
        r.buildAndAssertSuccess(p);
        assertThat(p.getProperty(ParametersDefinitionProperty.class), is(notNullValue()));
    }

    @Test
    void freestyle(JenkinsRule r) throws Exception {
        FreeStyleProject p = r.createProject(FreeStyleProject.class, "fs");
        p.getBuildersList().add(Functions.isWindows() ? new BatchFile("echo test") : new Shell("echo test"));

        ValidatingStringParameterDefinition def = new ValidatingStringParameterDefinition("name", "defVal", "\\w+", "Value must match the following pattern: \\w+");
        p.addProperty(new ParametersDefinitionProperty(def));

        try (JenkinsRule.WebClient wc = r.createWebClient()) {
            wc.setThrowExceptionOnFailingStatusCode(false);
            HtmlPage htmlPage = wc.goTo("job/fs/build");
            HtmlButton buildButton = htmlPage.querySelector(".jenkins-button--primary");
            buildButton.click();
        }

        r.waitUntilNoActivity();
        List<ParameterValue> parameters = p.getLastSuccessfulBuild().getAction(ParametersAction.class).getParameters();
        assertThat(parameters, is(not(empty())));
        ParameterValue parameterValue = parameters.get(0);
        assertThat(parameterValue, is(instanceOf(ValidatingStringParameterValue.class)));
        ValidatingStringParameterValue val = (ValidatingStringParameterValue) parameterValue;
        assertThat(val.getRegex(), is(equalTo("\\w+")));
        assertThat(val.getValue(), is(equalTo("defVal")));
        assertThat(val.getName(), is(equalTo("name")));
    }
}
