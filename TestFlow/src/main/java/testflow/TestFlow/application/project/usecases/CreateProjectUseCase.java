package testflow.TestFlow.application.project.usecases;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import testflow.TestFlow.application.project.input.CreateProjectInput;
import testflow.TestFlow.application.project.output.ProjectOutput;
import testflow.TestFlow.domain.project.Project;
import testflow.TestFlow.domain.project.ProjectRepository;
import testflow.TestFlow.infrastructure.event.ProjectCreatedEvent;

@Service
public class CreateProjectUseCase {
    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CreateProjectUseCase(ProjectRepository projectRepository, ApplicationEventPublisher eventPublisher) {
        this.projectRepository = projectRepository;
        this.eventPublisher = eventPublisher;
    }

    public ProjectOutput execute(CreateProjectInput input){
        long start = System.nanoTime();
        var project = new Project(input.name());
        var saved = projectRepository.save(project);
        long duration = (System.nanoTime() - start) / 1_000_000;
        eventPublisher.publishEvent(new ProjectCreatedEvent(saved.getId(),duration));
        return ProjectOutput.from(saved);
    }
}
