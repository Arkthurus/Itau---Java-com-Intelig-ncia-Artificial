package testflow.TestFlow.application.folder.usecases;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import testflow.TestFlow.application.folder.input.CreateFolderInput;
import testflow.TestFlow.application.folder.output.FolderOutput;
import testflow.TestFlow.domain.folder.Folder;
import testflow.TestFlow.domain.folder.FolderProjectMismatchException;
import testflow.TestFlow.domain.folder.FolderRepository;
import testflow.TestFlow.domain.folder.ParentFolderNotFoundException;
import testflow.TestFlow.domain.project.Project;
import testflow.TestFlow.domain.project.ProjectNotFoundException;
import testflow.TestFlow.domain.project.ProjectRepository;
import testflow.TestFlow.infrastructure.event.folder.FolderCreatedEvent;

@Service
public class CreateFolderUseCase {
    private final FolderRepository folderRepository;
    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CreateFolderUseCase(FolderRepository folderRepository, ProjectRepository projectRepository, ApplicationEventPublisher eventPublisher) {
        this.folderRepository = folderRepository;
        this.projectRepository = projectRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public FolderOutput execute(CreateFolderInput input) {
        long start = System.nanoTime();
        Project project = projectRepository.findById(input.projectId())
                    .orElseThrow(() -> new ProjectNotFoundException(input.projectId()));
        if (input.parentFolderId() != null) {
            var parentFolder = folderRepository.findById(input.parentFolderId())
                    .orElseThrow(() -> new ParentFolderNotFoundException(input.parentFolderId()));
            if (!input.projectId().equals(parentFolder.getProjectId())) {
                throw new FolderProjectMismatchException(
                        parentFolder.getName(),
                        parentFolder.getProjectId(),
                        input.name(),
                        input.projectId()
                );
            }
        }
        var folder = new Folder(input.name(), input.projectId(), input.parentFolderId());
        var saved =  folderRepository.save(folder);
        long duration = (System.nanoTime() - start) / 1_000_000;
        eventPublisher.publishEvent(new FolderCreatedEvent(saved.getName(), saved.getId(), duration));
        return FolderOutput.from(saved);
    }
}
