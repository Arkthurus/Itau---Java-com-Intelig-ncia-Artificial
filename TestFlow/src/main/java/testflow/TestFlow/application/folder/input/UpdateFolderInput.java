package testflow.TestFlow.application.folder.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateFolderInput(
        @NotBlank
        @Size(min = 3, max = 100)
        String name) {
}
