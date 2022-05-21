package de.bender.commandsdev.control;

import de.bender.commandsdev.entity.Command;

import java.util.List;

public interface OutputFormatter {

    boolean canHandle(OutputFormat format);

    String format(List<Command> translation);

    enum OutputFormat {
        json, raw, alfred
    }
}
