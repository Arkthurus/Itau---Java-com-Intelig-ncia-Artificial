package testflow.TestFlow.infrastructure.repository.folder;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import testflow.TestFlow.infrastructure.entity.folder.FolderEntity;
import testflow.TestFlow.infrastructure.entity.project.ProjectEntity;

import java.util.List;
import java.util.UUID;

@RepositoryRestResource(exported = false)
//ainda falta definir oq vai dentro do <>
public interface FolderEntityRepository extends PagingAndSortingRepository<FolderEntity, UUID>, CrudRepository<FolderEntity, UUID> {
    List<FolderEntity> findByNameContainingIgnoreCase (@Param("name") String name);

    @Modifying
    @Transactional
    @Query("update FolderEntity f set f.name = :name where f.id = :id")
    int updateNameById(@Param("id") UUID id, @Param("name") String name);
}
