package testflow.TestFlow.infrastructure.repository.project.http;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import testflow.TestFlow.application.project.*;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.infrastructure.repository.project.http.request.CreateProjectRequest;
import testflow.TestFlow.infrastructure.repository.project.http.request.UpdateProjectRequest;
import testflow.TestFlow.infrastructure.repository.project.http.response.ProjectResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private CreateProjectUseCase createProjectUseCase;
    private GetAllProjectsUserCase getAllProjectsUserCase;
    private GetProjectByIdUserCase getProjectByIdUserCase;
    private UpdateProjectUseCase updateProjectUseCase;
    private DeleteProjectUseCase deleteProjectUseCase;

    public ProjectController(CreateProjectUseCase createProjectUseCase, GetAllProjectsUserCase getAllProjectsUserCase, GetProjectByIdUserCase getProjectByIdUserCase, UpdateProjectUseCase updateProjectUseCase, DeleteProjectUseCase deleteProjectUseCase){
        this.createProjectUseCase = createProjectUseCase;
        this.getAllProjectsUserCase = getAllProjectsUserCase;
        this.getProjectByIdUserCase = getProjectByIdUserCase;
        this.updateProjectUseCase = updateProjectUseCase;
        this.deleteProjectUseCase = deleteProjectUseCase;
    }

    @PostMapping
    ProjectResponse createProject(@RequestBody @Valid CreateProjectRequest request){
        var input = request.toInput();
        var output = createProjectUseCase.execute(input);
        return ProjectResponse.toOutput(output);
    }

    @GetMapping
    List<ProjectResponse> getAllProjects(){
        return getAllProjectsUserCase.execute().stream().map(ProjectResponse::toOutput).toList();
    }

    @GetMapping("/{id}")
    ProjectResponse getProjectById(@PathVariable UUID id){
        var output = getProjectByIdUserCase.execute(new ProjectId(id));
        return ProjectResponse.toOutput(output);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID id){
        deleteProjectUseCase.execute(new ProjectId(id));
    }

    @PutMapping("/{id}")
    ProjectResponse update(@PathVariable UUID id, @RequestBody @Valid UpdateProjectRequest request){
        var input = request.toInput();
        var output = updateProjectUseCase.execute(new ProjectId(id), input);
        return ProjectResponse.toOutput(output);
    }
}
