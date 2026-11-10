package testflow.TestFlow.application.folder.output;

import jakarta.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

public record FolderTreeOutput(
        String id,
        String name,
        String projectId,
        @Nullable String parentFolderId,
        List<FolderTreeOutput> children
) implements Serializable {
}
