package testflow.TestFlow.application.folder.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testflow.TestFlow.domain.folder.Folder;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.folder.FolderRepository;
import testflow.TestFlow.domain.project.ProjectId;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindSubFoldersUseCaseTest {
    private FolderRepository folderRepository;
    private FindSubFoldersUseCase useCase;

    @BeforeEach
    void setUp() {
        folderRepository = mock(FolderRepository.class);
        useCase = new FindSubFoldersUseCase(folderRepository);
    }

    @Test
    void returnsDirectSubfoldersForParent() {
        ProjectId projectId = new ProjectId();
        FolderId parentFolderId = new FolderId();
        Folder child = new Folder("Filha", projectId, parentFolderId);
        when(folderRepository.findByParentFolderId(parentFolderId))
                .thenReturn(List.of(child));

        var result = useCase.execute(parentFolderId);

        assertEquals(1, result.size());
        assertEquals(child.getId().id().toString(), result.getFirst().id());
        assertEquals("Filha", result.getFirst().name());
        assertEquals(projectId.id().toString(), result.getFirst().projectId());
        assertEquals(parentFolderId.id().toString(), result.getFirst().parentFolderId());
        verify(folderRepository).findByParentFolderId(parentFolderId);
    }

    @Test
    void returnsEmptyListWhenParentHasNoSubfolders() {
        FolderId parentFolderId = new FolderId();
        when(folderRepository.findByParentFolderId(parentFolderId)).thenReturn(List.of());

        assertEquals(List.of(), useCase.execute(parentFolderId));
    }
}
