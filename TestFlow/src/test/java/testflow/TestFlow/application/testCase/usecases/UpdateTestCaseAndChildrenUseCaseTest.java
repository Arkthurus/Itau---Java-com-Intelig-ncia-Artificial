package testflow.TestFlow.application.testCase.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import testflow.TestFlow.application.testCase.input.UpdateTestCaseAndChildrenInput;
import testflow.TestFlow.domain.EntityNotFoundException;
import testflow.TestFlow.domain.testCase.InvalidTestObjectUpdateException;
import testflow.TestFlow.domain.testCase.TestCase;
import testflow.TestFlow.domain.testCase.TestCaseId;
import testflow.TestFlow.domain.testCase.TestCaseRepository;
import testflow.TestFlow.domain.testCase.TestObjectField;
import testflow.TestFlow.domain.testCase.UpdateTestObjCommand;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.domain.testCase.testStep.TestStep;
import testflow.TestFlow.domain.testCase.testStep.TestStepId;
import testflow.TestFlow.infrastructure.event.testCase.TestObjectUpdatedEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateTestCaseAndChildrenUseCaseTest {
    private TestCaseRepository testCaseRepository;
    private ApplicationEventPublisher eventPublisher;
    private UpdateTestCaseAndChildrenUseCase useCase;

    @BeforeEach
    void setUp() {
        testCaseRepository = mock(TestCaseRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        useCase = new UpdateTestCaseAndChildrenUseCase(testCaseRepository, eventPublisher);
    }

    @Test
    void updatesCaseAndAllStepsIncludingTheirOrder() {
        UUID caseId = UUID.randomUUID();
        UUID firstStepId = UUID.randomUUID();
        UUID secondStepId = UUID.randomUUID();
        TestCase testCase = new TestCase(
                new TestCaseId(caseId),
                "Caso original",
                new ProjectId(),
                new FolderId(),
                "Objetivo original",
                "Precondições originais",
                List.of()
        );
        TestStep firstStep = step(firstStepId, caseId, 1, "Ação original número um", "Resultado original um");
        TestStep secondStep = step(secondStepId, caseId, 2, "Ação original número dois", "Resultado original dois");
        when(testCaseRepository.findTestCaseById(new TestCaseId(caseId))).thenReturn(Optional.of(testCase));
        when(testCaseRepository.findAllTestStepsByTestCaseId(new TestCaseId(caseId)))
                .thenReturn(List.of(firstStep, secondStep));
        when(testCaseRepository.update(org.mockito.ArgumentMatchers.any())).thenReturn(true);

        useCase.execute(caseId, new UpdateTestCaseAndChildrenInput(
                "Caso atualizado",
                "Objetivo atualizado",
                null,
                List.of(
                        new UpdateTestCaseAndChildrenInput.TestStepInput(
                                secondStepId, 1, "Ação original número dois", "Resultado original dois"
                        ),
                        new UpdateTestCaseAndChildrenInput.TestStepInput(
                                firstStepId, 2, "Ação atualizada número um", "Resultado original um"
                        )
                )
        ));

        verify(testCaseRepository).update(new UpdateTestObjCommand(
                caseId, TestCase.class, TestObjectField.NAME, "Caso atualizado"
        ));
        verify(testCaseRepository).update(new UpdateTestObjCommand(
                caseId, TestCase.class, TestObjectField.OBJECTIVE, "Objetivo atualizado"
        ));
        verify(testCaseRepository).update(new UpdateTestObjCommand(
                secondStepId, TestStep.class, TestObjectField.STEP_ORDER, "1"
        ));
        verify(testCaseRepository).update(new UpdateTestObjCommand(
                firstStepId, TestStep.class, TestObjectField.STEP_ORDER, "2"
        ));
        verify(testCaseRepository).update(new UpdateTestObjCommand(
                firstStepId, TestStep.class, TestObjectField.ACTION, "Ação atualizada número um"
        ));
        verify(eventPublisher, org.mockito.Mockito.atLeastOnce()).publishEvent(
                argThat((Object event) -> event instanceof TestObjectUpdatedEvent)
        );
    }

    @Test
    void rejectsIncompleteStepListBeforeUpdatingAnything() {
        UUID caseId = UUID.randomUUID();
        TestCase testCase = new TestCase(
                new TestCaseId(caseId),
                "Caso original",
                new ProjectId(),
                new FolderId(),
                null,
                null,
                List.of()
        );
        TestStep existingStep = step(UUID.randomUUID(), caseId, 1, "Ação original número um", "Resultado original um");
        when(testCaseRepository.findTestCaseById(new TestCaseId(caseId))).thenReturn(Optional.of(testCase));
        when(testCaseRepository.findAllTestStepsByTestCaseId(new TestCaseId(caseId)))
                .thenReturn(List.of(existingStep));

        assertThrows(InvalidTestObjectUpdateException.class, () -> useCase.execute(
                caseId,
                new UpdateTestCaseAndChildrenInput("Caso atualizado", null, null, List.of())
        ));

        verify(testCaseRepository, never()).update(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void rejectsDuplicateStepOrdersBeforeUpdatingAnything() {
        UUID caseId = UUID.randomUUID();
        UUID firstStepId = UUID.randomUUID();
        UUID secondStepId = UUID.randomUUID();
        TestCase testCase = new TestCase(
                new TestCaseId(caseId),
                "Caso original",
                new ProjectId(),
                new FolderId(),
                null,
                null,
                List.of()
        );
        TestStep firstStep = step(firstStepId, caseId, 1, "Ação original número um", "Resultado original um");
        TestStep secondStep = step(secondStepId, caseId, 2, "Ação original número dois", "Resultado original dois");
        when(testCaseRepository.findTestCaseById(new TestCaseId(caseId))).thenReturn(Optional.of(testCase));
        when(testCaseRepository.findAllTestStepsByTestCaseId(new TestCaseId(caseId)))
                .thenReturn(List.of(firstStep, secondStep));

        assertThrows(InvalidTestObjectUpdateException.class, () -> useCase.execute(
                caseId,
                new UpdateTestCaseAndChildrenInput(
                        "Caso atualizado",
                        null,
                        null,
                        List.of(
                                new UpdateTestCaseAndChildrenInput.TestStepInput(
                                        firstStepId, 1, firstStep.getAction(), firstStep.getExpectedResult()
                                ),
                                new UpdateTestCaseAndChildrenInput.TestStepInput(
                                        secondStepId, 1, secondStep.getAction(), secondStep.getExpectedResult()
                                )
                        )
                )
        ));

        verify(testCaseRepository, never()).update(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void reportsMissingTestCase() {
        UUID caseId = UUID.randomUUID();
        when(testCaseRepository.findTestCaseById(new TestCaseId(caseId))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> useCase.execute(
                caseId,
                new UpdateTestCaseAndChildrenInput("Caso atualizado", null, null, List.of())
        ));

        verify(testCaseRepository, never()).findAllTestStepsByTestCaseId(org.mockito.ArgumentMatchers.any());
    }

    private static TestStep step(
            UUID stepId,
            UUID caseId,
            int order,
            String action,
            String expectedResult
    ) {
        return new TestStep(
                new TestStepId(stepId),
                new TestCaseId(caseId),
                order,
                action,
                expectedResult
        );
    }
}
