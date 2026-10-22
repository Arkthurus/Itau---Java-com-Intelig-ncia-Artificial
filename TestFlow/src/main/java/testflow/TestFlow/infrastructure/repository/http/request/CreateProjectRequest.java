package testflow.TestFlow.infrastructure.repository.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import testflow.TestFlow.application.input.CreateProjectInput;

public record CreateProjectRequest(
        @NotBlank
        @Size(min = 3, max = 100)
        String projectName
) {
    public CreateProjectInput toInput(){
        return new CreateProjectInput(projectName);
    }
}
