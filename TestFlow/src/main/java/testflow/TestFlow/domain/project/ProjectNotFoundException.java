package testflow.TestFlow.domain.project;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(ProjectId id) {

        super("Projeto com id " + id + " não encontrado!");
    }
}
