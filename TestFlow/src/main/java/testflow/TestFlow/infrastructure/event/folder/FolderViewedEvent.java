package testflow.TestFlow.infrastructure.event.folder;

import testflow.TestFlow.domain.folder.FolderId;

public record FolderViewedEvent(FolderId folderId, long duration) {
}
