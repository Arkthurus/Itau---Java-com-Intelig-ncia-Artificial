package testflow.TestFlow.domain.folder;

public class FolderMoveWouldCreateCycleException extends RuntimeException {
    public FolderMoveWouldCreateCycleException(FolderId folderId, FolderId newParentFolderId) {
        super("A pasta " + folderId + " não pode ser movida para dentro de " + newParentFolderId
                + " porque isso criaria um ciclo na hierarquia.");
    }
}
