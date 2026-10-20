package testflow.TestFlow.application;

import org.springframework.stereotype.Service;
import testflow.TestFlow.application.input.UpdateProjectInput;
import testflow.TestFlow.application.output.ProjectOutput;
import testflow.TestFlow.domain.Project;
import testflow.TestFlow.domain.ProjectId;
import testflow.TestFlow.domain.ProjectNotFoundException;
import testflow.TestFlow.domain.ProjectRepository;

@Service
public class UpdateProjectUseCase {
    private final ProjectRepository projectRepository;

    public UpdateProjectUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ProjectOutput execute(ProjectId id, UpdateProjectInput input){
        var project = projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException(id));
        project.update(input.name());
        var updated = projectRepository.create(project);
        return ProjectOutput.from(project);
    }
}
