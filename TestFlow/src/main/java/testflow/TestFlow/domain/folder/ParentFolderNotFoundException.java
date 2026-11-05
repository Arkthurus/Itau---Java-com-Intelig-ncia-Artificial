package testflow.TestFlow.domain.folder;

public class ParentFolderNotFoundException extends RuntimeException {
    public ParentFolderNotFoundException(FolderId id) {

        super("Pasta Pai com id " + id + " não encontrado!");
    }
}
