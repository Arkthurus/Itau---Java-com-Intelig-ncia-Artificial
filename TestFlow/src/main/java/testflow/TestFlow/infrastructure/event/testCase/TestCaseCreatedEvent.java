package testflow.TestFlow.infrastructure.event.testCase;

import testflow.TestFlow.domain.testCase.TestCaseId;

public record TestCaseCreatedEvent(TestCaseId testCaseId, long durationMs) {
}
