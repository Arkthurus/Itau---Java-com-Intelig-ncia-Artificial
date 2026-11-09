package testflow.TestFlow.application.folder.usecases;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import testflow.TestFlow.application.folder.output.FolderOutput;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.folder.FolderNotFoundException;
import testflow.TestFlow.domain.folder.FolderRepository;
import testflow.TestFlow.infrastructure.event.folder.FolderViewedEvent;

@Service
public class GetFolderByIdUseCase {
    private final FolderRepository folderRepository;
    private final ApplicationEventPublisher eventPublisher;

    public GetFolderByIdUseCase(FolderRepository folderRepository, ApplicationEventPublisher eventPublisher) {
        this.folderRepository = folderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Cacheable(value = "folders", key = "#id.id())")
    public FolderOutput execute(FolderId id){
        long start = System.nanoTime();
        FolderOutput output = folderRepository.findById(id)
                .map(FolderOutput::from)
                .orElseThrow(() -> new FolderNotFoundException(id));

        long duration = (System.nanoTime() - start) / 1_000_000;

        eventPublisher.publishEvent(new FolderViewedEvent(id, duration));
        return output;
    }
}
