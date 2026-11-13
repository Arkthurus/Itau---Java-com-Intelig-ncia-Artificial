package testflow.TestFlow.infrastructure.event.project;

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
import testflow.TestFlow.infrastructure.event.folder.FolderUpdatedEvent;

@Component
@RepositoryEventHandler
@EnableAsync
public class ProjectEventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectEventHandler.class);

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterCreate(ProjectCreatedEvent event) {
        LOG.warn("Projeto {} criado em {}ms", event.projectId(), event.durationMs());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterUpdate(ProjectUpdatedEvent event) {
        LOG.warn("Nome Projeto {} atualizado de {} para {} em {}ms", event.projectId(), event.projectName(), event.newProjectName(), event.durationMs());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProjectViewed(ProjectViewedEvent event) {
        LOG.info("Projeto {} visualizado em {}ms", event.projectId(), event.durationMs());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterDelete(ProjectDeletedEvent event) {
        LOG.info("Projeto {} deletado em {}ms", event.projectId(), event.durationMs());
    }

    //Cache de Update
    @CacheEvict(cacheNames = "projects", key = "#event.project().id()")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void evictUpdatedProject(ProjectUpdatedEvent event) {
    }

    //GetAll
    @EventListener
    public void handleProjectsListed(ProjectsListedEvent event) {
        LOG.info("Listados {} projetos em {}ms", event.count(), event.duration());
    }

}
