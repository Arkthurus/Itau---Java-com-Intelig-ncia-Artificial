package testflow.TestFlow.application.project;

import org.springframework.stereotype.Service;
import testflow.TestFlow.application.project.input.UpdateProjectInput;
import testflow.TestFlow.application.project.output.ProjectOutput;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.domain.project.ProjectNotFoundException;
import testflow.TestFlow.domain.project.ProjectRepository;

@Service
public class UpdateProjectUseCase {
    private final ProjectRepository projectRepository;

    public UpdateProjectUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ProjectOutput execute(ProjectId id, UpdateProjectInput input){
        if (!projectRepository.updateName(id, input.name())){
            throw new ProjectNotFoundException(id);
        }
        return new ProjectOutput(id.id().toString(), input.name());
    }
}
