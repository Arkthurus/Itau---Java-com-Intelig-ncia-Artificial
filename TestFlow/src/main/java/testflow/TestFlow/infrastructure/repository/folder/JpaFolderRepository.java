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
        //Verifica se ja existe uma pasta com o mesmo ID
        if (folderEntityRepository.existsById(folder.getId().id())) {
            throw new FolderAlreadyExistsExcepiton(folder.getId(), folder.getName());
        }

        //Converte a pasta de dominio para entidade e salva no banco
        var entity = mapperToEntity(folder);
        folderEntityRepository.save(entity);
        return folder;
    }

    @Override
    public Optional<Folder> findById(FolderId id) {
        //Busca a entidade pelo ID e converte o resultado para dominio
        return folderEntityRepository.findById(id.id())
                .map(JpaFolderRepository::mapperToDomain);
    }

    @Override
    public List<Folder> findAll() {
        //Busca todas as entidades e converte cada uma para dominio
        var interable = folderEntityRepository.findAll();
        return StreamSupport.stream(interable.spliterator(), false)
                .map(JpaFolderRepository::mapperToDomain).toList();
    }

    @Override
    public void delete(FolderId id) {
        //Remove a pasta do banco pelo ID
        folderEntityRepository.deleteById(id.id());
    }

    @Override
    public boolean updateName(FolderId id, String name) {
        //Atualiza o nome e informa se algum registro foi alterado
        return folderEntityRepository.updateNameById(id.id(), name) > 0;
    }

    @Override
    public boolean updateParentFolder(FolderId id, FolderId parentFolderId) {
        //Atualiza a pasta pai; parentFolderId nulo indica que a pasta sera raiz
        return folderEntityRepository.updateParentFolderById(
                id.id(),
                parentFolderId != null ? parentFolderId.id() : null
        ) > 0;
    }

    @Override
    public List<Folder> findRootFoldersByProjectId(ProjectId projectId) {
        //Busca as pastas do projeto que nao possuem pasta pai(pastas na root)
        return folderEntityRepository.findByProjectIdAndParentFolderIdIsNull(projectId.id())
                .stream()
                .map(JpaFolderRepository::mapperToDomain)
                .toList();
    }

    @Override
    public List<Folder> findByParentFolderId(FolderId parentFolderId) {
        //Busca as pastas filhas da pasta informada e converte para dominio
        return folderEntityRepository.findByParentFolderId(parentFolderId.id())
                .stream()
                .map(JpaFolderRepository::mapperToDomain)
                .toList();
    }

    //Transforma de classe DOMAIN pra entity BANCO
    private static FolderEntity mapperToEntity(Folder folder) {

        var entity = new FolderEntity();

        //Copia os IDs e dados da pasta para a entidade persistida
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
        //Converte o ID da pasta pai quando existir; pastas raiz nao possuem pai
        var pastaPaiId = entity.getParentFolderId()  != null ? new FolderId(entity.getParentFolderId()) : null;
        return new Folder(new FolderId(entity.getId()), entity.getName(), new ProjectId(entity.getProjectId()), pastaPaiId);
    }
}
