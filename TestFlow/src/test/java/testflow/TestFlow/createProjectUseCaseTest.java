package testflow.TestFlow;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import testflow.TestFlow.application.project.CreateProjectUseCase;
import testflow.TestFlow.application.project.input.CreateProjectInput;
import testflow.TestFlow.application.project.output.ProjectOutput;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class createProjectUseCaseTest {

    @Autowired
    CreateProjectUseCase useCase;

    @Test
    void should_create_project_successfully(){
        var input = new CreateProjectInput("TestFlow");

        ProjectOutput output = useCase.execute(input);

        assertNotNull(output);
        assertNotNull(output.id());
        assertNotNull(output.name());
        assertEquals("TestFlow", output.name());
    }
}