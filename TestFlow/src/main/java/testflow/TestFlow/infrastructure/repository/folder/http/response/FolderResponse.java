package testflow.TestFlow.infrastructure.repository.folder.http.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import testflow.TestFlow.application.folder.output.FolderOutput;

@JsonInclude(JsonInclude.Include.ALWAYS)
public record FolderResponse(
        String id,
        String name,
        String projectId,
        String parentFolderId
) {
    public static FolderResponse from(FolderOutput output) {
        return new FolderResponse(
                output.id(),
                output.name(),
                output.projectId(),
                output.parentFolderId()
        );
    }
}
