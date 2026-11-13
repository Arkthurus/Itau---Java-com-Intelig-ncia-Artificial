package testflow.TestFlow.infrastructure.event.folder;

import jakarta.annotation.Nullable;
import testflow.TestFlow.domain.folder.FolderId;

public record FolderMovedEvent(
        FolderId folderId,
        @Nullable FolderId oldParentFolderId,
        @Nullable FolderId newParentFolderId,
        long durationMs
) {
}
