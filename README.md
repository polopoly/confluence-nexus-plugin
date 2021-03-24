Introduction
============
This is Confluence Plugin that List Maven Artifact(s) information in Confluence page via Macro.
There also have a configuration page used to configure Nexus credential, Nexus URL and default Group ID used to search Maven Artifact(s).

Confluence
==========
Tested on Confluence 4.0 and 3.4.3

Nexus
=====
Tested on Nexus 1.9.2.2
Tested on Nexus 2.x
Tested on Nexus 3.x

Compilation
===========
To compile you need apache maven 2 (tested with 2.2.1) using the following switches:

`-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true`

since the atlassian repository is not well configured (as of 20210324).