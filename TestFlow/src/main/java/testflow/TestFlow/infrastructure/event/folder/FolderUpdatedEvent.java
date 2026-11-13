package testflow.TestFlow.infrastructure.event.folder;

import testflow.TestFlow.domain.folder.FolderId;

public record FolderUpdatedEvent(FolderId folderId, long duration, String newFolderName, String oldFolderName) {
}
