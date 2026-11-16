package testflow.TestFlow.infrastructure.event.folder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleValueWrapper;
import testflow.TestFlow.application.folder.output.FolderOutput;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.project.ProjectId;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;

class FolderEventHandlerCacheTest {
    private Cache cache;
    private FolderEventHandler handler;

    @BeforeEach
    void setUp() {
        CacheManager cacheManager = mock(CacheManager.class);
        cache = mock(Cache.class);
        when(cacheManager.getCache("folders")).thenReturn(cache);
        handler = new FolderEventHandler(cacheManager);
    }

    @Test
    void updatesExistingDetailAndParentListAfterFolderUpdate() {
        FolderId folderId = new FolderId();
        ProjectId projectId = new ProjectId();
        FolderId parentId = new FolderId();
        String folderIdValue = folderId.id().toString();
        String projectIdValue = projectId.id().toString();
        String parentIdValue = parentId.id().toString();
        FolderOutput oldFolder = new FolderOutput(folderIdValue, "Old name", projectIdValue, parentIdValue);
        FolderOutput updatedFolder = new FolderOutput(folderIdValue, "New name", projectIdValue, parentIdValue);
        String detailKey = projectIdValue + ":" + folderIdValue;
        String parentListKey = "children:" + projectIdValue + ":" + parentIdValue;

        when(cache.get(detailKey)).thenReturn(new SimpleValueWrapper(oldFolder));
        when(cache.get(parentListKey)).thenReturn(new SimpleValueWrapper(List.of(oldFolder)));

        handler.updateCachedFolderAfterUpdate(new FolderUpdatedEvent(
                folderId,
                projectId,
                parentId,
                5,
                updatedFolder.name(),
                "Old name"
        ));

        verify(cache).put(detailKey, updatedFolder);
        ArgumentCaptor<Object> listCaptor = ArgumentCaptor.forClass(Object.class);
        verify(cache).put(eq(parentListKey), listCaptor.capture());
        assertEquals(List.of(updatedFolder), listCaptor.getValue());
    }

    @Test
    void movesFolderBetweenExistingParentListsAndUpdatesItsDetail() {
        FolderId folderId = new FolderId();
        ProjectId projectId = new ProjectId();
        FolderId oldParentId = new FolderId();
        FolderId newParentId = new FolderId();
        FolderOutput movedFolder = new FolderOutput(
                folderId.id().toString(), "Folder", projectId.id().toString(), newParentId.id().toString());
        FolderOutput oldSibling = new FolderOutput(
                new FolderId().id().toString(), "Old sibling", projectId.id().toString(), oldParentId.id().toString());
        FolderOutput newSibling = new FolderOutput(
                new FolderId().id().toString(), "New sibling", projectId.id().toString(), newParentId.id().toString());
        String detailKey = projectId.id() + ":" + folderId.id();
        String oldListKey = "children:" + projectId.id() + ":" + oldParentId.id();
        String newListKey = "children:" + projectId.id() + ":" + newParentId.id();

        when(cache.get(detailKey)).thenReturn(new SimpleValueWrapper(
                new FolderOutput(folderId.id().toString(), "Folder", projectId.id().toString(), oldParentId.id().toString())
        ));
        when(cache.get(oldListKey)).thenReturn(new SimpleValueWrapper(List.of(movedFolder, oldSibling)));
        when(cache.get(newListKey)).thenReturn(new SimpleValueWrapper(List.of(newSibling)));

        handler.updateCachedFolderAfterMove(new FolderMovedEvent(
                folderId,
                projectId,
                movedFolder.name(),
                oldParentId,
                newParentId,
                7
        ));

        verify(cache).put(detailKey, movedFolder);
        ArgumentCaptor<Object> oldListCaptor = ArgumentCaptor.forClass(Object.class);
        verify(cache).put(eq(oldListKey), oldListCaptor.capture());
        assertEquals(List.of(oldSibling), oldListCaptor.getValue());
        ArgumentCaptor<Object> newListCaptor = ArgumentCaptor.forClass(Object.class);
        verify(cache).put(eq(newListKey), newListCaptor.capture());
        assertEquals(List.of(newSibling, movedFolder), newListCaptor.getValue());
    }
}
