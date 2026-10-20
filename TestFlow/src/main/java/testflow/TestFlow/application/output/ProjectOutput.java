package testflow.TestFlow.application.output;

import testflow.TestFlow.domain.Project;

public record ProjectOutput(String id, String name) {

    public static ProjectOutput from(Project project) {
        return new ProjectOutput(project.getId().id().toString(), project.getName());
    }
}
