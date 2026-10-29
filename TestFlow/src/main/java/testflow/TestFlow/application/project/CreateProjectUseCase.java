package testflow.TestFlow.application.project;

import org.springframework.stereotype.Service;
import testflow.TestFlow.application.project.input.CreateProjectInput;
import testflow.TestFlow.application.project.output.ProjectOutput;
import testflow.TestFlow.domain.project.Project;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.domain.project.ProjectRepository;

@Service
public class CreateProjectUseCase {
    private final ProjectRepository projectRepository;

    public CreateProjectUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ProjectOutput execute(CreateProjectInput input){
        var project = new Project(new ProjectId(), input.name());
        var saved = projectRepository.save(project);
        return ProjectOutput.from(saved);
    }
}
