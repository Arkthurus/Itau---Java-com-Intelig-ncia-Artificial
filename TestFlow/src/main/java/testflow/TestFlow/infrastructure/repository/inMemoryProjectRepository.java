package testflow.TestFlow.infrastructure.repository;

import org.springframework.stereotype.Repository;
import testflow.TestFlow.domain.Project;
import testflow.TestFlow.domain.ProjectId;
import testflow.TestFlow.domain.ProjectRepository;

import java.util.*;
@Repository
public class inMemoryProjectRepository implements ProjectRepository {

    private final Map<ProjectId, Project> projects = new HashMap<>();

    @Override
    public Project create(Project project) {
        projects.put(project.getId(), project);
        return project;
    }

    @Override
    public List<Project> findAll() {
        return new ArrayList<Project>(projects.values());
    }

    @Override
    public Optional<Project> findById(ProjectId id) {
            Optional<Project> project =  Optional.ofNullable(projects.get(id));
        return Optional.of(project.orElseThrow(() -> new RuntimeException("Projeto não encontrado")));
    }

    @Override
    public void delete(ProjectId id) {
        projects.remove(id);

    }
}
