package testflow.TestFlow.infrastructure.event.project;

import testflow.TestFlow.domain.project.ProjectId;

public record ProjectCreatedEvent(ProjectId projectId, long durationMs) {
}
