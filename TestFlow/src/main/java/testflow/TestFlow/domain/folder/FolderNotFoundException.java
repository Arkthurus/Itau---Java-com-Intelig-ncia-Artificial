package testflow.TestFlow.domain.folder;

import testflow.TestFlow.domain.project.ProjectId;

public class FolderNotFoundException extends RuntimeException {
    public FolderNotFoundException(FolderId id) {

        super("Folder com id " + id + " não encontrado!");
    }
}
