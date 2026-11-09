package testflow.TestFlow.infrastructure.event.folder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.event.EventListener;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;


@Component
@RepositoryEventHandler
@EnableAsync
public class FolderEventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(FolderEventHandler.class);

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterCreate(FolderCreatedEvent event) {
        LOG.warn("Pasta {} com id : ( {} ) criado em {}ms", event.folderName(), event.folderId(), event.duration());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterUpdate(FolderUpdatedEvent event) {
        LOG.warn("Nome Projeto {} atualizado de {} para {} em {}ms", event.folderId(), event.oldFolderName(), event.newFolderName(), event.duration());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterMove(FolderMovedEvent event) {
        LOG.info("Pasta {} movida de {} para {} em {}ms",
                event.folderId(), event.oldParentFolderId(), event.newParentFolderId(), event.durationMs());
    }

    @CacheEvict(cacheNames = "folders", key = "#event.folderId().id()")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void evictMovedFolder(FolderMovedEvent event) {
    }


    //Get{id}
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFolderViewed(FolderViewedEvent event) {
        LOG.info("Projeto {} visualizado em {}ms", event.folderId(), event.duration());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterDelete(FolderDeletedEvent event) {
        LOG.info("Projeto {} deletado em {}ms", event.folderId(), event.duration());
    }

    //Cache do Update
    @CacheEvict(cacheNames = "folders", key = "#event.folderId().id()")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void evictUpdatedFolder(FolderUpdatedEvent event) {
    }

    //GetAll
    @EventListener
    public void handleFoldersListed(FoldersListedEvent event) {
        LOG.info("Listada(s) {} pasta(s) em {}ms", event.count(), event.duration());
    }

    @EventListener
    public void handleFolderTreeListed(FolderTreeListedEvent event) {
        LOG.info(
                "Arvore do projeto {} listada com {} pasta(s) em {}ms",
                event.projectId(),
                event.count(),
                event.durationMs()
        );
    }
}
