package testflow.TestFlow.infrastructure.repository.project.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import testflow.TestFlow.application.project.input.UpdateProjectInput;

public record UpdateProjectRequest(
        @NotBlank @Size(min = 3, max = 100) String newName
){
    public UpdateProjectInput toInput(){
        return new UpdateProjectInput(newName);
    }
}
