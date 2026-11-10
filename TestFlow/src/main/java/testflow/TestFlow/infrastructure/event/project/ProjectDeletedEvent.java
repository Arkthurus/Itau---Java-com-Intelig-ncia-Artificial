package testflow.TestFlow.infrastructure.event.project;

import testflow.TestFlow.domain.project.ProjectId;

public record ProjectDeletedEvent(ProjectId projectId, long durationMs) {
}
