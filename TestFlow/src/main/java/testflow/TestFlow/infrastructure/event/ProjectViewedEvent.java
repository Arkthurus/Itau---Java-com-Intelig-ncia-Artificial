package testflow.TestFlow.infrastructure.event;

import testflow.TestFlow.domain.project.ProjectId;

public record ProjectViewedEvent(ProjectId projectId, long durationMs) {}
