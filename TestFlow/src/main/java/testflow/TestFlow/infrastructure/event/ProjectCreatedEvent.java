package testflow.TestFlow.infrastructure.event;

import testflow.TestFlow.domain.project.ProjectId;

public record ProjectCreatedEvent(ProjectId projectId, long durationMs) {
}
