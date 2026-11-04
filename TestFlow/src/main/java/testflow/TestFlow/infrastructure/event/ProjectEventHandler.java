package testflow.TestFlow.infrastructure.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler
@EnableAsync
public class ProjectEventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectEventHandler.class);

    @Async
    @EventListener
    public void handleAfterCreate(ProjectCreatedEvent event) {
        LOG.warn("Projeto {} criado em {}ms", event.projectId(), event.durationMs());
    }

    @Async
    @EventListener
    public void handleAfterUpdate(ProjectUpdatedEvent event) {
        LOG.warn("Nome Projeto {} atualizado de {} para {} em {}ms", event.projectId(), event.projectName(), event.newProjectName(), event.durationMs());
    }

    @Async
    @EventListener
    public void handleProjectViewed(ProjectViewedEvent event) {
        LOG.info("Projeto {} visualizado em {}ms", event.projectId(), event.durationMs());
    }

    @Async
    @EventListener
    public void handleAfterDelete(ProjectDeletedEvent event) {
        LOG.info("Projeto {} deletado em {}ms", event.projectId(), event.durationMs());
    }

}
