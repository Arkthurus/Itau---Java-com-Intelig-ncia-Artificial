package testflow.TestFlow.application.folder.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import testflow.TestFlow.domain.folder.Folder;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.folder.FolderMoveWouldCreateCycleException;
import testflow.TestFlow.domain.folder.FolderProjectMismatchException;
import testflow.TestFlow.domain.folder.FolderRepository;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.infrastructure.event.folder.FolderMovedEvent;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MoveFolderUseCaseTest {
    private FolderRepository folderRepository;
    private ApplicationEventPublisher eventPublisher;
    private MoveFolderUseCase useCase;

    @BeforeEach
    void setUp() {
        folderRepository = mock(FolderRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        useCase = new MoveFolderUseCase(folderRepository, eventPublisher);
    }

    @Test
    void movesFolderToParentInSameProject() {
        ProjectId projectId = new ProjectId(UUID.randomUUID());
        FolderId folderId = new FolderId();
        FolderId oldParentId = new FolderId();
        FolderId newParentId = new FolderId();
        Folder folder = new Folder(folderId, "Subpasta", projectId, oldParentId);
        Folder newParent = new Folder(newParentId, "Destino", projectId, null);

        when(folderRepository.findById(folderId)).thenReturn(Optional.of(folder));
        when(folderRepository.findById(newParentId)).thenReturn(Optional.of(newParent));
        when(folderRepository.updateParentFolder(folderId, newParentId)).thenReturn(true);

        var output = useCase.execute(folderId, newParentId);

        assertEquals(newParentId.id().toString(), output.parentFolderId());
        verify(folderRepository).updateParentFolder(folderId, newParentId);
        verify(eventPublisher).publishEvent(org.mockito.ArgumentMatchers.any(FolderMovedEvent.class));
    }

    @Test
    void rejectsMovingFolderToAnotherProject() {
        FolderId folderId = new FolderId();
        FolderId newParentId = new FolderId();
        Folder folder = new Folder(folderId, "Subpasta", new ProjectId(), null);
        Folder newParent = new Folder(newParentId, "Destino", new ProjectId(), null);

        when(folderRepository.findById(folderId)).thenReturn(Optional.of(folder));
        when(folderRepository.findById(newParentId)).thenReturn(Optional.of(newParent));

        assertThrows(FolderProjectMismatchException.class,
                () -> useCase.execute(folderId, newParentId));
    }

    @Test
    void rejectsMovingFolderIntoItsDescendant() {
        ProjectId projectId = new ProjectId();
        FolderId folderId = new FolderId();
        FolderId childId = new FolderId();
        Folder folder = new Folder(folderId, "Pasta", projectId, null);
        Folder child = new Folder(childId, "Subpasta", projectId, folderId);

        when(folderRepository.findById(folderId)).thenReturn(Optional.of(folder));
        when(folderRepository.findById(childId)).thenReturn(Optional.of(child));

        assertThrows(FolderMoveWouldCreateCycleException.class,
                () -> useCase.execute(folderId, childId));
    }
}
