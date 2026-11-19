package testflow.TestFlow.infrastructure.repository.testCase;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;
import testflow.TestFlow.infrastructure.entity.testCase.TestCaseEntity;

import java.util.List;
import java.util.UUID;

@RepositoryRestResource(exported = false)
public interface TestCaseEntityRepository extends CrudRepository<TestCaseEntity, UUID> {
    //Busca TestCases da pasta informada
    List<TestCaseEntity> findByFolderId(UUID folderId);

    //Mover TestCase para outro folder
    @Modifying
    @Transactional
    //Atualiza a pasta pai; não pode existir testCases na root
    @Query("update TestCaseEntity tc set tc.folderId = :folderId where tc.id = :id")
    int updateTestCaseFolderById(@Param("id") UUID id, @Param("folderId") UUID folderId);
}
