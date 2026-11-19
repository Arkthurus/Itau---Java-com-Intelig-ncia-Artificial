package testflow.TestFlow.infrastructure.event.testCase;

import testflow.TestFlow.domain.testCase.TestCaseId;

public record TestCaseDeletedEvent(TestCaseId testCaseId, long durationMs) {
}
