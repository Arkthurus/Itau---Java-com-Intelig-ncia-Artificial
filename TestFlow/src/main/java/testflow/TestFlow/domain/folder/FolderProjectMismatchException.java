package testflow.TestFlow.domain.folder;

import testflow.TestFlow.domain.project.ProjectId;

public class FolderProjectMismatchException extends RuntimeException {

    public FolderProjectMismatchException(
            String parentFolderName,
            ProjectId parentFolderProjectId,
            String newFolderName,
            ProjectId newFolderProjectId
    ) {
        super(
                "A pasta '" + newFolderName
                        + "' não pode ficar dentro da pasta pai '"
                        + parentFolderName
                        + "', pois pertencem a projetos diferentes. "
                        + "ProjectId da pasta pai: " + parentFolderProjectId
                        + ". ProjectId informado: " + newFolderProjectId
        );
    }
}
