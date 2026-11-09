package testflow.TestFlow.application.folder.usecases;

import jakarta.annotation.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import testflow.TestFlow.application.folder.output.FolderOutput;
import testflow.TestFlow.domain.folder.Folder;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.folder.FolderMoveWouldCreateCycleException;
import testflow.TestFlow.domain.folder.FolderNotFoundException;
import testflow.TestFlow.domain.folder.FolderProjectMismatchException;
import testflow.TestFlow.domain.folder.FolderRepository;
import testflow.TestFlow.domain.folder.ParentFolderNotFoundException;
import testflow.TestFlow.infrastructure.event.folder.FolderMovedEvent;

import java.util.HashSet;
import java.util.Set;

@Service
public class MoveFolderUseCase {
    private final FolderRepository folderRepository;
    private final ApplicationEventPublisher eventPublisher;

    public MoveFolderUseCase(FolderRepository folderRepository, ApplicationEventPublisher eventPublisher) {
        this.folderRepository = folderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public FolderOutput execute(FolderId folderId, @Nullable FolderId newParentFolderId) {
        long start = System.nanoTime();
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new FolderNotFoundException(folderId));

        //Verifica existencia da pasta alvo
        if (newParentFolderId != null) {
            Folder newParentFolder = folderRepository.findById(newParentFolderId)
                    .orElseThrow(() -> new ParentFolderNotFoundException(newParentFolderId));

            //Verifica se a pasta alvo e a pasta que esta sendo movida são do mesmo Projeto
            if (!folder.getProjectId().equals(newParentFolder.getProjectId())) {
                throw new FolderProjectMismatchException(
                        newParentFolder.getName(),
                        newParentFolder.getProjectId(),
                        folder.getName(),
                        folder.getProjectId()
                );
            }

            ensureParentIsNotDescendant(folderId, newParentFolderId);
        }

        FolderId oldParentFolderId = folder.getParentFolderId();
        if (!folderRepository.updateParentFolder(folderId, newParentFolderId)) {
            throw new FolderNotFoundException(folderId);
        }

        long durationMs = (System.nanoTime() - start) / 1_000_000;
        eventPublisher.publishEvent(new FolderMovedEvent(
                folderId,
                oldParentFolderId,
                newParentFolderId,
                durationMs
        ));

        return new FolderOutput(
                folderId.id().toString(),
                folder.getName(),
                folder.getProjectId().id().toString(),
                newParentFolderId != null ? newParentFolderId.id().toString() : null
        );
    }

    //Verifica se a pasta alvo não é uma subpPasta da Pasta que esta sendo movida
    private void ensureParentIsNotDescendant(FolderId folderId, FolderId newParentFolderId) {
        //Lista de pastas ja lidas
        Set<FolderId> visited = new HashSet<>();
        //Inicia pelo id da pasta Alvo
        FolderId ancestorId = newParentFolderId;

        //Loop enquanto o ancestorId não for nulo
        while (ancestorId != null) {
            // Se o AncestorId é igual ao folderId(se o id da pasta pai [alvo] é o mesmo id da pasta movida)
            // ou
            // Se a adição do acenstorId atual na lista 'visited' falhar, um ciclo foi detectado:
            //  Pasta A → pai B
            //  Pasta B → pai A
            if (ancestorId.equals(folderId) || !visited.add(ancestorId)) {
                throw new FolderMoveWouldCreateCycleException(folderId, newParentFolderId);
            }

            //Se não cair no IF:
            //Atribui o ancestorId como id da pasta Pai atual
            FolderId currentAncestorId = ancestorId;
            //Busca ela e monta o OBJ
            Folder ancestor = folderRepository.findById(currentAncestorId)
                    .orElseThrow(() -> new ParentFolderNotFoundException(currentAncestorId));
            //Define o ancestorId como o parentFolderId do currentAncestorId, assim tornando esse o proximo a ser verificado
            ancestorId = ancestor.getParentFolderId();
        }
    }
}
