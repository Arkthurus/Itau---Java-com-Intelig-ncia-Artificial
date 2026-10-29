package testflow.TestFlow.infrastructure.repository.project;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import testflow.TestFlow.infrastructure.entity.project.Project;

import java.util.List;
import java.util.UUID;

@RepositoryRestResource
public interface ProjectEntityRepository extends PagingAndSortingRepository<Project, UUID>, CrudRepository<Project, UUID> {
        List<Project> findByNameContainingIgnoreCase (@Param("name") String name);
}
