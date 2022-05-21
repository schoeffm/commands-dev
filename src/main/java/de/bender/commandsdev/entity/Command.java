package de.bender.commandsdev.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Set;

@RegisterForReflection
public class Command {

    private String name;
    private String command;
    private String description;
    private String objectId;
    private Set<String> tags;

    public Command() {}

    public Command(String name, String command) {
        this.name = name;
        this.command = command;
    }
    public Command(String name, String command, String description, String objectId, Set<String> tags) {
        this.name = name;
        this.description = description;
        this.command = command;
        this.objectId = objectId;
        this.tags = Set.copyOf(tags);
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getTags() {
        return Set.copyOf(tags);
    }

    public void setTags(Set<String> tags) {
        this.tags = Set.copyOf(tags);
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Command{" +
                "name='" + name + '\'' +
                ", command='" + command + '\'' +
                ", description='" + description + '\'' +
                ", tags=" + tags +
                '}';
    }
}
