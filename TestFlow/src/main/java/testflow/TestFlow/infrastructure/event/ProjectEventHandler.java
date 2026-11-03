package testflow.TestFlow.infrastructure.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import testflow.TestFlow.domain.project.ProjectRepository;
import testflow.TestFlow.infrastructure.entity.project.Project;

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
