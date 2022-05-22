package de.bender.commandsdev.boundary;

import picocli.AutoComplete;
import picocli.CommandLine.Command;

@Command(name = "completion",
        header = "bash/zsh completion:  source <(${PARENT-COMMAND-FULL-NAME:-$PARENTCOMMAND} ${COMMAND-NAME})",
        helpCommand = true)
public class Completion extends AutoComplete.GenerateCompletion {
}
