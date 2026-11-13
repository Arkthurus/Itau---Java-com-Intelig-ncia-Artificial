package testflow.TestFlow.domain.testcase;

import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.domain.testcase.teststep.TestStep;

import java.util.List;
import java.util.Objects;

public final class TestCase {
    private static final int NAME_MAX_LENGTH = 150;

    private final TestCaseId id;
    private final ProjectId projectId;
    private final FolderId folderId;
    private String name;
    private String objective;
    private String preconditions;
    private List<TestStep> steps;

    public TestCase(
            String name,
            String objective,
            String preconditions,
            ProjectId projectId,
            FolderId folderId,
            List<TestStep> steps
    ) {
        this(new TestCaseId(), name, objective, preconditions, projectId, folderId, steps);
    }

    public TestCase(
            TestCaseId id,
            String name,
            String objective,
            String preconditions,
            ProjectId projectId,
            FolderId folderId,
            List<TestStep> steps
    ) {
        this.id = Objects.requireNonNull(id, "TestCase id não pode ser nulo");
        this.projectId = Objects.requireNonNull(projectId, "ProjectId não pode ser nulo");
        this.folderId = Objects.requireNonNull(folderId, "FolderId não pode ser nulo");
        updateDefinition(name, objective, preconditions, steps);
    }

    public TestCaseId getId() {
        return id;
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    public FolderId getFolderId() {
        return folderId;
    }

    public String getName() {
        return name;
    }

    public String getObjective() {
        return objective;
    }

    public String getPreconditions() {
        return preconditions;
    }

    public List<TestStep> getSteps() {
        return steps;
    }

    public void updateDefinition(
            String name,
            String objective,
            String preconditions,
            List<TestStep> steps
    ) {
        String normalizedName = normalizeName(name);
        String normalizedObjective = normalizeOptionalText(objective);
        String normalizedPreconditions = normalizeOptionalText(preconditions);
        List<TestStep> normalizedSteps = normalizeSteps(steps);

        this.name = normalizedName;
        this.objective = normalizedObjective;
        this.preconditions = normalizedPreconditions;
        this.steps = normalizedSteps;
    }

    private static String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome do TestCase não pode ser vazio");
        }

        String normalizedName = name.trim();
        if (normalizedName.length() > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("Nome do TestCase não pode exceder 150 caracteres");
        }
        return normalizedName;
    }

    private static String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static List<TestStep> normalizeSteps(List<TestStep> steps) {
        if (steps == null || steps.isEmpty()) {
            throw new IllegalArgumentException("TestCase deve possuir pelo menos um TestStep");
        }

        // A posição de cada item na lista do agregado define sua ordem persistida, sempre iniciando em 1.
        return java.util.stream.IntStream.range(0, steps.size())
                .mapToObj(index -> Objects.requireNonNull(steps.get(index), "TestStep não pode ser nulo")
                        .withOrder(index + 1))
                .toList();
    }
}
