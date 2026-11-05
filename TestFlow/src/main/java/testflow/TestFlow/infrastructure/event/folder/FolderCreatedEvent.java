package testflow.TestFlow.infrastructure.event.folder;

import org.springframework.context.ApplicationEvent;
import testflow.TestFlow.domain.folder.FolderId;

public record FolderCreatedEvent(String folderName, FolderId folderId, long duration) {
}
