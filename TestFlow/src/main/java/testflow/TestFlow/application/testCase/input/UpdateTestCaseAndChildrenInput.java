package testflow.TestFlow.application.testCase.input;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record UpdateTestCaseAndChildrenInput(
        @NotBlank
        @Size(min = 3, max = 100)
        String name,
        @Size(min = 3, max = 100)
        String objective,
        @Size(min = 3, max = 100)
        String preconditions,
        @NotNull
        @Valid
        List<TestStepInput> steps
) {
    public record TestStepInput(
            @NotNull
            UUID id,
            @NotNull
            @Positive
            Integer stepOrder,
            @NotBlank
            @Size(min = 10, max = 500)
            String action,
            @NotBlank
            @Size(min = 10, max = 500)
            String expectedResult
    ) {}
}
