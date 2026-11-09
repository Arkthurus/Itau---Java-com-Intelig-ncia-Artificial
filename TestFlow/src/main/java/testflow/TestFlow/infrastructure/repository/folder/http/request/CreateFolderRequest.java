package testflow.TestFlow.infrastructure.repository.folder.http.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateFolderRequest(
        @NotBlank
        @Size(min = 3, max = 100)
        String name
) {
    public CreateFolderRequest toInput(){return new CreateFolderRequest(name);}
}
