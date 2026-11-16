package testflow.TestFlow.domain.testcase.teststep;

public final class TestStep {
    private final TestStepId id;
    private final int stepOrder;
    private final String action;
    private final String expectedResult;

    public TestStep(String action, String expectedResult) {
        this(new TestStepId(), 1, action, expectedResult);
    }

    public TestStep(TestStepId id, int stepOrder, String action, String expectedResult) {
        if (id == null) {
            throw new IllegalArgumentException("TestStep id não pode ser nulo");
        }
        if (stepOrder < 1) {
            throw new IllegalArgumentException("A ordem do TestStep deve começar em 1");
        }

        this.id = id;
        this.stepOrder = stepOrder;
        this.action = requireText(action, "A ação do TestStep não pode ser vazia");
        this.expectedResult = requireText(
                expectedResult,
                "O resultado esperado do TestStep não pode ser vazio"
        );
    }

    public TestStepId getId() {
        return id;
    }

    public int getStepOrder() {
        return stepOrder;
    }

    public String getAction() {
        return action;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public TestStep withOrder(int newOrder) {
        return new TestStep(id, newOrder, action, expectedResult);
    }

    private static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }
}
