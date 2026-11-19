package testflow.TestFlow.domain.testCase;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.testCase.testStep.TestStep;
import testflow.TestFlow.domain.testCase.testStep.TestStepId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestCaseRepository {
    TestCase saveTestCase(TestCase testCase);
    TestStep saveTestStep(TestStep step);
    List<TestCase> findAllTestCasesByFolderId(FolderId folderId);
    List<TestStep> findAllTestStepsByTestCaseId(TestCaseId testCaseId);
    Optional<TestCase> findTestCaseById(TestCaseId testCaseId);
    Optional<TestStep> findTestStepById(TestStepId testStepId);
    void deleteTestCase(TestCaseId testCaseId);
    void deleteTestStep(TestStepId testStepId);
    boolean update(UpdateTestObjCommand command);
    boolean moveTestCaseToOtherFolder(TestCaseId id, @NotNull FolderId testCaseFolderId);
}
