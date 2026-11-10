package testflow.TestFlow.application.folder.usecases;

import org.springframework.stereotype.Service;
import testflow.TestFlow.application.folder.output.FolderOutput;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.folder.FolderRepository;
import testflow.TestFlow.domain.folder.FolderNotFoundException;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.domain.project.ProjectNotFoundException;
import testflow.TestFlow.domain.project.ProjectRepository;

import java.util.List;

@Service
public class FindSubFoldersUseCase {
    private final FolderRepository folderRepository;
    private final ProjectRepository projectRepository;

    public FindSubFoldersUseCase(
            FolderRepository folderRepository,
            ProjectRepository projectRepository
    ) {
        this.folderRepository = folderRepository;
        this.projectRepository = projectRepository;
    }

    public List<FolderOutput> execute(ProjectId projectId, FolderId parentFolderId) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        //Busca as subpastas da pasta pai informada
        var parent = folderRepository.findById(parentFolderId)
                .orElseThrow(() -> new FolderNotFoundException(parentFolderId));
        if (!parent.getProjectId().equals(projectId)) {
            throw new FolderNotFoundException(parentFolderId);
        }
        return folderRepository.findByParentFolderId(parentFolderId)
                .stream()
                //Converte cada pasta para FolderOutput
                .map(FolderOutput::from)
                .toList();
    }
}
