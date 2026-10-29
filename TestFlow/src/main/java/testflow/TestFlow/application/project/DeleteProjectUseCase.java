package testflow.TestFlow.application.project;

import org.springframework.stereotype.Service;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.domain.project.ProjectNotFoundException;
import testflow.TestFlow.domain.project.ProjectRepository;

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
