package testflow.TestFlow.domain.testCase;

import org.springframework.util.Assert;
import testflow.TestFlow.domain.folder.FolderId;

import java.util.UUID;

public record TestCaseId(UUID id) {
    public TestCaseId {
        Assert.notNull(id, "id must not be null");
    }

    public TestCaseId() {this(UUID.randomUUID());}
}
