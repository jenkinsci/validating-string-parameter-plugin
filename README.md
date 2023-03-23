# Validating String Parameter Plugin

[![Jenkins](https://ci.jenkins.io/job/Plugins/job/validating-string-parameter-plugin/job/master/badge/icon)](https://ci.jenkins.io/job/Plugins/job/validating-string-parameter-plugin/job/master/)
[![Jenkins Plugin](https://img.shields.io/jenkins/plugin/v/validating-string-parameter.svg)](https://plugins.jenkins.io/validating-string-parameter)
[![GitHub release](https://img.shields.io/github/release/jenkinsci/validating-string-parameter-plugin.svg?label=changelog)](https://github.com/jenkinsci/validating-string-parameter-plugin/releases/latest)

The validating string parameter plugin contributes a new parameter type
to Jenkins that supports regular expression validation of the user's
entered parameter.

# Usage

This plugin is used wherever build parameter selection is available,
most commonly in the job configuration page by enabling parameterized
builds (this parameter type will also be available as release parameters
using the [release
plugin](https://plugins.jenkins.io/release/)).

Configure the parameter by entering a name, regular expression to
validate the entered value and optionally a default value, an error
message shown when the user entered value fails the regular expression
check and a parameter description.  
![](docs/images/configure.PNG)

When a build is requested, the user will be prompted with the parameters
to enter. Users enter the parameter as normal, but will now be prompted
with an error message if the entered value does not meet the regular
expression.

![](docs/images/build-error.PNG) 

Once the entered value meets the configured regular expression, the
error message is no longer displayed.

![](docs/images/build-success.PNG)

## Pipeline Examples

```groovy
pipeline {
    agent any

    parameters {
        validatingString(
            name: 'param1', 
            defaultValue: '', 
            regex: /^[0-9]+$/, 
            failedValidationMessage: '', 
            description: 'Numbers only parameter example'
        )
    }

    stages {
        stage("Check") {
            steps {
                echo "${params.param1}"
            }
        }
    }
}
```
