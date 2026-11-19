package testflow.TestFlow.infrastructure.event.testCase;

import testflow.TestFlow.domain.testCase.TestCaseId;

public record TestCaseViewedEvent(TestCaseId testCaseId, long durationMs) {
}
