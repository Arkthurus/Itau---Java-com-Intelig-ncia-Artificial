package testflow.TestFlow.infrastructure.repository.project;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;
import testflow.TestFlow.infrastructure.entity.project.Project;

import java.util.List;
import java.util.UUID;

@RepositoryRestResource(exported = false)
public interface ProjectEntityRepository extends PagingAndSortingRepository<Project, UUID>, CrudRepository<Project, UUID> {
        List<Project> findByNameContainingIgnoreCase (@Param("name") String name);

        @Modifying
        @Transactional
        @Query("update Project p set p.name = :name where p.id = :id")
        int updateNameById(@Param("id") UUID id, @Param("name") String name);
}
