package testflow.TestFlow.application.project.usecases;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import testflow.TestFlow.application.project.input.UpdateProjectInput;
import testflow.TestFlow.application.project.output.ProjectOutput;
import testflow.TestFlow.domain.project.Project;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.domain.project.ProjectNotFoundException;
import testflow.TestFlow.domain.project.ProjectRepository;
import testflow.TestFlow.infrastructure.event.project.ProjectUpdatedEvent;

@Service
public class UpdateProjectUseCase {
    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UpdateProjectUseCase(ProjectRepository projectRepository, ApplicationEventPublisher eventPublisher) {
        this.projectRepository = projectRepository;
        this.eventPublisher = eventPublisher;
    }

    @CacheEvict(value = "projects", key = "#id.id()")
    public ProjectOutput execute(ProjectId id, UpdateProjectInput input) {
        long start = System.nanoTime();

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));

        String oldName = project.getName();

        if (!projectRepository.updateName(id, input.name())) {
            throw new ProjectNotFoundException(id);
        }

        long duration = (System.nanoTime() - start) / 1_000_000;

        eventPublisher.publishEvent(
                new ProjectUpdatedEvent(id, duration, input.name(), oldName)
        );

        return new ProjectOutput(id.id().toString(), input.name());
    }
}
