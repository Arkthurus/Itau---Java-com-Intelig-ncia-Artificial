package testflow.TestFlow.infrastructure.entity.testCase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "testcase")
@Getter
@Setter
@NoArgsConstructor
public class TestCaseEntity {
    @Id
    private UUID id;
    private String name;
    @Column(name = "projectid")
    private UUID projectId;
    @Column(name = "folderid")
    private UUID folderId;
    private String objective;
    private String preconditions;
    @Column(name = "created_on", nullable = false, updatable = false)
    private Instant createdOn;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdOn == null) {
            createdOn = Instant.now();
        }
    }
}
