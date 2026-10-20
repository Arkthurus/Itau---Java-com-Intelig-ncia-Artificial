package testflow.TestFlow.application;

import org.springframework.stereotype.Service;
import testflow.TestFlow.application.output.ProjectOutput;
import testflow.TestFlow.domain.ProjectId;
import testflow.TestFlow.domain.ProjectNotFoundException;
import testflow.TestFlow.domain.ProjectRepository;

@Service
public class DeleteProjectUseCase {
    private final ProjectRepository projectRepository;

    public DeleteProjectUseCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void execute(ProjectId id){
        if (projectRepository.findById(id).isEmpty()) {
            throw new ProjectNotFoundException(id);
        }

        projectRepository.delete(id);
    }
}
