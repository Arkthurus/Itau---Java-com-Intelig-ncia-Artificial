package testflow.TestFlow.infrastructure.repository.folder.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import testflow.TestFlow.application.folder.input.UpdateFolderInput;

public record UpdateFolderRequest(
        @NotBlank
        @Size(min = 3, max = 60)
        String newName
) {
    public UpdateFolderInput toInput(){return new UpdateFolderInput(newName);}
}
