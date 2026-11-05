package testflow.TestFlow.infrastructure.entity.folder;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@Entity
@NoArgsConstructor
public class FolderEntity {
    @Id
    private UUID id;
    @NotBlank
    @Column(nullable = false, length = 60)
    private String name;
    @NotNull
    private UUID projectId;
    @Nullable
    private UUID parentFolderId;
    @Column(nullable = false, updatable = false)
    private Instant createdOn;

    @PrePersist
    public void prePersist(){
        if (id == null){
            id = UUID.randomUUID();
        }
        if (createdOn == null){
            createdOn = Instant.now();
        }
    }
}
