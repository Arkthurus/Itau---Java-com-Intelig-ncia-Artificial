package testflow.TestFlow.application.project.output;

import testflow.TestFlow.domain.project.Project;

public record ProjectOutput(String id, String name) {

    public static ProjectOutput from(Project project) {
        return new ProjectOutput(project.getId().id().toString(), project.getName());
    }
}
