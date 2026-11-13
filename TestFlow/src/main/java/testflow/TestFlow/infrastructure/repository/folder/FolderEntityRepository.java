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
public interface FolderEntityRepository extends PagingAndSortingRepository<FolderEntity, UUID>, CrudRepository<FolderEntity, UUID> {
    //Busca pastas cujo nome contenha o texto informado, sem diferenciar maiusculas de minusculas
    List<FolderEntity> findByNameContainingIgnoreCase (@Param("name") String name);

    //Busca as pastas raiz de um projeto, que nao possuem pasta pai
    List<FolderEntity> findByProjectIdAndParentFolderIdIsNull(UUID projectId);

    //Busca as pastas filhas da pasta informada
    List<FolderEntity> findByParentFolderId(UUID parentFolderId);

    @Modifying
    @Transactional
    //Atualiza o nome da pasta com o ID informado
    @Query("update FolderEntity f set f.name = :name where f.id = :id")
    int updateNameById(@Param("id") UUID id, @Param("name") String name);

    @Modifying
    @Transactional
    //Atualiza a pasta pai; um ID nulo deixa a pasta como raiz
    @Query("update FolderEntity f set f.parentFolderId = :parentFolderId where f.id = :id")
    int updateParentFolderById(@Param("id") UUID id, @Param("parentFolderId") UUID parentFolderId);
}
