package testflow.TestFlow.application.folder.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testflow.TestFlow.domain.folder.Folder;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.folder.FolderRepository;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.domain.project.Project;
import testflow.TestFlow.domain.project.ProjectRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FindSubFoldersUseCaseTest {
    private FolderRepository folderRepository;
    private ProjectRepository projectRepository;
    private FindSubFoldersUseCase useCase;

    @BeforeEach
    void setUp() {
        folderRepository = mock(FolderRepository.class);
        projectRepository = mock(ProjectRepository.class);
        useCase = new FindSubFoldersUseCase(folderRepository, projectRepository);
    }

    @Test
    void returnsDirectSubfoldersForParent() {
        ProjectId projectId = new ProjectId();
        FolderId parentFolderId = new FolderId();
        Folder parent = new Folder(parentFolderId, "Pai", projectId, null);
        when(projectRepository.findById(projectId))
                .thenReturn(java.util.Optional.of(new Project(projectId, "Projeto")));
        Folder child = new Folder("Filha", projectId, parentFolderId);
        when(folderRepository.findById(parentFolderId)).thenReturn(java.util.Optional.of(parent));
        when(folderRepository.findByParentFolderId(parentFolderId))
                .thenReturn(List.of(child));

        var result = useCase.execute(projectId, parentFolderId);

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
        ProjectId projectId = new ProjectId();
        when(projectRepository.findById(projectId))
                .thenReturn(java.util.Optional.of(new Project(projectId, "Projeto")));
        Folder parent = new Folder(parentFolderId, "Pai", projectId, null);
        when(folderRepository.findById(parentFolderId)).thenReturn(java.util.Optional.of(parent));
        when(folderRepository.findByParentFolderId(parentFolderId)).thenReturn(List.of());

        assertEquals(List.of(), useCase.execute(projectId, parentFolderId));
    }
}
