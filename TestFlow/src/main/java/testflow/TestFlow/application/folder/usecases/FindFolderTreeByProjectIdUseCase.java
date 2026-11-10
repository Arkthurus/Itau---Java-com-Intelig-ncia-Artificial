package testflow.TestFlow.application.folder.usecases;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import testflow.TestFlow.application.folder.output.FolderTreeOutput;
import testflow.TestFlow.domain.folder.Folder;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.folder.FolderRepository;
import testflow.TestFlow.domain.project.ProjectId;
import testflow.TestFlow.domain.project.ProjectNotFoundException;
import testflow.TestFlow.domain.project.ProjectRepository;
import testflow.TestFlow.infrastructure.event.folder.FolderTreeListedEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class FindFolderTreeByProjectIdUseCase {
    private final FolderRepository folderRepository;
    private final ProjectRepository projectRepository;
    private final ApplicationEventPublisher eventPublisher;

    public FindFolderTreeByProjectIdUseCase(
            FolderRepository folderRepository,
            ProjectRepository projectRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.folderRepository = folderRepository;
        this.projectRepository = projectRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<FolderTreeOutput> execute(ProjectId projectId) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        //Inicia a medicao antes das consultas para incluir a busca e a montagem da árvore.
        long start = System.nanoTime();

        //Usa cada pasta raiz como ponto de partida para montar uma árvore independente.
        //Retorna SOMENTE todas as pastas na RAIZ
        List<FolderTreeOutput> folderTrees = folderRepository.findRootFoldersByProjectId(projectId)
                .stream()
                //Para cada Pasta raiz, percorre toda a sua hierarquia adicionando as pastas filhas a um HashSet
                .map(folder -> buildTree(folder, new HashSet<>()))
                .toList();

        //Calcula o tempo total e publica a quantidade de pastas da árvore para registrar a consulta.
        long durationMs = (System.nanoTime() - start) / 1_000_000;
        eventPublisher.publishEvent(new FolderTreeListedEvent(
                projectId,
                countFolders(folderTrees),
                durationMs
        ));

        return folderTrees;
    }

    private int countFolders(List<FolderTreeOutput> folders) {
        // Esta lista pode conter pastas raiz ou os filhos de uma pasta.
        // Para cada pasta da lista, contamos:
        // 1) a própria pasta (o "1" abaixo);
        // 2) todas as pastas que existem dentro de children, chamando este método novamente.
        // A chamada recursiva percorre filhos, netos e todos os níveis seguintes.
        return folders.stream()
                // Calcula a quantidade de pastas de cada ramo: a pasta atual + seus descendentes.
                // mapToInt transforma cada resultado em int para que sum() some os ramos.
                .mapToInt(folder -> 1 + countFolders(folder.children()))
                // Soma as quantidades de todos os ramos da lista.
                // Se a lista estiver vazia, sum() retorna 0; esse é o caso base da recursão.
                .sum();
    }

    private FolderTreeOutput buildTree(Folder folder, Set<FolderId> ancestors) {
        //Tenta adicionar o ID do FolderRaiz informado a lista de ancestors; se já estiver presente, encontrou um ciclo na hierarquia.
        if (!ancestors.add(folder.getId())) {
            throw new IllegalStateException("Ciclo encontrado na hierarquia de pastas: " + folder.getId());
        }

        try {
            //Busca os filhos diretos da pasta atual.
            List<FolderTreeOutput> children = folderRepository.findByParentFolderId(folder.getId())
                    .stream()
                    // Monta recursivamente a subárvore de cada filha.
                    // Quando uma pasta não tem filhas, o seu FolderTreeOutput terá children vazio.
                    // Esse resultado retorna à chamada anterior, que o inclui na sua própria lista children.
                    // As chamadas anteriores continuam montando a árvore até chegar à raiz.
                    .map(child -> buildTree(child, ancestors))
                    .toList();

            //Cria o resultado desta pasta com os filhos já organizados nos niveis abaixo.
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
            //Remove o ID ao sair deste ramo para que uma pasta irma não seja confundida com um ciclo.
            ancestors.remove(folder.getId());
        }
    }
}
