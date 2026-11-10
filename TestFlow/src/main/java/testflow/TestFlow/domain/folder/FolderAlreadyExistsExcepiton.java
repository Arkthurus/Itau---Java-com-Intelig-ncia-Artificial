package testflow.TestFlow.domain.folder;

import testflow.TestFlow.domain.project.ProjectId;

public class FolderAlreadyExistsExcepiton extends RuntimeException {
    public FolderAlreadyExistsExcepiton(FolderId folderId, String name) {
        super("Pasta com id " + folderId + " já existe com o nome : " + name);
    }
}
