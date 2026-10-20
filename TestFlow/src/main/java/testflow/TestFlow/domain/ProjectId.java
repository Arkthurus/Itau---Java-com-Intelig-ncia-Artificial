package testflow.TestFlow.domain;

import org.springframework.util.Assert;

import java.util.UUID;

public record ProjectId(UUID id) {
    public ProjectId{
        Assert.notNull(id, "id must not be null");
    }

    public ProjectId(){
        this(UUID.randomUUID());
    }
}
