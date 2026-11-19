package testflow.TestFlow.http.testcase.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import testflow.TestFlow.application.testCase.usecases.UpdateTestCaseAndChildrenUseCase;
import testflow.TestFlow.http.testcase.request.UpdateTestCaseAndChildrenRequest;

import java.util.UUID;

@RestController
public class TestObjectController {
    private final UpdateTestCaseAndChildrenUseCase updateTestCaseAndChildrenUseCase;

    public TestObjectController(UpdateTestCaseAndChildrenUseCase updateTestCaseAndChildrenUseCase) {
        this.updateTestCaseAndChildrenUseCase = updateTestCaseAndChildrenUseCase;
    }

    @PatchMapping("/testCases/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTestCase(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateTestCaseAndChildrenRequest request
    ) {
        updateTestCaseAndChildrenUseCase.execute(id, request.toInput());
    }
}
