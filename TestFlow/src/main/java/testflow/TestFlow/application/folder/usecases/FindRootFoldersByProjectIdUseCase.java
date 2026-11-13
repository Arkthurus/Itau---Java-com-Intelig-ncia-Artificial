package testflow.TestFlow.application.folder.usecases;

import org.springframework.stereotype.Service;
import testflow.TestFlow.application.folder.output.FolderOutput;
import testflow.TestFlow.domain.folder.FolderRepository;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.domain.project.ProjectNotFoundException;
import testflow.TestFlow.domain.project.ProjectRepository;

import java.util.List;

@Service
public class FindRootFoldersByProjectIdUseCase {
    private final FolderRepository folderRepository;
    private final ProjectRepository projectRepository;

    public FindRootFoldersByProjectIdUseCase(
            FolderRepository folderRepository,
            ProjectRepository projectRepository
    ) {
        this.folderRepository = folderRepository;
        this.projectRepository = projectRepository;
    }

    public List<FolderOutput> execute(ProjectId projectId) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        //Busca as pastas raiz do Projeto informado
        return folderRepository.findRootFoldersByProjectId(projectId)
                .stream()
                //Converte cada pasta para FolderOutput
                .map(FolderOutput::from)
                .toList();
    }
}
