package testflow.TestFlow.application.folder.usecases;

import org.springframework.stereotype.Service;
import testflow.TestFlow.application.folder.output.FolderOutput;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.folder.FolderRepository;

import java.util.List;

@Service
public class FindSubFoldersUseCase {
    private final FolderRepository folderRepository;

    public FindSubFoldersUseCase(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    public List<FolderOutput> execute(FolderId parentFolderId) {
        //Busca as subpastas da pasta pai informada
        return folderRepository.findByParentFolderId(parentFolderId)
                .stream()
                //Converte cada pasta para FolderOutput
                .map(FolderOutput::from)
                .toList();
    }
}
