package testflow.TestFlow.infrastructure.repository.folder;

import testflow.TestFlow.domain.folder.Folder;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.folder.FolderRepository;

import java.util.List;
import java.util.Optional;

public class JpaFolderRepository implements FolderRepository {
    @Override
    public Folder save(Folder folder) {
        return null;
    }

    @Override
    public Optional<Folder> findById(FolderId id) {
        return Optional.empty();
    }

    @Override
    public List<Folder> findAll() {
        return List.of();
    }

    @Override
    public void delete(FolderId id) {

    }

    @Override
    public boolean updateName(FolderId id, String name) {
        return false;
    }
}
