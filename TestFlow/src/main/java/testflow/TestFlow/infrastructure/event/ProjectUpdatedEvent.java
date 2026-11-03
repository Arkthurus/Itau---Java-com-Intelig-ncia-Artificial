package testflow.TestFlow.infrastructure.event;

import testflow.TestFlow.domain.project.ProjectId;

public record ProjectUpdatedEvent(ProjectId projectId, long durationMs, String newProjectName, String projectName) {
}
