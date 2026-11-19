package testflow.TestFlow.http.testcase.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import testflow.TestFlow.application.testCase.input.UpdateTestCaseAndChildrenInput;

import java.util.List;
import java.util.UUID;

public record UpdateTestCaseAndChildrenRequest(
        @NotBlank
        @Size(min = 3, max = 100)
        String name,

        @Size(min = 3, max = 100)
        String objective,

        @Size(min = 3, max = 100)
        String preconditions,

        @NotNull
        @Valid
        List<TestStepUpdateRequest> steps
) {
    public record TestStepUpdateRequest(
            @NotNull UUID id,

            @NotNull
            @Positive
            Integer stepOrder,

            @NotBlank
            @Size(min = 10, max = 500)
            String action,

            @NotBlank
            @Size(min = 10, max = 500)
            String expectedResult
    ) {
    }

    public UpdateTestCaseAndChildrenInput toInput() {
        return new UpdateTestCaseAndChildrenInput(
                name,
                objective,
                preconditions,
                steps.stream()
                        .map(step -> new UpdateTestCaseAndChildrenInput.TestStepInput(
                                step.id(),
                                step.stepOrder(),
                                step.action(),
                                step.expectedResult()
                        ))
                        .toList()
        );
    }
}