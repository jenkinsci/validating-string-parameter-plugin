# Version History

## Version 2.4 (20-Apr-2018)

- Fixes Pipeline compatibility

## Version 2.3 (04-Feb-2015)

-   Fixed links to regex pattern documentation
-   Use codemirror for the description field

## Version 2.2 (13-Sep-2011)

-   [JENKINS-10139](http://issues.jenkins-ci.org/browse/JENKINS-10139)
    Fix for regex containing back slashes

## Version 2.1 (27-Feb-2011)

-   If an invalid value is passed to the build, the build will be
    aborted and an error message will display in console indicating the
    parameter and failing value

## Version 2.0 (19-Feb-2011)

-   Migrated code to jenkins and performed release

## Version 1.4 (13-Oct-2010)

-   [JENKINS-7794](http://issues.jenkins-ci.org/browse/JENKINS-6158)
    validation check doesn't work if Jenkins is hosted with a context
    root

## Version 1.3 (22-May-2010)

-   [JENKINS-6158](http://issues.jenkins-ci.org/browse/JENKINS-6158)
    Build will fail if invalid parameter is passed
-   [JENKINS-6160](http://issues.jenkins-ci.org/browse/JENKINS-6160)
    Jenkins will record regex used to validate the build parameter as
    part of build configuration and display as tooltip on the parameter
    page.

## Version 1.2 (29-Aug-2009)

-   Used the javascript function encodeURIComponent to properly handle
    characters within the user entered values for regex and validation
    error message. (Issue
    [4334](http://wiki.jenkins-ci.org/pages/editpage.action?pageId=38928603))

## Version 1.1 (28-Aug-2009)

-   Supported the release plugins as well as any other plugin that uses
    build parameters

## Version 1.0 (24-Aug-2009)

-   Initial Version
