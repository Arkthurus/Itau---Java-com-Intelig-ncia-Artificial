package testflow.TestFlow.infrastructure.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import testflow.TestFlow.domain.project.ProjectRepository;
import testflow.TestFlow.infrastructure.entity.project.Project;

@Component
@RepositoryEventHandler
public class ProjectEventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectEventHandler.class);

    @HandleAfterCreate
    public void handleAfterCreate(Project project) {
        LOG.warn("Handling after Create");
    }

    @HandleAfterSave
    public void handleAfterSave(Project project) {
        LOG.warn("Handling after save");
    }

    @HandleAfterDelete
    public void handleAfterDelete(Project project) {
        LOG.warn("Handling after delete");
    }

}
