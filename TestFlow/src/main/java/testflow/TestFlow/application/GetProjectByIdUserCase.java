package testflow.TestFlow.application;

import org.springframework.stereotype.Service;
import testflow.TestFlow.application.output.ProjectOutput;
import testflow.TestFlow.domain.Project;
import testflow.TestFlow.domain.ProjectId;
import testflow.TestFlow.domain.ProjectNotFoundException;
import testflow.TestFlow.domain.ProjectRepository;

import java.util.List;
import java.util.Optional;

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
