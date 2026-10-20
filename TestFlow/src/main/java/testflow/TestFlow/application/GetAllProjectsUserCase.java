package testflow.TestFlow.application;

import org.springframework.stereotype.Service;
import testflow.TestFlow.application.input.CreateProjectInput;
import testflow.TestFlow.application.output.ProjectOutput;
import testflow.TestFlow.domain.Project;
import testflow.TestFlow.domain.ProjectRepository;

import java.util.List;

@Service
public class GetAllProjectsUserCase {

    private final ProjectRepository projectRepository;

    public GetAllProjectsUserCase(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<ProjectOutput> execute(){
        return projectRepository.findAll().stream().map(ProjectOutput::from).toList();
    }
}
