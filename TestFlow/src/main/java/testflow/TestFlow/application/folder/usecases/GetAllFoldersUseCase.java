package testflow.TestFlow.application.folder.usecases;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import testflow.TestFlow.application.folder.output.FolderOutput;
import testflow.TestFlow.domain.folder.Folder;
import testflow.TestFlow.domain.folder.FolderRepository;
import testflow.TestFlow.infrastructure.event.folder.FoldersListedEvent;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class GetAllFoldersUseCase {
    private final FolderRepository folderRepository;
    private final ApplicationEventPublisher eventPublisher;

    public GetAllFoldersUseCase(FolderRepository folderRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.folderRepository = folderRepository;
        this.eventPublisher = applicationEventPublisher;
    }

    public List<FolderOutput> execute(){
        long start = System.nanoTime();

        List<FolderOutput> folders = folderRepository.findAll()
                .stream()
                .map(FolderOutput::from)
                .toList();

        long duration = (System.nanoTime() - start) /  1_000_000;
        eventPublisher.publishEvent(new FoldersListedEvent( folders.size(), duration));

        return folders;
    }
}
