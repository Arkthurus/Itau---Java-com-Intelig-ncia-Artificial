package testflow.TestFlow.infrastructure.repository.folder;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@RepositoryRestResource(exported = false)
//ainda falta definir oq vai dentro do <>
public interface FolderEntityRepository extends PagingAndSortingRepository<>, CrudRepository<> {
}
