package testflow.TestFlow.infrastructure.event;

import testflow.TestFlow.domain.project.ProjectId;

public record ProjectDeletedEvent(ProjectId projectId, long durationMs) {
}
