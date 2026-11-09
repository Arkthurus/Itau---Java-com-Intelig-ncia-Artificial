package testflow.TestFlow.application.folder.usecases;

import org.springframework.stereotype.Service;
import testflow.TestFlow.application.folder.output.FolderOutput;
import testflow.TestFlow.domain.folder.FolderRepository;
import testflow.TestFlow.domain.project.ProjectId;

import java.util.List;

@Service
public class FindRootFoldersByProjectIdUseCase {
    private final FolderRepository folderRepository;

    public FindRootFoldersByProjectIdUseCase(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    public List<FolderOutput> execute(ProjectId projectId) {
        //Busca as pastas raiz do Projeto informado
        return folderRepository.findRootFoldersByProjectId(projectId)
                .stream()
                //Converte cada pasta para FolderOutput
                .map(FolderOutput::from)
                .toList();
    }
}
