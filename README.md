Introduction
============
This is Confluence Plugin that List Maven Artifact(s) information in Confluence page via Macro.
There also have a configuration page used to configure Nexus credential, Nexus URL and default Group ID used to search Maven Artifact(s).

Confluence
==========
Tested on Confluence 4.0 and 3.4.3

Nexus
=====
* Tested on Nexus 1.9.2.2
* Tested on Nexus 2.x
* Tested on Nexus 3.x

Compilation
===========
To compile you need apache maven 2 (tested with 2.2.1) using the following switches:

`-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true`

since the atlassian repository is not well configured (as of 20210324).

Release Notes
=============

## confluence-nexus-plugin-1.5.3

- [view commit](https://github.com/polopoly/confluence-nexus-plugin/commit/ad49c4575725039ea5381eafd31b1b972cef4fbe) Fixed https://github.com/advisories/GHSA-269g-pwp5-87pp
- [view commit](https://github.com/polopoly/confluence-nexus-plugin/commit/2dd3795fdbe45854105b6405954d9c6a3f38942e) Added releases notes and deploy procedure.
- [view commit](https://github.com/polopoly/confluence-nexus-plugin/commit/0438a61a63f0f570de8b810350eca7a64eab9994) Fixed pom for releasing the plugin
- [view commit](https://github.com/polopoly/confluence-nexus-plugin/commit/460aafd53eaa1ea1423371ec3084764039a0c123) Now work across nexus 1.x, nexus 2.x and nexus 3.x

## confluence-nexus-plugin-1.5.2

- [view commit](https://github.com/polopoly/confluence-nexus-plugin/commit/e374b7f1c6d8eba8bc3a94a6eb4b19851f775996) Made plugin search more specificly for the artifacts, removed unused code, added some more info to the most common error message, added ignore of DS_Store and made the plugin ignore inherited polopoly.version property when printing supported version.

## confluence-nexus-plugin-1.5.1

- [view commit](https://github.com/polopoly/confluence-nexus-plugin/commit/bf2a78af436f4b732d6025fa6d94af4c6325998c) MDCPD-188 Add validation and tune unit test case
- [view commit](https://github.com/polopoly/confluence-nexus-plugin/commit/0ab0d93efc84179addd4654621fa96240381d411) MDCPD-188 Make the maven repo link customizable from configuration page
- [view commit](https://github.com/polopoly/confluence-nexus-plugin/commit/c60dc5c60eacef37b84b7f2e7cc941497691fd21) Merge branch 'master' of github.com:polopoly/confluence-nexus-plugin
- [view commit](https://github.com/polopoly/confluence-nexus-plugin/commit/8e225abbde2ef88c580cb05d74ddabf7b0b2770a) MDCPD-168 MDCPD-170 Change text label

Deploy guide
============

You need to use java 1.8

### (Re)Deploy an existing release

```
mvn clean install deploy -Pdeploy
```

### Prepare for a release

First you need to generate the release notes, use the last tag generated:

```
./getReleaseNotes.sh  confluence-nexus-plugin-xxxx | pbcopy
```

Edit the file `README.md` by adding the new release on top and pasting the output of the script above and then push to git.

```
git commit -a -m "update release notes"
git push
```

```
mvn clean install deploy release:prepare -Pdeploy -DautoVersionSubmodules=true
mvn clean install deploy release:perform -Pdeploy
```

For these two tasks you can use the scripts:

`./releasePrepare.sh <maven settings.xml>`
`./releasePerform.sh <maven settings.xml>`

### Cleanup a broken release

Sometimes when you perform a release it may fail with an error `Unable to tag SCM`.

You need to clean the release

```
mvn release:clean
```

and remove the tag from the local and remote git:

```
git tag -d confluence-nexus-plugin-xxxx
git push --delete origin confluence-nexus-plugin-xxxx
```

finally you can cleanup the checked out files (this will revert all the modified files!!!):

```
git reset HEAD --hard
```

Then you can try to release it again.
