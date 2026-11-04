package testflow.TestFlow.infrastructure.repository.project;

import org.springframework.stereotype.Repository;
import testflow.TestFlow.domain.project.Project;
import testflow.TestFlow.domain.project.ProjectAlreadyExistException;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.domain.project.ProjectRepository;
import testflow.TestFlow.infrastructure.entity.project.ProjectEntity;

import java.util.*;
import java.util.stream.StreamSupport;

@Repository
public class JpaProjectRepository implements ProjectRepository {

    private final ProjectEntityRepository projectEntityRepository;

    public JpaProjectRepository(ProjectEntityRepository projectEntityRepository) {
        this.projectEntityRepository = projectEntityRepository;
    }

    private final Map<ProjectId, Project> projects = new HashMap<>();

    @Override
    public Project save(Project project) {
        if (projectEntityRepository.existsById(project.getId().id())) {
            throw new ProjectAlreadyExistException(project.getId(), project.getName());
        }

        var entity = mapperToEntity(project);

        projectEntityRepository.save(entity);
        return project;
    }

    @Override
    public List<Project> findAll() {
        var iterable = projectEntityRepository.findAll();
    return StreamSupport.stream(iterable.spliterator(), false)
                .map(JpaProjectRepository::mapperToDomain).toList();
    }

    @Override
    public Optional<Project> findById(ProjectId id) {
        return projectEntityRepository.findById(id.id())
                .map(JpaProjectRepository::mapperToDomain);
    }

    @Override
    public void delete(ProjectId id) {
        projectEntityRepository.deleteById(id.id());
    }

    @Override
    public boolean updateName(ProjectId id, String name) {
        return projectEntityRepository.updateNameById(id.id(), name) > 0;
    }

    private static ProjectEntity mapperToEntity(Project project) {

        var entity = new ProjectEntity();

        entity.setId(project.getId().id());
        entity.setName(project.getName());

        return entity;
    }

    private static Project mapperToDomain(
            ProjectEntity entity) {

        return new Project(new ProjectId(entity.getId()), entity.getName());
    }
}
