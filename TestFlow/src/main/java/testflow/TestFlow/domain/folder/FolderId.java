package testflow.TestFlow.domain.folder;

import org.springframework.util.Assert;

import java.util.UUID;

public record FolderId(UUID id) {
    public FolderId{
        Assert.notNull(id, "id must not be null");
    }

    public FolderId()
    {
        this(UUID.randomUUID());
    }
}
