package testflow.TestFlow.application;

import org.springframework.stereotype.Service;
import testflow.TestFlow.application.input.CreateProjectInput;
import testflow.TestFlow.application.output.ProjectOutput;
import testflow.TestFlow.domain.Project;
import testflow.TestFlow.domain.ProjectRepository;

@Service
public class CreateProjectUseCase {
    private final ProjectRepository projectRepository;

    public CreateProjectUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ProjectOutput execute(CreateProjectInput input){
        var project = new Project(input.name());
        var saved = projectRepository.create(project);
        return ProjectOutput.from(saved);
    }
}
