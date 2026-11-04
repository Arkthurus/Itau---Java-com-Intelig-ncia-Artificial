package testflow.TestFlow.infrastructure.repository.folder;

import org.springframework.stereotype.Repository;
import testflow.TestFlow.domain.folder.*;
import testflow.TestFlow.domain.project.Project;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.infrastructure.entity.folder.FolderEntity;
import testflow.TestFlow.infrastructure.entity.project.ProjectEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
public class JpaFolderRepository implements FolderRepository {

    private final FolderEntityRepository  folderEntityRepository;

    public JpaFolderRepository(FolderEntityRepository folderEntityRepository) {
        this.folderEntityRepository = folderEntityRepository;
    }

    @Override
    public Folder save(Folder folder) {

        if (folderEntityRepository.existsById(folder.getId().id())) {
            throw new FolderAlreadyExistsExcepiton(folder.getId(), folder.getName());
        }

        var entity = mapperToEntity(folder);

        folderEntityRepository.save(entity);
        return folder;
    }

    @Override
    public Optional<Folder> findById(FolderId id) {
        return folderEntityRepository.findById(id.id())
                .map(JpaFolderRepository::mapperToDomain);
    }

    @Override
    public List<Folder> findAll() {
        var interable = folderEntityRepository.findAll();
        return StreamSupport.stream(interable.spliterator(), false)
                .map(JpaFolderRepository::mapperToDomain).toList();
    }

    @Override
    public void delete(FolderId id) {
        folderEntityRepository.deleteById(id.id());
    }

    @Override
    public boolean updateName(FolderId id, String name) {
        return folderEntityRepository.updateNameById(id.id(), name) > 0;
    }

    //futuramente
    @Override
    public List<Folder> findFolderByProjectId(ProjectId projectId) {
        return List.of();
    }
    //futuramente
    @Override
    public List<Folder> findByParentFolderId(FolderId parentFolderId) {
        return List.of();
    }

    //Transforma de classe DOMAIN pra entity BANCO
    private static FolderEntity mapperToEntity(Folder folder) {

        var entity = new FolderEntity();

        entity.setId(folder.getId().id());
        entity.setName(folder.getName());
        entity.setProjectId(folder.getProjectId().id());
        if (folder.getParentFolderId() != null) {
            entity.setParentFolderId(folder.getParentFolderId().id());
        }

        return entity;
    }
    //Transforma de entity BANCO pra classe DOMAIN
    private static Folder mapperToDomain(FolderEntity entity) {
        var pastaPaiId = entity.getParentFolderId()  != null ? new FolderId(entity.getParentFolderId()) : null;
        return new Folder(new FolderId(entity.getId()), entity.getName(), new ProjectId(entity.getProjectId()), pastaPaiId);
    }
}
