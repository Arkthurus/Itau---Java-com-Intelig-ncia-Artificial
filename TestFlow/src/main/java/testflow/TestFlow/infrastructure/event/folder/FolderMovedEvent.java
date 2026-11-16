package testflow.TestFlow.infrastructure.event.folder;

import jakarta.annotation.Nullable;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.project.ProjectId;

public record FolderMovedEvent(
        FolderId folderId,
        ProjectId projectId,
        String folderName,
        @Nullable FolderId oldParentFolderId,
        @Nullable FolderId newParentFolderId,
        long durationMs
) {
}
