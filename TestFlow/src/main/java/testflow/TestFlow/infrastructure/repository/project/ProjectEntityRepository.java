package testflow.TestFlow.infrastructure.repository.project;

import org.springframework.data.repository.CrudRepository;
import testflow.TestFlow.infrastructure.entity.project.Project;

import java.util.UUID;

public interface ProjectEntityRepository extends CrudRepository<Project, UUID> {

    testflow.TestFlow.domain.project.Project getById(UUID id);
}
