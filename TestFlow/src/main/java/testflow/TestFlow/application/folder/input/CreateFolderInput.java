package testflow.TestFlow.application.folder.input;

import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.project.ProjectId;

public record CreateFolderInput(String name,
                                ProjectId projectId,
                                FolderId parentFolderId
) {}
