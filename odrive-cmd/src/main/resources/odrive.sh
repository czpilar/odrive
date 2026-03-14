#!/bin/bash

ODRIVE_DIR=.
ODRIVE_ARTIFACT=${project.artifactId}-${project.version}

java -jar $ODRIVE_DIR/lib/$ODRIVE_ARTIFACT-spring-boot.jar "$@"
