package testflow.TestFlow.application.folder.usecases;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import testflow.TestFlow.application.folder.input.UpdateFolderInput;
import testflow.TestFlow.application.folder.output.FolderOutput;
import testflow.TestFlow.domain.folder.Folder;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.folder.FolderNotFoundException;
import testflow.TestFlow.domain.folder.FolderRepository;
import testflow.TestFlow.infrastructure.event.folder.FolderUpdatedEvent;

@Service
public class UpdateFolderUseCase {
    private final FolderRepository folderRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UpdateFolderUseCase(FolderRepository folderRepository, ApplicationEventPublisher eventPublisher) {
        this.folderRepository = folderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
//    @CacheEvict(value = "folders", key = "#id.id()", beforeInvocation = false)
    public FolderOutput execute (FolderId id, UpdateFolderInput input){
        long start = System.nanoTime();

        Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new FolderNotFoundException(id));

        String oldName = folder.getName();

        var parentFolderId = folder.getParentFolderId() != null
                                  ? folder.getParentFolderId().id().toString()
                                  : null;

        if (!folderRepository.updateName(id, input.name())) {
            throw new FolderNotFoundException(id);
        }

        long duration = (System.nanoTime() - start) / 1_000_000;

        eventPublisher.publishEvent(
                new FolderUpdatedEvent(id, duration, input.name(), oldName)
        );

        return new FolderOutput(id.id().toString(), input.name(), folder.getProjectId().id().toString(), parentFolderId);
    }
}
