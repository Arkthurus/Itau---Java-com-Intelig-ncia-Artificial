package testflow.TestFlow.application.project;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.domain.project.ProjectNotFoundException;
import testflow.TestFlow.domain.project.ProjectRepository;
import testflow.TestFlow.infrastructure.event.ProjectDeletedEvent;

@Service
public class DeleteProjectUseCase {
    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DeleteProjectUseCase(ProjectRepository projectRepository, ApplicationEventPublisher eventPublisher) {
        this.projectRepository = projectRepository;
        this.eventPublisher = eventPublisher;
    }

    @CacheEvict(value = "projects", key = "#id.id()")
    public void execute(ProjectId id){
        long start = System.nanoTime();
        if (projectRepository.findById(id).isEmpty()) {
            throw new ProjectNotFoundException(id);
        }

        long duration = (System.nanoTime() - start) / 1_000_000;
        eventPublisher.publishEvent(new ProjectDeletedEvent(id, duration));

        projectRepository.delete(id);
    }
}
