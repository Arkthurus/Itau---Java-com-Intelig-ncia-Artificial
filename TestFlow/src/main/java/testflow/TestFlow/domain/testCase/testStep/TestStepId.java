package testflow.TestFlow.domain.testCase.testStep;

import org.springframework.util.Assert;

import java.util.UUID;

public record TestStepId(UUID id) {
    public TestStepId {
        Assert.notNull(id, "O TestStepId não pode ser nulo.");
    }

    public TestStepId() {
        this(UUID.randomUUID());
    }
}
