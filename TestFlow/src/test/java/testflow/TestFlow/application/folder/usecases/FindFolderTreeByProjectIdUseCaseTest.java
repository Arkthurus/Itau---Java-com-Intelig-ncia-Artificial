package testflow.TestFlow.application.folder.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import testflow.TestFlow.domain.folder.Folder;
import testflow.TestFlow.domain.folder.FolderRepository;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.infrastructure.event.folder.FolderTreeListedEvent;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class FindFolderTreeByProjectIdUseCaseTest {
    private FolderRepository folderRepository;
    private ApplicationEventPublisher eventPublisher;
    private FindFolderTreeByProjectIdUseCase useCase;

    @BeforeEach
    void setUp() {
        folderRepository = mock(FolderRepository.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        useCase = new FindFolderTreeByProjectIdUseCase(folderRepository, eventPublisher);
    }

    @Test
    void returnsCompleteFolderTreeForProject() {
        ProjectId projectId = new ProjectId();
        Folder root = new Folder("Raiz", projectId, null);
        Folder child = new Folder("Filha", projectId, root.getId());
        Folder grandchild = new Folder("Neta", projectId, child.getId());
        when(folderRepository.findRootFoldersByProjectId(projectId)).thenReturn(List.of(root));
        when(folderRepository.findByParentFolderId(root.getId())).thenReturn(List.of(child));
        when(folderRepository.findByParentFolderId(child.getId())).thenReturn(List.of(grandchild));
        when(folderRepository.findByParentFolderId(grandchild.getId())).thenReturn(List.of());

        var result = useCase.execute(projectId);

        assertEquals(1, result.size());
        assertEquals(root.getId().id().toString(), result.getFirst().id());
        assertEquals("Raiz", result.getFirst().name());
        assertEquals(projectId.id().toString(), result.getFirst().projectId());
        assertNull(result.getFirst().parentFolderId());
        assertEquals(1, result.getFirst().children().size());
        assertEquals(child.getId().id().toString(), result.getFirst().children().getFirst().id());
        assertEquals(1, result.getFirst().children().getFirst().children().size());
        assertEquals(
                grandchild.getId().id().toString(),
                result.getFirst().children().getFirst().children().getFirst().id()
        );
        assertEquals(
                List.of(),
                result.getFirst().children().getFirst().children().getFirst().children()
        );
        verify(folderRepository).findRootFoldersByProjectId(projectId);
        verify(eventPublisher).publishEvent((Object) argThat((Object event) ->
                event instanceof FolderTreeListedEvent listedEvent
                        && listedEvent.projectId().equals(projectId)
                        && listedEvent.count() == 3
                        && listedEvent.durationMs() >= 0
        ));
    }

    @Test
    void returnsEmptyListWhenProjectHasNoRootFolders() {
        ProjectId projectId = new ProjectId();
        when(folderRepository.findRootFoldersByProjectId(projectId)).thenReturn(List.of());

        assertEquals(List.of(), useCase.execute(projectId));
        verify(eventPublisher).publishEvent((Object) argThat((Object event) ->
                event instanceof FolderTreeListedEvent listedEvent
                        && listedEvent.projectId().equals(projectId)
                        && listedEvent.count() == 0
        ));
    }

    @Test
    void failsWhenFolderHierarchyContainsCycle() {
        ProjectId projectId = new ProjectId();
        Folder root = new Folder("Raiz", projectId, null);
        Folder child = new Folder("Filha", projectId, root.getId());
        when(folderRepository.findRootFoldersByProjectId(projectId)).thenReturn(List.of(root));
        when(folderRepository.findByParentFolderId(root.getId())).thenReturn(List.of(child));
        when(folderRepository.findByParentFolderId(child.getId())).thenReturn(List.of(root));

        assertThrows(IllegalStateException.class, () -> useCase.execute(projectId));
        verifyNoInteractions(eventPublisher);
    }
}
