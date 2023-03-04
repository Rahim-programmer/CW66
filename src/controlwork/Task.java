package controlwork;

import server.Generator;

public class Task {
    private String taskId;
    private String name;
    private String description;
    private TypeOfTask type;

    public Task() {
        this(Generator.makePassword(), Generator.makeName(), Generator.makeDescription(), null);
    }

    public Task(String taskId, String name, String description, TypeOfTask type) {
        this.taskId = taskId;
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeOfTask getType() {
        return type;
    }

    public void setType(TypeOfTask type) {
        this.type = type;
    }
}
