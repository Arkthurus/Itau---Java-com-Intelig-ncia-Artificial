package testflow.TestFlow.infrastructure.repository.project.http;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import testflow.TestFlow.application.project.usecases.*;
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
    private GetAllProjectsUseCase getAllProjectsUseCase;
    private GetProjectByIdUseCase getProjectByIdUseCase;
    private UpdateProjectUseCase updateProjectUseCase;
    private DeleteProjectUseCase deleteProjectUseCase;

    public ProjectController(CreateProjectUseCase createProjectUseCase, GetAllProjectsUseCase getAllProjectsUseCase, GetProjectByIdUseCase getProjectByIdUseCase, UpdateProjectUseCase updateProjectUseCase, DeleteProjectUseCase deleteProjectUseCase){
        this.createProjectUseCase = createProjectUseCase;
        this.getAllProjectsUseCase = getAllProjectsUseCase;
        this.getProjectByIdUseCase = getProjectByIdUseCase;
        this.updateProjectUseCase = updateProjectUseCase;
        this.deleteProjectUseCase = deleteProjectUseCase;
    }

    @PostMapping
    ProjectResponse createProject(@RequestBody @Valid CreateProjectRequest request){
        return ProjectResponse.from(createProjectUseCase.execute(request.toInput()));
    }

    @GetMapping
    List<ProjectResponse> getAllProjects(){
        return getAllProjectsUseCase.execute().stream().map(ProjectResponse::from).toList();
    }

    @GetMapping("/{id}")
    ProjectResponse getProjectById(@PathVariable UUID id){
        return ProjectResponse.from(getProjectByIdUseCase.execute(new ProjectId(id)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable UUID id){
        deleteProjectUseCase.execute(new ProjectId(id));
    }

    @PutMapping("/{id}")
    ProjectResponse update(@PathVariable UUID id, @RequestBody @Valid UpdateProjectRequest request){
        return ProjectResponse.from(updateProjectUseCase.execute(new ProjectId(id), request.toInput()));
    }
}
