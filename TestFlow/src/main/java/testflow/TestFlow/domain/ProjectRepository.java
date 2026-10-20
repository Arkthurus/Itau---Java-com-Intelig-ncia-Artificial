package testflow.TestFlow.domain;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    Project create(Project project);
    List<Project> findAll();
    Optional<Project> findById(ProjectId id);
    void delete(ProjectId id);
}
