package testflow.TestFlow.infrastructure.repository.http.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import testflow.TestFlow.application.output.ProjectOutput;

@JsonInclude(JsonInclude.Include.NON_ABSENT)
public record ProjectResponse(String id, String projectName) {
    public static ProjectResponse toOutput(ProjectOutput projectOutput){
        return new ProjectResponse(projectOutput.id(), projectOutput.name());
    }
}
