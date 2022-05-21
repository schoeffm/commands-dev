#!/bin/bash

binaryName=cmd

mvn clean package -Pnative
cp target/cmd-1.0.0-SNAPSHOT-runner target/$binaryName

pushd target/quarkus-app
  java -cp $(ls lib/main | awk '{print "lib/main/" $1}' | tr "\n" ":")../cmd-1.0.0-SNAPSHOT.jar picocli.AutoComplete de.bender.commandsdev.boundary.Commands
  mv cmd_completion ..
popd
