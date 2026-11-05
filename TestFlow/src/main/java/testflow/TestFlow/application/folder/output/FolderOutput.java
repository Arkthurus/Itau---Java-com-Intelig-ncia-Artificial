package testflow.TestFlow.application.folder.output;

import jakarta.annotation.Nullable;
import testflow.TestFlow.domain.folder.Folder;
import testflow.TestFlow.domain.folder.FolderId;

import java.io.Serializable;

public record FolderOutput(String id,
                           String name,
                           String projectId,
                           @Nullable String parentFolderId
) implements Serializable {
    public static FolderOutput from(Folder folder) {
        return new FolderOutput(folder.getId().id().toString(),
                                folder.getName(),
                                folder.getProjectId().id().toString(),
                                folder.getParentFolderId() != null
                                        ? folder.getParentFolderId().id().toString()
                                        : null
        );
    }
}
