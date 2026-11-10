package testflow.TestFlow.infrastructure.entity.project;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "project")
@Data
@RequiredArgsConstructor
public class ProjectEntity {
    @Id
    private UUID id;
    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;
    @NotNull
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
