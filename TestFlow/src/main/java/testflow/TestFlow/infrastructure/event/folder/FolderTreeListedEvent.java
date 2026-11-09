package testflow.TestFlow.infrastructure.event.folder;

import testflow.TestFlow.domain.project.ProjectId;

public record FolderTreeListedEvent(ProjectId projectId, int count, long durationMs) {
}
