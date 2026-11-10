package testflow.TestFlow.infrastructure.event.project;

import testflow.TestFlow.domain.project.ProjectId;

public record ProjectViewedEvent(ProjectId projectId, long durationMs) {}
