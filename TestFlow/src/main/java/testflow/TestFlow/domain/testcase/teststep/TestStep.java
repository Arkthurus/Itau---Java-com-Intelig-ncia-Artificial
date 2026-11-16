package testflow.TestFlow.domain.testCase.testStep;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;
import testflow.TestFlow.domain.testCase.TestCase;
import testflow.TestFlow.domain.testCase.TestCaseId;

@Getter
@Setter
public  class TestStep {
    private TestStepId id;
    @NotNull
    private TestCaseId testCaseId;
    private int stepOrder;
    @NotBlank
    private String action;
    @NotBlank
    private String expectedResult;

    //Construtor de criação
    public TestStep(@NotNull TestCaseId testCaseId,
                    @NotNull Integer stepOrder,
                    @NotBlank String action,
                    @NotBlank String expectedResult
    ) {
        Assert.notNull(testCaseId, "TestCaseId não pode ser nulo.");
        Assert.notNull(stepOrder, "StepOrder não pode ser nulo.");
        Assert.isTrue(stepOrder >= 1, "StepOrder deve ser maior ou igual a 1.");

        Assert.hasText(action, "Action não pode estar vazio.");
        Assert.hasText(expectedResult, "ExpectedResult não pode estar vazio.");

        this.id = new  TestStepId();
        this.testCaseId = testCaseId;
        this.stepOrder = stepOrder;
        this.action = action;
        this.expectedResult = expectedResult;
    }

    //Construtor de consulta ao banco
    public TestStep(@NotNull TestStepId id,
                    @NotNull TestCaseId testCaseId,
                    @NotNull Integer stepOrder,
                    @NotBlank String action,
                    @NotBlank String expectedResult
    ) {
        Assert.notNull(id, "TestStepId não pode ser nulo.");
        Assert.notNull(testCaseId, "TestCaseId não pode ser nulo.");
        Assert.notNull(stepOrder, "StepOrder não pode ser nulo.");
        Assert.isTrue(stepOrder >= 1, "StepOrder deve ser maior ou igual a 1.");
        Assert.hasText(action, "Action não pode estar vazio.");
        Assert.hasText(expectedResult, "ExpectedResult não pode estar vazio.");

        this.id = id;
        this.testCaseId = testCaseId;
        this.stepOrder = stepOrder;
        this.action = action;
        this.expectedResult = expectedResult;
    }
}
