package testflow.TestFlow.domain.folder;

import testflow.TestFlow.domain.project.ProjectId;

public class FolderAlreadyExistsExcepiton extends RuntimeException {
    public FolderAlreadyExistsExcepiton(FolderId folderId, String name) {
        super("Projetp com id " + folderId + " já existe como  nome de " + name);
    }
}
