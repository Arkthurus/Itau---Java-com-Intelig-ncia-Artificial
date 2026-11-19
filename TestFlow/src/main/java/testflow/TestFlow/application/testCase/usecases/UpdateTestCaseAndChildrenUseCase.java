package testflow.TestFlow.application.testCase.usecases;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import testflow.TestFlow.application.testCase.input.UpdateTestCaseAndChildrenInput;
import testflow.TestFlow.domain.EntityNotFoundException;
import testflow.TestFlow.domain.testCase.InvalidTestObjectUpdateException;
import testflow.TestFlow.domain.testCase.TestCase;
import testflow.TestFlow.domain.testCase.TestCaseId;
import testflow.TestFlow.domain.testCase.TestCaseRepository;
import testflow.TestFlow.domain.testCase.TestObjectField;
import testflow.TestFlow.domain.testCase.UpdateTestObjCommand;
import testflow.TestFlow.domain.testCase.testStep.TestStep;
import testflow.TestFlow.domain.testCase.testStep.TestStepId;
import testflow.TestFlow.infrastructure.event.testCase.TestObjectUpdatedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class UpdateTestCaseAndChildrenUseCase {
    private final TestCaseRepository testCaseRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UpdateTestCaseAndChildrenUseCase(
            TestCaseRepository testCaseRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.testCaseRepository = testCaseRepository;
        this.eventPublisher = eventPublisher;
    }

    // Mantem a edicao do caso e de todos os passos na mesma transacao:
    // se alguma atualizacao falhar, nenhuma alteracao parcial deve ser confirmada.
    @Transactional
    public void execute(UUID id, UpdateTestCaseAndChildrenInput input) {
        long startTime = System.nanoTime();
        TestCaseId testCaseId = new TestCaseId(id);
        TestCase testCase = testCaseRepository.findTestCaseById(testCaseId)
                .orElseThrow(() -> new EntityNotFoundException("TestCase", id));

        // A lista persistida e a referencia para validar que o request representa
        // exatamente os passos deste caso, mesmo quando a ordem enviada mudou.
        List<TestStep> currentSteps = testCaseRepository.findAllTestStepsByTestCaseId(testCaseId);
        // Indexa os passos pelo UUID para validar a lista enviada e recuperar cada valor atual,
        // independentemente da ordem em que os passos aparecem no request.
        Map<UUID, TestStep> currentStepsById = indexCurrentSteps(currentSteps);
        validateStepSet(input.steps(), currentStepsById);
        validateStepOrders(input.steps());

        // Guarda somente alteracoes reais; os eventos sao publicados depois de
        // todas as persistencias terem sido aceitas dentro da transacao.
        List<PendingUpdate> updates = new ArrayList<>();
        updateField(
                testCaseId.id(), TestCase.class, TestObjectField.NAME,
                testCase.getName(), input.name(), updates
        );
        updateField(
                testCaseId.id(), TestCase.class, TestObjectField.OBJECTIVE,
                testCase.getObjective(), input.objective(), updates
        );
        updateField(
                testCaseId.id(), TestCase.class, TestObjectField.PRECONDITIONS,
                testCase.getPreconditions(), input.preconditions(), updates
        );

        // Cada passo e localizado pelo ID, nao pela posicao da lista, pois a lista
        // recebida pode estar em uma ordem diferente da atual.
        for (UpdateTestCaseAndChildrenInput.TestStepInput updatedStep : input.steps()) {
            TestStep currentStep = currentStepsById.get(updatedStep.id());
            updateField(
                    updatedStep.id(), TestStep.class, TestObjectField.ACTION,
                    currentStep.getAction(), updatedStep.action(), updates
            );
            updateField(
                    updatedStep.id(), TestStep.class, TestObjectField.EXPECTED_RESULT,
                    currentStep.getExpectedResult(), updatedStep.expectedResult(), updates
            );
            updateField(
                    updatedStep.id(), TestStep.class, TestObjectField.STEP_ORDER,
                    Integer.toString(currentStep.getStepOrder()),
                    Integer.toString(updatedStep.stepOrder()),
                    updates
            );
        }

        // O mesmo tempo total e associado aos eventos gerados por este envio da edicao.
        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        updates.forEach(update -> eventPublisher.publishEvent(new TestObjectUpdatedEvent(
                update.objectName(),
                update.id(),
                update.field(),
                update.objectType(),
                durationMs,
                update.oldValue(),
                update.newValue()
        )));
    }

    private Map<UUID, TestStep> indexCurrentSteps(List<TestStep> currentSteps) {
        Map<UUID, TestStep> stepsById = new HashMap<>();
        for (TestStep step : currentSteps) {
            // O mapa permite encontrar o estado anterior de cada passo em tempo constante.
            stepsById.put(step.getId().id(), step);
        }
        return stepsById;
    }

    private void validateStepSet(
            List<UpdateTestCaseAndChildrenInput.TestStepInput> requestedSteps,
            Map<UUID, TestStep> currentStepsById
    ) {
        if (requestedSteps == null) {
            throw new InvalidTestObjectUpdateException("A lista de passos deve ser informada.");
        }

        // Impede IDs repetidos e garante que nenhum passo de outro caso seja aceito.
        Set<UUID> requestedIds = new HashSet<>();
        for (UpdateTestCaseAndChildrenInput.TestStepInput step : requestedSteps) {
            if (step == null || step.id() == null || !requestedIds.add(step.id())) {
                throw new InvalidTestObjectUpdateException(
                        "A lista de passos contém um item inválido ou ID duplicado."
                );
            }
        }

        // A igualdade de conjuntos tambem rejeita listas incompletas ou com passos extras.
        if (!requestedIds.equals(currentStepsById.keySet())) {
            throw new InvalidTestObjectUpdateException(
                    "A lista deve conter exatamente todos os passos existentes do TestCase."
            );
        }
    }

    private void validateStepOrders(List<UpdateTestCaseAndChildrenInput.TestStepInput> steps) {
        Set<Integer> orders = new HashSet<>();
        // Cada passo precisa ocupar uma posicao positiva e exclusiva.
        for (UpdateTestCaseAndChildrenInput.TestStepInput step : steps) {
            if (step.stepOrder() == null || step.stepOrder() < 1 || !orders.add(step.stepOrder())) {
                throw new InvalidTestObjectUpdateException(
                        "A ordem dos passos deve conter posições positivas e sem duplicatas."
                );
            }
        }
        // Exige uma sequencia sem lacunas (1..N), para manter uma ordenacao previsivel.
        for (int expectedOrder = 1; expectedOrder <= steps.size(); expectedOrder++) {
            if (!orders.contains(expectedOrder)) {
                throw new InvalidTestObjectUpdateException(
                        "A ordem dos passos deve ser contínua, começando em 1."
                );
            }
        }
    }

    private void updateField(
            UUID id,
            Class<?> objectType,
            TestObjectField field,
            String oldValue,
            String newValue,
            List<PendingUpdate> updates
    ) {
        // Evita gravacao e evento quando o valor enviado nao mudou.
        if (Objects.equals(oldValue, newValue)) {
            return;
        }
        String objectName = objectType == TestCase.class ? "TestCase" : "TestStep";
        UpdateTestObjCommand command = new UpdateTestObjCommand(id, objectType, field, newValue);
        if (!testCaseRepository.update(command)) {
            throw new EntityNotFoundException(objectName, id);
        }
        // Registra os valores antigo e novo para publicar o evento apos as atualizacoes.
        updates.add(new PendingUpdate(objectName, id, field, objectType, oldValue, newValue));
    }

    // Mantem o contexto de cada mudanca ate o momento de publicar seus eventos.
    private record PendingUpdate(
            String objectName,
            UUID id,
            TestObjectField field,
            Class<?> objectType,
            String oldValue,
            String newValue
    ) {
    }
}
