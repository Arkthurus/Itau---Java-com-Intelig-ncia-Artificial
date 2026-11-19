package testflow.TestFlow.domain;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String entityName, Object id) {
        super(entityName + " com id " + id + " não encontrado.");
    }
}
