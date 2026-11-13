package testflow.TestFlow.domain.testcase;

import java.util.UUID;

public record TestCaseId(UUID id) {
    public TestCaseId {
        if (id == null) {
            throw new IllegalArgumentException("TestCase id não pode ser nulo");
        }
    }

    public TestCaseId() {
        this(UUID.randomUUID());
    }
}
