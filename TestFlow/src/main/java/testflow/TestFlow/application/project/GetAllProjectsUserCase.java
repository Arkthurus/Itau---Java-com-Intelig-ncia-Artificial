package testflow.TestFlow.application.project;

import org.springframework.stereotype.Service;
import testflow.TestFlow.application.project.output.ProjectOutput;
import testflow.TestFlow.domain.project.ProjectRepository;

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
