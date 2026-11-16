package testflow.TestFlow.domain.testcase.teststep;

import java.util.UUID;

public record TestStepId(UUID id) {
    public TestStepId {
        if (id == null) {
            throw new IllegalArgumentException("TestStep id não pode ser nulo");
        }
    }

    public TestStepId() {
        this(UUID.randomUUID());
    }
}
