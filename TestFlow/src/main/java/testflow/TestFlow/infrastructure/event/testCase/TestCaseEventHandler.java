package testflow.TestFlow.infrastructure.event.testCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.event.EventListener;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RepositoryEventHandler
@EnableAsync
public class TestCaseEventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TestCaseEventHandler.class);

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterCreate(TestCaseCreatedEvent event) {
        LOG.warn("Caso de teste {} criado em {}ms", event.testCaseId(), event.durationMs());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterUpdate(TestObjectUpdatedEvent event) {
        LOG.warn(
                "{} com id ({}) foi atualizado: campo {} de '{}' para '{}' em {}ms",
                event.objectName(),
                event.id(),
                event.updatedField(),
                event.oldFieldValue(),
                event.newFieldValue(),
                event.durationMs()
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTestCaseViewed(TestCaseViewedEvent event) {
        LOG.info("Caso de teste {} visualizado em {}ms", event.testCaseId(), event.durationMs());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterDelete(TestCaseDeletedEvent event) {
        LOG.info("Caso de teste {} deletado em {}ms", event.testCaseId(), event.durationMs());
    }

    @CacheEvict(cacheNames = "testCases", allEntries = true)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void evictTestCasesAfterDelete(TestCaseDeletedEvent event) {
    }

    @CacheEvict(cacheNames = "testCases", allEntries = true)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void evictUpdatedTestObject(TestObjectUpdatedEvent event) {
    }

    @EventListener
    public void handleTestCasesListed(TestCasesListedEvent event) {
        LOG.info("Listados {} casos de teste em {}ms", event.count(), event.durationMs());
    }
}
