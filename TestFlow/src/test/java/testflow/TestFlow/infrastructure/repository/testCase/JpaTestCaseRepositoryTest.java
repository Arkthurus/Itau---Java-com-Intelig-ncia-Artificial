package testflow.TestFlow.infrastructure.repository.testCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testflow.TestFlow.domain.testCase.InvalidTestObjectUpdateException;
import testflow.TestFlow.domain.testCase.TestCase;
import testflow.TestFlow.domain.testCase.TestObjectField;
import testflow.TestFlow.domain.testCase.UpdateTestObjCommand;
import testflow.TestFlow.domain.testCase.testStep.TestStep;
import testflow.TestFlow.infrastructure.entity.testCase.TestCaseEntity;
import testflow.TestFlow.infrastructure.entity.testCase.TestStepEntity;
import testflow.TestFlow.infrastructure.repository.testCase.testStep.TestStepEntityRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class JpaTestCaseRepositoryTest {
    private TestCaseEntityRepository testCaseEntityRepository;
    private TestStepEntityRepository testStepEntityRepository;
    private JpaTestCaseRepository repository;

    @BeforeEach
    void setUp() {
        testCaseEntityRepository = mock(TestCaseEntityRepository.class);
        testStepEntityRepository = mock(TestStepEntityRepository.class);
        repository = new JpaTestCaseRepository(testCaseEntityRepository, testStepEntityRepository);
    }

    @Test
    void updatesAndPersistsCompleteTestCaseEntity() {
        UUID id = UUID.randomUUID();
        var entity = new TestCaseEntity();
        entity.setId(id);
        entity.setName("Original");
        entity.setObjective("Objetivo");
        when(testCaseEntityRepository.findById(id)).thenReturn(Optional.of(entity));

        assertTrue(repository.update(new UpdateTestObjCommand(
                id, TestCase.class, TestObjectField.NAME, "Atualizado"
        )));

        assertEquals("Atualizado", entity.getName());
        assertEquals("Objetivo", entity.getObjective());
        verify(testCaseEntityRepository).save(entity);
        verifyNoInteractions(testStepEntityRepository);
    }

    @Test
    void updatesAndPersistsCompleteTestStepEntity() {
        UUID id = UUID.randomUUID();
        var entity = new TestStepEntity();
        entity.setId(id);
        entity.setAction("Ação anterior");
        entity.setExpectedResult("Resultado esperado");
        when(testStepEntityRepository.findById(id)).thenReturn(Optional.of(entity));

        assertTrue(repository.update(new UpdateTestObjCommand(
                id, TestStep.class, TestObjectField.ACTION, "Ação atualizada"
        )));

        assertEquals("Ação atualizada", entity.getAction());
        assertEquals("Resultado esperado", entity.getExpectedResult());
        verify(testStepEntityRepository).save(entity);
        verifyNoInteractions(testCaseEntityRepository);
    }

    @Test
    void rejectsFieldThatDoesNotBelongToObjectType() {
        UUID id = UUID.randomUUID();
        when(testCaseEntityRepository.findById(id)).thenReturn(Optional.of(new TestCaseEntity()));

        assertThrows(InvalidTestObjectUpdateException.class, () -> repository.update(
                new UpdateTestObjCommand(id, TestCase.class, TestObjectField.ACTION, "Ação")
        ));
    }

    @Test
    void validatesTestStepFieldLengths() {
        UUID id = UUID.randomUUID();
        when(testStepEntityRepository.findById(id)).thenReturn(Optional.of(new TestStepEntity()));

        assertThrows(InvalidTestObjectUpdateException.class, () -> repository.update(
                new UpdateTestObjCommand(id, TestStep.class, TestObjectField.ACTION, "Curto")
        ));
        assertThrows(InvalidTestObjectUpdateException.class, () -> repository.update(
                new UpdateTestObjCommand(
                        id,
                        TestStep.class,
                        TestObjectField.EXPECTED_RESULT,
                        "x".repeat(501)
                )
        ));
    }

    @Test
    void returnsFalseWhenObjectDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(testStepEntityRepository.findById(id)).thenReturn(Optional.empty());

        assertFalse(repository.update(new UpdateTestObjCommand(
                id, TestStep.class, TestObjectField.ACTION, "Ação"
        )));
    }
}
