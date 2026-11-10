package testflow.TestFlow.infrastructure.repository.folder.http;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import testflow.TestFlow.application.folder.usecases.CreateFolderUseCase;
import testflow.TestFlow.application.folder.usecases.DeleteFolderUseCase;
import testflow.TestFlow.application.folder.usecases.FindFolderTreeByProjectIdUseCase;
import testflow.TestFlow.application.folder.usecases.FindRootFoldersByProjectIdUseCase;
import testflow.TestFlow.application.folder.usecases.FindSubFoldersUseCase;
import testflow.TestFlow.application.folder.usecases.GetFolderByIdUseCase;
import testflow.TestFlow.application.folder.usecases.MoveFolderUseCase;
import testflow.TestFlow.application.folder.usecases.UpdateFolderUseCase;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.infrastructure.repository.folder.http.request.CreateFolderRequest;
import testflow.TestFlow.infrastructure.repository.folder.http.request.MoveFolderRequest;
import testflow.TestFlow.infrastructure.repository.folder.http.request.UpdateFolderRequest;
import testflow.TestFlow.infrastructure.repository.folder.http.response.FolderResponse;
import testflow.TestFlow.infrastructure.repository.folder.http.response.FolderTreeResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects/{projectId}/folders")
public class FolderController {
    private final CreateFolderUseCase createFolderUseCase;
    private final GetFolderByIdUseCase getFolderByIdUseCase;
    private final UpdateFolderUseCase updateFolderUseCase;
    private final DeleteFolderUseCase deleteFolderUseCase;
    private final MoveFolderUseCase moveFolderUseCase;
    private final FindRootFoldersByProjectIdUseCase findRootFoldersByProjectIdUseCase;
    private final FindSubFoldersUseCase findSubFoldersUseCase;
    private final FindFolderTreeByProjectIdUseCase findFolderTreeByProjectIdUseCase;

    public FolderController(
            CreateFolderUseCase createFolderUseCase,
            GetFolderByIdUseCase getFolderByIdUseCase,
            UpdateFolderUseCase updateFolderUseCase,
            DeleteFolderUseCase deleteFolderUseCase,
            MoveFolderUseCase moveFolderUseCase,
            FindRootFoldersByProjectIdUseCase findRootFoldersByProjectIdUseCase,
            FindSubFoldersUseCase findSubFoldersUseCase,
            FindFolderTreeByProjectIdUseCase findFolderTreeByProjectIdUseCase
    ) {
        this.createFolderUseCase = createFolderUseCase;
        this.getFolderByIdUseCase = getFolderByIdUseCase;
        this.updateFolderUseCase = updateFolderUseCase;
        this.deleteFolderUseCase = deleteFolderUseCase;
        this.moveFolderUseCase = moveFolderUseCase;
        this.findRootFoldersByProjectIdUseCase = findRootFoldersByProjectIdUseCase;
        this.findSubFoldersUseCase = findSubFoldersUseCase;
        this.findFolderTreeByProjectIdUseCase = findFolderTreeByProjectIdUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FolderResponse createFolder(
            @PathVariable("projectId") UUID projectId,
            @RequestBody @Valid CreateFolderRequest request
    ) {
        return FolderResponse.from(createFolderUseCase.execute(
                request.toInput(new ProjectId(projectId))
        ));
    }

    @GetMapping
    public List<FolderResponse> findRootFolders(@PathVariable("projectId") UUID projectId) {
        return findRootFoldersByProjectIdUseCase.execute(new ProjectId(projectId))
                .stream()
                .map(FolderResponse::from)
                .toList();
    }

    @GetMapping("/tree")
    public List<FolderTreeResponse> findFolderTree(@PathVariable("projectId") UUID projectId) {
        return findFolderTreeByProjectIdUseCase.execute(new ProjectId(projectId))
                .stream()
                .map(FolderTreeResponse::from)
                .toList();
    }

    @GetMapping("/{parentFolderId}/subfolders")
    public List<FolderResponse> findSubFolders(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("parentFolderId") UUID parentFolderId
    ) {
        return findSubFoldersUseCase.execute(
                        new ProjectId(projectId),
                        new FolderId(parentFolderId)
                )
                .stream()
                .map(FolderResponse::from)
                .toList();
    }

    @GetMapping("/{folderId}")
    public FolderResponse getFolderById(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("folderId") UUID folderId
    ) {
        return FolderResponse.from(getFolderByIdUseCase.execute(
                new ProjectId(projectId),
                new FolderId(folderId)
        ));
    }

    @PutMapping("/{folderId}")
    public FolderResponse updateFolder(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("folderId") UUID folderId,
            @RequestBody @Valid UpdateFolderRequest request
    ) {
        return FolderResponse.from(updateFolderUseCase.execute(
                new ProjectId(projectId),
                new FolderId(folderId),
                request.toInput()
        ));
    }

    @DeleteMapping("/{folderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFolder(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("folderId") UUID folderId
    ) {
        deleteFolderUseCase.execute(new ProjectId(projectId), new FolderId(folderId));
    }

    @PatchMapping("/{folderId}/parent")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void moveFolder(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("folderId") UUID folderId,
            @RequestBody @Valid MoveFolderRequest request
    ) {
        moveFolderUseCase.execute(
                new ProjectId(projectId),
                new FolderId(folderId),
                request.toFolderId()
        );
    }
}
