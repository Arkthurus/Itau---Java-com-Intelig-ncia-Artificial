package testflow.TestFlow.domain;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(ProjectId id) {

        super("Projeto com id " + id + " não encontrado!");
    }
}
