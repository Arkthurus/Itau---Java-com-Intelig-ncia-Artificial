package testflow.TestFlow.domain.project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    Project save(Project project);
    List<Project> findAll();
    Optional<Project> findById(ProjectId id);
    void delete(ProjectId id);
    boolean updateName(ProjectId id, String name);
}
