package testflow.TestFlow.infrastructure.repository.testCase;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.testCase.InvalidTestObjectUpdateException;
import testflow.TestFlow.domain.testCase.TestCase;
import testflow.TestFlow.domain.testCase.TestCaseId;
import testflow.TestFlow.domain.testCase.TestCaseRepository;
import testflow.TestFlow.domain.testCase.TestObjectField;
import testflow.TestFlow.domain.testCase.UpdateTestObjCommand;
import testflow.TestFlow.domain.testCase.testStep.TestStep;
import testflow.TestFlow.domain.testCase.testStep.TestStepId;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.infrastructure.entity.testCase.TestCaseEntity;
import testflow.TestFlow.infrastructure.entity.testCase.TestStepEntity;
import testflow.TestFlow.infrastructure.repository.testCase.testStep.TestStepEntityRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaTestCaseRepository implements TestCaseRepository {
    private final TestCaseEntityRepository testCaseEntityRepository;
    private final TestStepEntityRepository testStepEntityRepository;

    public JpaTestCaseRepository(
            TestCaseEntityRepository testCaseEntityRepository,
            TestStepEntityRepository testStepEntityRepository
    ) {
        this.testCaseEntityRepository = testCaseEntityRepository;
        this.testStepEntityRepository = testStepEntityRepository;
    }

    @Override
    public TestCase saveTestCase(TestCase testCase) {
        testCaseEntityRepository.save(toEntity(testCase));
        return testCase;
    }

    @Override
    public TestStep saveTestStep(TestStep step) {
        testStepEntityRepository.save(toEntity(step));
        return step;
    }

    @Override
    public List<TestCase> findAllTestCasesByFolderId(FolderId folderId) {
        return testCaseEntityRepository.findByFolderId(folderId.id()).stream()
                .map(JpaTestCaseRepository::toDomain)
                .toList();
    }

    @Override
    public List<TestStep> findAllTestStepsByTestCaseId(TestCaseId testCaseId) {
        return testStepEntityRepository.findByTestCaseId(testCaseId.id()).stream()
                .map(JpaTestCaseRepository::toDomain)
                .toList();
    }

    @Override
    public Optional<TestCase> findTestCaseById(TestCaseId testCaseId) {
        return testCaseEntityRepository.findById(testCaseId.id())
                .map(JpaTestCaseRepository::toDomain);
    }

    @Override
    public Optional<TestStep> findTestStepById(TestStepId testStepId) {
        return testStepEntityRepository.findById(testStepId.id())
                .map(JpaTestCaseRepository::toDomain);
    }

    @Override
    public void deleteTestCase(TestCaseId testCaseId) {
        testCaseEntityRepository.deleteById(testCaseId.id());
    }

    @Override
    public void deleteTestStep(TestStepId testStepId) {
        testStepEntityRepository.deleteById(testStepId.id());
    }

    @Override
    @Transactional
    public boolean update(UpdateTestObjCommand command) {
        if (command.objType() == TestCase.class) {
            return testCaseEntityRepository.findById(command.id())
                    .map(entity -> updateTestCase(entity, command))
                    .orElse(false);
        }
        if (command.objType() == TestStep.class) {
            return testStepEntityRepository.findById(command.id())
                    .map(entity -> updateTestStep(entity, command))
                    .orElse(false);
        }
        throw new InvalidTestObjectUpdateException(
                "Tipo de objeto de teste não permitido: " + command.objType()
        );
    }

    @Override
    public boolean moveTestCaseToOtherFolder(TestCaseId id, FolderId testCaseFolderId) {
        return false;
    }

    private boolean updateTestCase(TestCaseEntity entity, UpdateTestObjCommand command) {
        String value = validateValue(command.field(), command.newValue());
        switch (command.field()) {
            case NAME -> entity.setName(value);
            case OBJECTIVE -> entity.setObjective(value);
            case PRECONDITIONS -> entity.setPreconditions(value);
            default -> throw unsupportedField(command);
        }
        testCaseEntityRepository.save(entity);
        return true;
    }

    private boolean updateTestStep(TestStepEntity entity, UpdateTestObjCommand command) {
        String newValue = validateValue(command.field(), command.newValue());
        switch (command.field()) {
            case ACTION -> entity.setAction(newValue);
            case EXPECTED_RESULT -> entity.setExpectedResult(newValue);
            case STEP_ORDER -> entity.setStepOrder(Integer.parseInt(newValue));
            default -> throw unsupportedField(command);
        }
        testStepEntityRepository.save(entity);
        return true;
    }

    private static String validateValue(TestObjectField field, String value) {
        if (field == null) {
            throw new InvalidTestObjectUpdateException(
                    "O campo deve ser informado."
            );
        }
        if (field == TestObjectField.OBJECTIVE || field == TestObjectField.PRECONDITIONS) {
            if (value == null) {
                return null;
            }
        } else if (value == null || value.isBlank()) {
            throw new InvalidTestObjectUpdateException(
                    "O novo valor do campo " + field + " deve ser informado."
            );
        }
        if (field == TestObjectField.STEP_ORDER) {
            try {
                if (Integer.parseInt(value) < 1) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException exception) {
                throw new InvalidTestObjectUpdateException(
                        "A ordem do passo deve ser um número inteiro positivo."
                );
            }
            return value;
        }
        int minLength = switch (field) {
            case NAME, OBJECTIVE, PRECONDITIONS -> 3;
            case ACTION, EXPECTED_RESULT -> 10;
            case STEP_ORDER -> throw new IllegalStateException("STEP_ORDER já foi validado.");
        };
        int maxLength = switch (field) {
            case NAME, OBJECTIVE, PRECONDITIONS -> 100;
            case ACTION, EXPECTED_RESULT -> 500;
            case STEP_ORDER -> throw new IllegalStateException("STEP_ORDER já foi validado.");
        };
        if (value != null && (value.length() < minLength || value.length() > maxLength)) {
            throw new InvalidTestObjectUpdateException(
                    "O valor do campo " + field + " deve ter entre "
                            + minLength + " e " + maxLength + " caracteres."
            );
        }
        return value;
    }

    private static InvalidTestObjectUpdateException unsupportedField(UpdateTestObjCommand command) {
        return new InvalidTestObjectUpdateException(
                "O campo " + command.field() + " não pode ser atualizado em "
                        + command.objType().getSimpleName() + "."
        );
    }

    private static TestCaseEntity toEntity(TestCase testCase) {
        var entity = new TestCaseEntity();
        entity.setId(testCase.getId().id());
        entity.setName(testCase.getName());
        entity.setProjectId(testCase.getProjectId().id());
        entity.setFolderId(testCase.getFolderId().id());
        entity.setObjective(testCase.getObjective());
        entity.setPreconditions(testCase.getPreconditions());
        return entity;
    }

    private static TestStepEntity toEntity(TestStep step) {
        var entity = new TestStepEntity();
        entity.setId(step.getId().id());
        entity.setTestCaseId(step.getTestCaseId().id());
        entity.setStepOrder(step.getStepOrder());
        entity.setAction(step.getAction());
        entity.setExpectedResult(step.getExpectedResult());
        return entity;
    }

    private static TestCase toDomain(TestCaseEntity entity) {
        return new TestCase(
                new TestCaseId(entity.getId()),
                entity.getName(),
                new ProjectId(entity.getProjectId()),
                new FolderId(entity.getFolderId()),
                entity.getObjective(),
                entity.getPreconditions(),
                List.of()
        );
    }

    private static TestStep toDomain(TestStepEntity entity) {
        return new TestStep(
                new TestStepId(entity.getId()),
                new TestCaseId(entity.getTestCaseId()),
                entity.getStepOrder(),
                entity.getAction(),
                entity.getExpectedResult()
        );
    }
}
