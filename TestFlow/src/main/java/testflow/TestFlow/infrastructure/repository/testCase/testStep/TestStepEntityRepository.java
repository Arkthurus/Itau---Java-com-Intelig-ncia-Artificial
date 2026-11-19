package testflow.TestFlow.infrastructure.repository.testCase.testStep;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import testflow.TestFlow.infrastructure.entity.testCase.TestStepEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RepositoryRestResource(exported = false)
public interface TestStepEntityRepository extends CrudRepository<TestStepEntity, UUID> {
    List<TestStepEntity> findByTestCaseId(UUID testCaseId);
    Optional<TestStepEntity> findById(UUID testStepId);
}
