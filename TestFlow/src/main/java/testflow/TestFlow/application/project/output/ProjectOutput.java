package testflow.TestFlow.application.project.output;

import testflow.TestFlow.domain.project.Project;

import java.io.Serializable;

public record ProjectOutput(String id, String name) implements Serializable {

    public static ProjectOutput from(Project project) {
        return new ProjectOutput(project.getId().id().toString(), project.getName());
    }
}
