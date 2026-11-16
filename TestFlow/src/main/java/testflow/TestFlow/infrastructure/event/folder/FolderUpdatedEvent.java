package testflow.TestFlow.infrastructure.event.folder;

import jakarta.annotation.Nullable;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.project.ProjectId;

public record FolderUpdatedEvent(
        FolderId folderId,
        ProjectId projectId,
        @Nullable FolderId parentFolderId,
        long duration,
        String newFolderName,
        String oldFolderName
) {
}
