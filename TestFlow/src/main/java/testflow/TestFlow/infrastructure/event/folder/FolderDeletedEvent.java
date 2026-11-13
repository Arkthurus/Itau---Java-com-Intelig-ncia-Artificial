package testflow.TestFlow.infrastructure.event.folder;

import testflow.TestFlow.domain.folder.FolderId;

public record FolderDeletedEvent(FolderId folderId, long duration) {
}
