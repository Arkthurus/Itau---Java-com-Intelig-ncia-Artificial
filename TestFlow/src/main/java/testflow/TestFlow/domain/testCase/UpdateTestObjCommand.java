package testflow.TestFlow.domain.testCase;

import java.util.UUID;

public record UpdateTestObjCommand(UUID id,
                                   Class<?> objType,
                                   TestObjectField field,
                                   String newValue
) {
}
