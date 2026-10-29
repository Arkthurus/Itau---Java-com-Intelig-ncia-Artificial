package testflow.TestFlow.application.project;

import org.springframework.stereotype.Service;
import testflow.TestFlow.application.project.output.ProjectOutput;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.domain.project.ProjectNotFoundException;
import testflow.TestFlow.domain.project.ProjectRepository;

@Service
public class GetProjectByIdUserCase {
    private final ProjectRepository projectRepository;

    public GetProjectByIdUserCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ProjectOutput execute(ProjectId id){
        return projectRepository.findById(id).map(ProjectOutput::from).orElseThrow(() -> new ProjectNotFoundException(id));
    }
}
