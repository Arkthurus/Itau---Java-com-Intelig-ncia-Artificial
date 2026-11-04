package testflow.TestFlow.domain.project;

public class ProjectAlreadyExistException extends RuntimeException {
    public ProjectAlreadyExistException(ProjectId projectId, String name) {
        super("Projetp com id " + projectId + " já existe como  nome de " + name);
    }
}
