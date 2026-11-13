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
        //Verifica se ja existe um projeto com o mesmo ID
        if (projectEntityRepository.existsById(project.getId().id())) {
            throw new ProjectAlreadyExistException(project.getId(), project.getName());
        }

        //Converte o projeto de dominio para entidade e salva no banco
        var entity = mapperToEntity(project);
        projectEntityRepository.save(entity);
        return project;
    }

    @Override
    public List<Project> findAll() {
        //Busca todas as entidades e converte cada uma para dominio
        var iterable = projectEntityRepository.findAll();
    return StreamSupport.stream(iterable.spliterator(), false)
                .map(JpaProjectRepository::mapperToDomain).toList();
    }

    @Override
    public Optional<Project> findById(ProjectId id) {
        //Busca a entidade pelo ID e converte o resultado para dominio
        return projectEntityRepository.findById(id.id())
                .map(JpaProjectRepository::mapperToDomain);
    }

    @Override
    public void delete(ProjectId id) {
        //Remove o projeto do banco pelo ID
        projectEntityRepository.deleteById(id.id());
    }

    @Override
    public boolean updateName(ProjectId id, String name) {
        //Atualiza o nome e informa se algum registro foi alterado
        return projectEntityRepository.updateNameById(id.id(), name) > 0;
    }

    private static ProjectEntity mapperToEntity(Project project) {

        var entity = new ProjectEntity();

        //Copia o ID e o nome do projeto para a entidade persistida
        entity.setId(project.getId().id());
        entity.setName(project.getName());

        return entity;
    }

    private static Project mapperToDomain(
            ProjectEntity entity) {

        //Converte a entidade persistida para o modelo de dominio
        return new Project(new ProjectId(entity.getId()), entity.getName());
    }
}
