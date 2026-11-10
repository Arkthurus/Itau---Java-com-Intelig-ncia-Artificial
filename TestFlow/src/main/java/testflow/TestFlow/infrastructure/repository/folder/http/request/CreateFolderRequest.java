package testflow.TestFlow.infrastructure.repository.folder.http.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import testflow.TestFlow.application.folder.input.CreateFolderInput;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.project.ProjectId;

import java.util.UUID;

public record CreateFolderRequest(
        @NotBlank
        @Size(min = 3, max = 60)
        String name,
        @Nullable
        UUID parentFolderId

) {
    public CreateFolderInput toInput(ProjectId projectId){
        return new CreateFolderInput(
                name,
                projectId,
                parentFolderId != null ? new FolderId(parentFolderId) : null
        );
    }
}
