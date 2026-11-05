package testflow.TestFlow.application.project.usecases;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import testflow.TestFlow.application.project.output.ProjectOutput;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.domain.project.ProjectNotFoundException;
import testflow.TestFlow.domain.project.ProjectRepository;
import testflow.TestFlow.infrastructure.event.project.ProjectEventHandler;
import testflow.TestFlow.infrastructure.event.project.ProjectViewedEvent;

@Service
public class GetProjectByIdUserCase {
    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher eventPublisher;

    public GetProjectByIdUserCase(ProjectRepository projectRepository, ProjectEventHandler projectEventHandler, ApplicationEventPublisher eventPublisher) {
        this.projectRepository = projectRepository;

        this.eventPublisher = eventPublisher;
    }

    @Cacheable(value = "projects", key = "#id.id()")
    public ProjectOutput execute(ProjectId id){
        Long start = System.nanoTime();
        ProjectOutput output = projectRepository.findById(id).map(ProjectOutput::from).orElseThrow(() -> new ProjectNotFoundException(id));

        long durationMs = (System.nanoTime() - start) / 1_000_000;
        eventPublisher.publishEvent(new ProjectViewedEvent(id, durationMs));

        return output;
    }
}
