package testflow.TestFlow.application.project.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProjectInput(
        @NotBlank
        @Size(min = 3, max = 100)
        String name) {
}
