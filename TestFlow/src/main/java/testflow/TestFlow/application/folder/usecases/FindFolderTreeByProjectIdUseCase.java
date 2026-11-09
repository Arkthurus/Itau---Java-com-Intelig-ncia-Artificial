package testflow.TestFlow.application.folder.usecases;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import testflow.TestFlow.application.folder.output.FolderTreeOutput;
import testflow.TestFlow.domain.folder.Folder;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.folder.FolderRepository;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.infrastructure.event.folder.FolderTreeListedEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FindFolderTreeByProjectIdUseCase {
    private final FolderRepository folderRepository;
    private final ApplicationEventPublisher eventPublisher;

    public FindFolderTreeByProjectIdUseCase(
            FolderRepository folderRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.folderRepository = folderRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<FolderTreeOutput> execute(ProjectId projectId) {
        //Inicia a medicao antes das consultas para incluir a busca e a montagem da arvore.
        long start = System.nanoTime();

        //Usa cada pasta raiz como ponto de partida para montar uma arvore independente.
        List<FolderTreeOutput> folderTrees = folderRepository.findRootFoldersByProjectId(projectId)
                .stream()
                //Monta recursivamente os descendentes de cada raiz; cada raiz recebe seu proprio caminho de ancestrais.
                .map(folder -> buildTree(folder, new HashSet<>()))
                .toList();

        //Calcula o tempo total e publica a quantidade de pastas da arvore para registrar a consulta.
        long durationMs = (System.nanoTime() - start) / 1_000_000;
        eventPublisher.publishEvent(new FolderTreeListedEvent(
                projectId,
                countFolders(folderTrees),
                durationMs
        ));

        return folderTrees;
    }

    private int countFolders(List<FolderTreeOutput> folders) {
        //Soma cada pasta da lista com a contagem recursiva de seus filhos, cobrindo todos os niveis.
        return folders.stream()
                .mapToInt(folder -> 1 + countFolders(folder.children()))
                .sum();
    }

    private FolderTreeOutput buildTree(Folder folder, Set<FolderId> ancestors) {
        //Adiciona o ID ao caminho atual; se ja estiver presente, encontrou um ciclo na hierarquia.
        if (!ancestors.add(folder.getId())) {
            throw new IllegalStateException("Ciclo encontrado na hierarquia de pastas: " + folder.getId());
        }

        try {
            //Busca os filhos diretos da pasta atual.
            List<FolderTreeOutput> children = folderRepository.findByParentFolderId(folder.getId())
                    .stream()
                    //Monta recursivamente a subarvore de cada filha.
                    .map(child -> buildTree(child, ancestors))
                    .toList();

            //Cria o resultado desta pasta com os filhos ja organizados nos niveis abaixo.
            return new FolderTreeOutput(
                    folder.getId().id().toString(),
                    folder.getName(),
                    folder.getProjectId().id().toString(),
                    folder.getParentFolderId() != null
                            ? folder.getParentFolderId().id().toString()
                            : null,
                    children
            );
        } finally {
            //Remove o ID ao sair deste ramo para que uma pasta irma nao seja confundida com um ciclo.
            ancestors.remove(folder.getId());
        }
    }
}
