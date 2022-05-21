package de.bender.commandsdev.control;

import de.bender.commandsdev.entity.Command;

import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.List;

@ApplicationScoped
public class JsonOutputFormatter implements OutputFormatter {

    private final Jsonb jsonb;

    public JsonOutputFormatter() {
        this.jsonb = JsonbBuilder.create();
    }

    @Override
    public boolean canHandle(OutputFormat format) {
        return OutputFormat.json.equals(format);
    }

    @Override
    public String format(List<Command> commands) {
        return jsonb.toJson(commands);
    }
}
