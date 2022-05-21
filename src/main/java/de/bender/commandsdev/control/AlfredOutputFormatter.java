package de.bender.commandsdev.control;

import de.bender.commandsdev.entity.Command;

import javax.enterprise.context.ApplicationScoped;

import java.beans.XMLEncoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.XMLFormatter;

import static de.bender.commandsdev.control.OutputFormatter.OutputFormat.alfred;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * See <a href="https://www.alfredapp.com/help/workflows/inputs/script-filter/xml/">here at alfred.com</a> for more details about the output
 */
@ApplicationScoped
public class AlfredOutputFormatter implements OutputFormatter {

    private static final int MAX_RESULTS = 20;

    @Override
    public boolean canHandle(OutputFormatter.OutputFormat format) {
        return alfred.equals(format);
    }

    @Override
    public String format(List<Command> commands) {
        StringBuilder out = new StringBuilder();
        out.append("<?xml version=\"1.0\"?>");
        out.append("<items>");
        if (commands.isEmpty()) {
            out.append("<item valid=\"no\">");
            out.append("<title>").append("No Results Found</title>");
            // out.append("<icon>cmd.png</icon>");
            out.append("</item>");
        } else {
            var max_iteration = Math.min(MAX_RESULTS, commands.size());
            for (int i = 0; i < max_iteration; i++) {
                String cmd = commands.get(i).getCommand().replaceAll("(\\\\|\n|\\s{2,})", "");
                String cmdName = commands.get(i).getName();
                String cmdDesc = commands.get(i).getDescription()
                        .replaceAll("\"", "&quot;")
                        .replaceAll("'", "&apos;")
                        .replaceAll("<", "&lt;")
                        .replaceAll(">", "&gt;")
                        .replaceAll("&", "&amp;");
                String cmdUrl = String.format("https://www.commands.dev/workflows/%s", commands.get(i).getObjectId());

                out.append("<item valid=\"yes\" arg=\"").append(cmdName).append("\">")
                        .append("<title>").append(cmd).append("</title>")
                        .append("<subtitle>").append(cmdDesc).append("</subtitle>")
                        .append("<arg>").append(cmd).append("</arg>")
                        .append("<quicklookurl>").append(cmdUrl).append("</quicklookurl>")
                        .append("<mod key=\"cmd\" subtitle=\"").append(cmdUrl)
                            .append("\" valid=\"yes\" arg=\"").append(cmdUrl)
                            .append("\"/>")
                        .append("</item>");
            }
        }
        out.append("</items>");
        return out.toString();
    }
}
