package testflow.TestFlow.domain.folder;

import java.util.List;
import java.util.Optional;

public interface FolderRepository {
    Folder save(Folder folder);
    Optional<Folder> findById(FolderId id);
    List<Folder> findAll();
    void delete(FolderId id);
    boolean updateName(FolderId id, String name);
}
