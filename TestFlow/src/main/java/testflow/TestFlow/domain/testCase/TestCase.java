package testflow.TestFlow.domain.testCase;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.domain.testCase.testStep.TestStep;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class TestCase {
    private TestCaseId id;
    @NotNull
    private ProjectId projectId;
    @NotNull
    private FolderId folderId;
    @NotBlank
    @Size(min = 3, max = 100)
    private String name;
    @Nullable
    @Size(min = 3, max = 100)
    private String objective;
    @Nullable
    @Size(min = 3, max = 100)
    private String preconditions;
    private List<TestStep> steps;


    //Construtor de criação
    public TestCase(
            String name,
            ProjectId projectId,
            FolderId folderId,
            @Nullable String objective,
            @Nullable String preconditions
    ) {
        Assert.notNull(projectId, "ProjectId não pode ser nulo.");
        Assert.notNull(folderId, "FolderId não pode ser nulo.");
        Assert.hasText(name, "Name do TestCase não pode ser vazio");
        if (objective != null && objective.isBlank()) {
            throw new IllegalArgumentException(
                    "Objetivo não pode conter apenas espaços em branco"
            );
        }
        if (preconditions != null && preconditions.isBlank()) {
            throw new IllegalArgumentException(
                    "Pré-condições não podem conter apenas espaços em branco"
            );
        }

        this.id = new TestCaseId();
        this.projectId = projectId;
        this.folderId = folderId;
        this.name = name;
        this.objective = objective;
        this.preconditions = preconditions;
        this.steps = new ArrayList<>();
    }

    //Construtor De consulta ao banco
    public TestCase(
            TestCaseId id,
            String name,
            ProjectId projectId,
            FolderId folderId,
            @Nullable String objective,
            @Nullable String preconditions,
            List<TestStep> steps
    ) {
        Assert.notNull(id, "TestCaseId não pode ser nulo.");
        Assert.notNull(projectId, "ProjectId não pode ser nulo.");
        Assert.notNull(folderId, "FolderId não pode ser nulo.");
        Assert.hasText(name, "Name do TestCase não pode ser vazio");
        if (objective != null && objective.isBlank()) {
            throw new IllegalArgumentException(
                    "Objetivo não pode conter apenas espaços em branco"
            );
        }
        if (preconditions != null && preconditions.isBlank()) {
            throw new IllegalArgumentException(
                    "Pre-condições não pode conter apenas espaços em branco"
            );
        }

        this.id = id;
        this.projectId = projectId;
        this.folderId = folderId;
        this.preconditions = preconditions;
        this.objective = objective;
        this.steps = steps;
    }
}

