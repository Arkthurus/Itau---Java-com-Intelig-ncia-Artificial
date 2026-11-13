package testflow.TestFlow.domain.testCase;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.project.Project;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.domain.testcase.teststep.TestStep;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TestCase {
    private String name;
    private TestCaseId id;
    private FolderId folderId;
    private ProjectId projectId;
    @Nullable
    private String objective;
    @Nullable
    private String preConditions;
    private List<TestStep> testSteps;

    //Constructor de criação
    public TestCase(String name,
                    FolderId folderId,
                    ProjectId projectId,
                    @Nullable String objective,
                    @Nullable String preConditions
    ){
        Assert.hasText(name, "Nome do TestCase não pode ser vazio");
        Assert.notNull(projectId, "ProjectId não pode ser nulo");
        Assert.notNull(folderId, "FolderId não pode ser nulo");

        this.id = new TestCaseId(UUID.randomUUID());
        this.name = name;
        this.folderId = folderId;
        this.projectId = projectId;
        this.objective = objective;
        this.preConditions = preConditions;
        this.testSteps = new ArrayList<>();

    }

    //Constructor de consulta ao banco
    public TestCase(TestCaseId id,
                    String name,
                    FolderId folderId,
                    ProjectId projectId,
                    @Nullable String objective,
                    @Nullable String preConditions,
                    List<TestStep> testSteps
    ){
        Assert.hasText(name, "Nome do TestCase não pode ser vazio");
        Assert.notNull(projectId, "ProjectId não pode ser nulo");
        Assert.notNull(folderId, "FolderId não pode ser nulo");

        this.id = id;
        this.name = name;
        this.folderId = folderId;
        this.projectId = projectId;
        this.objective = objective;
        this.preConditions = preConditions;
        this.testSteps = testSteps;

    }
}
