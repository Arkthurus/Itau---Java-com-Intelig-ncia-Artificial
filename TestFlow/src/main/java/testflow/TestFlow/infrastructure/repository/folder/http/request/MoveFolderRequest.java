package testflow.TestFlow.infrastructure.repository.folder.http.request;

import jakarta.annotation.Nullable;
import testflow.TestFlow.domain.folder.FolderId;

import java.util.UUID;

public record MoveFolderRequest(@Nullable UUID parentFolderId) {
    public FolderId toFolderId() {
        return parentFolderId != null ? new FolderId(parentFolderId) : null;
    }
}
