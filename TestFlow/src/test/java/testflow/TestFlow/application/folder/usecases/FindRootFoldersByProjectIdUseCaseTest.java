package testflow.TestFlow.application.folder.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testflow.TestFlow.domain.folder.Folder;
import testflow.TestFlow.domain.folder.FolderRepository;
import testflow.TestFlow.domain.project.ProjectId;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindRootFoldersByProjectIdUseCaseTest {
    private FolderRepository folderRepository;
    private FindRootFoldersByProjectIdUseCase useCase;

    @BeforeEach
    void setUp() {
        folderRepository = mock(FolderRepository.class);
        useCase = new FindRootFoldersByProjectIdUseCase(folderRepository);
    }

    @Test
    void returnsRootFoldersForProject() {
        ProjectId projectId = new ProjectId();
        Folder rootFolder = new Folder("Raiz", projectId, null);
        when(folderRepository.findRootFoldersByProjectId(projectId))
                .thenReturn(List.of(rootFolder));

        var result = useCase.execute(projectId);

        assertEquals(1, result.size());
        assertEquals(rootFolder.getId().id().toString(), result.getFirst().id());
        assertEquals("Raiz", result.getFirst().name());
        assertEquals(projectId.id().toString(), result.getFirst().projectId());
        assertNull(result.getFirst().parentFolderId());
        verify(folderRepository).findRootFoldersByProjectId(projectId);
    }

    @Test
    void returnsEmptyListWhenProjectHasNoRootFolders() {
        ProjectId projectId = new ProjectId();
        when(folderRepository.findRootFoldersByProjectId(projectId)).thenReturn(List.of());

        assertEquals(List.of(), useCase.execute(projectId));
    }
}
