package de.bender.commandsdev.control;

import de.bender.commandsdev.entity.Command;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

import static de.bender.commandsdev.control.OutputFormatter.OutputFormat.raw;

@ApplicationScoped
public class StandardOutputFormatter implements OutputFormatter {

    private static final int MAX_RESULTS = 20;

    @Override
    public boolean canHandle(OutputFormat format) {
        return raw.equals(format);
    }

    @Override
    public String format(List<Command> commands) {
        StringBuilder out = new StringBuilder();

        if (commands.isEmpty()) {
            out.append("Not Found");
        } else {
            var max_iteration = Math.min(MAX_RESULTS, commands.size());
            for (int i = 0; i < max_iteration; i++) {
                Command cmd = commands.get(i);
                out.append("# Desc: ").append(cmd.getDescription() ).append("\n")
                        .append("# Tags: ").append(String.join(", ", cmd.getTags())).append("\n")
                        .append(cmd.getCommand()).append("\n\n");
            }
        }

        return out.toString();
    }
}
