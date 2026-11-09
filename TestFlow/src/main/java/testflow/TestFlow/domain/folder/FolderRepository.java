package testflow.TestFlow.domain.folder;

import testflow.TestFlow.domain.project.ProjectId;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.Optional;

public interface FolderRepository {
    Folder save(Folder folder);
    Optional<Folder> findById(FolderId id);
    List<Folder> findAll();
    void delete(FolderId id);
    boolean updateName(FolderId id, String name);
    boolean updateParentFolder(FolderId id, @Nullable FolderId parentFolderId);
    List<Folder> findRootFoldersByProjectId(ProjectId projectId);
    List<Folder> findByParentFolderId(FolderId parentFolderId);
}
