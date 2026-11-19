package testflow.TestFlow.infrastructure.event.testCase;

import testflow.TestFlow.domain.testCase.TestObjectField;

import java.util.UUID;

public record TestObjectUpdatedEvent(
        String objectName,
        UUID id,
        TestObjectField updatedField,
        Class<?> objType,
        long durationMs,
        String oldFieldValue,
        String newFieldValue
) {
}
