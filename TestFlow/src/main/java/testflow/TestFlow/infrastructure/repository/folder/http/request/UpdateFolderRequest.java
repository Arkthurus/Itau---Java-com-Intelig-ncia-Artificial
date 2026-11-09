package testflow.TestFlow.infrastructure.repository.folder.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateFolderRequest(
        @NotBlank
        @Size(min = 3, max = 100)
        String newName
) {
    public UpdateFolderRequest toInput(){return new UpdateFolderRequest(newName);}
}
