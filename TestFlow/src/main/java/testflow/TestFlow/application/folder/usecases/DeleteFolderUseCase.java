package testflow.TestFlow.application.folder.usecases;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.folder.FolderNotFoundException;
import testflow.TestFlow.domain.folder.FolderRepository;
import testflow.TestFlow.infrastructure.event.folder.FolderDeletedEvent;

@Service
public class DeleteFolderUseCase {
    private final FolderRepository folderRepository;
    private final ApplicationEventPublisher  eventPublisher;

    public DeleteFolderUseCase(FolderRepository folderRepository, ApplicationEventPublisher eventPublisher) {
        this.folderRepository = folderRepository;
        this.eventPublisher = eventPublisher;
    }

    @CacheEvict(value = "folders", key = "#id.id()")
    public void execute(FolderId id){
        long start = System.nanoTime();
        if(folderRepository.findById(id).isEmpty()){
            throw new FolderNotFoundException(id);
        }

        folderRepository.delete(id);

        long duration = (System.nanoTime() - start) / 1_000_000;
        eventPublisher.publishEvent(new FolderDeletedEvent(id, duration));
    }
}
