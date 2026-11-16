package testflow.TestFlow.domain.testCase;

import org.springframework.util.Assert;

import java.util.UUID;

public record TestCaseId(UUID id) {
    public TestCaseId {
        Assert.notNull(id, "id não pode ser nulo.");
    }

    public TestCaseId() {
        this(UUID.randomUUID());
    }
}
