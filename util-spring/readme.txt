util-spring

Simple utilities for working with Spring.  Primarily used for debugging and
diagnostic feedback.

To build this code and create an OSGI bundle:

> mvn clean install

Deploy to Nexus repository.

> mvn deploy


This module does not have its own OSGI feature.  It will be deployed to esb
nodes by the feature files of other modules via transitive dependencies.