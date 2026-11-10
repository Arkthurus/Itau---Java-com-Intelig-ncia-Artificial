package testflow.TestFlow.infrastructure.repository.folder.http.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import testflow.TestFlow.application.folder.output.FolderTreeOutput;

import java.util.List;

@JsonInclude(JsonInclude.Include.ALWAYS)
public record FolderTreeResponse(
        String id,
        String name,
        String projectId,
        String parentFolderId,
        List<FolderTreeResponse> children
) {
    public static FolderTreeResponse from(FolderTreeOutput output){
        return new FolderTreeResponse(
                output.id(),
                output.name(),
                output.projectId(),
                output.parentFolderId(),
                output.children()
                        .stream()
                        .map(FolderTreeResponse::from)
                        .toList()
        );
    }
}
