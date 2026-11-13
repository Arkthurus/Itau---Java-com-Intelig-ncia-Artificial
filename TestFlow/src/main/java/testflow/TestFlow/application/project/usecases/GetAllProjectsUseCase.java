package testflow.TestFlow.application.project.usecases;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import testflow.TestFlow.application.project.output.ProjectOutput;
import testflow.TestFlow.domain.project.ProjectRepository;
import testflow.TestFlow.infrastructure.event.project.ProjectsListedEvent;

import java.util.List;

@Service
public class GetAllProjectsUseCase {
    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher eventPublisher;

    public GetAllProjectsUseCase(ProjectRepository projectRepository, ApplicationEventPublisher eventPublisher) {
        this.projectRepository = projectRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<ProjectOutput> execute(){
        long start = System.nanoTime();

        List<ProjectOutput> projects = projectRepository.findAll()
                .stream()
                .map(ProjectOutput::from)
                .toList();

        long duration = (System.nanoTime() - start) / 1_000_000;
        eventPublisher.publishEvent(new ProjectsListedEvent(projects.size(), duration));

        return projects;
    }
}
