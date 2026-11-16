package testflow.TestFlow.infrastructure.event.folder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.event.EventListener;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import testflow.TestFlow.application.folder.output.FolderOutput;

import java.util.ArrayList;
import java.util.List;

@Component
@RepositoryEventHandler
@EnableAsync
public class FolderEventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(FolderEventHandler.class);
    private static final String FOLDERS_CACHE = "folders";

    private final CacheManager cacheManager;

    public FolderEventHandler(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Atualiza o detalhe e a lista da pasta depois que a transação de alteração do nome foi confirmada.
     * As chaves que ainda não existem continuam ausentes; a atualização não carrega dados do banco.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateCachedFolderAfterUpdate(FolderUpdatedEvent event) {
        // Cria a resposta que as consultas retornariam, usando os valores atualizados do evento.
        FolderOutput folder = new FolderOutput(
                // Converte o UUID da pasta para String, formato usado pelo DTO.
                event.folderId().id().toString(),
                // Usa o nome novo confirmado no banco.
                event.newFolderName(),
                // Converte o projeto para String para compor as chaves do cache.
                event.projectId().id().toString(),
                // Mantém null para pasta raiz; caso contrário, usa o ID do pai atual.
                event.parentFolderId() != null ? event.parentFolderId().id().toString() : null
        );

        // Obtém do CacheManager a região que armazena os resultados das consultas de folders.
        Cache cache = foldersCache();
        // Se a região não estiver configurada, não há entrada que possa ser atualizada.
        if (cache == null) {
            return;
        }

        // Atualiza o detalhe individual somente se uma consulta anterior já o armazenou.
        putIfCached(cache, folderDetailKey(folder), folder);
        // Atualiza a lista de raízes ou a lista de filhos do pai atual, se já estiver no cache.
        upsertListEntryIfCached(cache, folderListKey(folder.projectId(), folder.parentFolderId()), folder);
    }

    /**
     * Ajusta as entradas de cache depois que a movimentação foi confirmada.
     * Remove a pasta da lista do pai antigo e a inclui na lista do pai novo.
     * Quando um pai é null, a lista correspondente é a de pastas raiz do projeto.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateCachedFolderAfterMove(FolderMovedEvent event) {
        // Monta o DTO com o novo local da pasta para atualizar as consultas cacheadas.
        FolderOutput folder = new FolderOutput(
                // O ID permanece o mesmo porque a pasta foi movida, não recriada.
                event.folderId().id().toString(),
                // A movimentação não altera o nome da pasta.
                event.folderName(),
                // O projeto identifica o espaço de cache ao qual a pasta pertence.
                event.projectId().id().toString(),
                // Usa o novo pai; null representa uma movimentação para a raiz.
                event.newParentFolderId() != null ? event.newParentFolderId().id().toString() : null
        );

        // Recupera o cache usado pelas consultas de detalhe e de listas.
        Cache cache = foldersCache();
        // Sem a região de cache, encerra o listener sem tentar atualizações.
        if (cache == null) {
            return;
        }

        // Atualiza o detalhe individual com o novo parentFolderId, se essa chave já existir.
        putIfCached(cache, folderDetailKey(folder), folder);

        // Calcula a chave da lista antiga; pai null seleciona a lista de raízes do projeto.
        removeListEntryIfCached(
                cache,
                folderListKey(folder.projectId(), event.oldParentFolderId() != null
                        ? event.oldParentFolderId().id().toString()
                        : null),
                // Passa o ID para retirar somente a pasta movida e preservar os irmãos.
                folder.id()
        );

        // Atualiza a lista de destino; pai null seleciona a lista de raízes.
        upsertListEntryIfCached(cache, folderListKey(folder.projectId(), folder.parentFolderId()), folder);
    }

    /**
     * A exclusão pode remover descendentes em cascata. Por isso, uma chave individual
     * não é suficiente: todas as entradas de folders são limpas depois do commit.
     */
    @CacheEvict(cacheNames = "folders", allEntries = true)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void evictFoldersAfterDelete(FolderDeletedEvent event) {
        // O corpo fica vazio de propósito: o interceptor do Spring executa o CacheEvict.
    }

    // Estes listeners registram logs após o commit, sem fazer parte da atualização do cache.
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterCreate(FolderCreatedEvent event) {
        LOG.warn("Pasta {} com id : ( {} ) criado em {}ms", event.folderName(), event.folderId(), event.duration());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterUpdate(FolderUpdatedEvent event) {
        LOG.warn("Pasta {} atualizado de {} para {} em {}ms", event.folderId(), event.oldFolderName(), event.newFolderName(), event.duration());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterMove(FolderMovedEvent event) {
        LOG.info("Pasta {} movida de {} para {} em {}ms",
                event.folderId(), event.oldParentFolderId(), event.newParentFolderId(), event.durationMs());
    }

    // Registra a consulta individual da pasta após o commit da transação correspondente.
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFolderViewed(FolderViewedEvent event) {
        LOG.info("Projeto {} visualizado em {}ms", event.folderId(), event.duration());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterDelete(FolderDeletedEvent event) {
        LOG.info("Projeto {} deletado em {}ms", event.folderId(), event.duration());
    }

    // Estes eventos de listagem não dependem de transação; apenas registram métricas no log.
    @EventListener
    public void handleFoldersListed(FoldersListedEvent event) {
        LOG.info("Listada(s) {} pasta(s) em {}ms", event.count(), event.duration());
    }

    @EventListener
    public void handleFolderTreeListed(FolderTreeListedEvent event) {
        LOG.info(
                "Arvore do projeto {} listada com {} pasta(s) em {}ms",
                event.projectId(),
                event.count(),
                event.durationMs()
        );
    }

    /** Obtém do CacheManager a região de cache chamada folders. */
    private Cache foldersCache() {
        // Usa o nome centralizado para evitar divergência entre as operações do handler.
        return cacheManager.getCache(FOLDERS_CACHE);
    }

    /** Substitui um detalhe somente se a chave já tiver sido criada por uma consulta. */
    private void putIfCached(Cache cache, String key, Object value) {
        // Cache.get retorna um ValueWrapper para chave existente e null para chave ausente.
        if (cache.get(key) != null) {
            // Como já havia um valor, substitui-o pelo dado atualizado do evento.
            cache.put(key, value);
        }
    }

    /**
     * Atualiza ou inclui uma pasta em uma lista que já esteja cacheada.
     * A lista original não é alterada; uma cópia nova é gravada ao final.
     */
    private void upsertListEntryIfCached(Cache cache, String key, FolderOutput folder) {
        // Busca o valor associado à chave calculada para raízes ou filhos de um pai.
        Cache.ValueWrapper cached = cache.get(key);
        // Sai se a chave não existe ou se o valor armazenado não é uma lista.
        // O && faz com que cached.get() só seja avaliado quando cached não é null.
        if (cached == null || !(cached.get() instanceof List<?> values)) {
            return;
        }

        // Cria uma lista mutável para copiar os valores sem modificar a lista original.
        List<FolderOutput> updated = new ArrayList<>(values.size() + 1);
        // Indica se a pasta foi encontrada e substituída durante o loop.
        boolean replaced = false;
        // Percorre todos os itens para preservar os demais folders e sua ordem.
        for (Object value : values) {
            // Confere se o item atual tem o tipo esperado antes de convertê-lo.
            if (!(value instanceof FolderOutput cachedFolder)) {
                // Se algum item tiver outro tipo, não grava uma lista parcialmente reconstruída.
                return;
            }

            // Compara pelo ID estável da pasta, pois o nome pode ter sido alterado.
            if (cachedFolder.id().equals(folder.id())) {
                // Evita adicionar a pasta mais de uma vez se a lista já contiver IDs duplicados.
                if (!replaced) {
                    // Coloca a nova versão na posição ocupada pela versão antiga.
                    updated.add(folder);
                    // Registra que a versão nova já foi adicionada.
                    replaced = true;
                }
            } else {
                // Copia sem alterações todos os folders que não correspondem ao ID atualizado.
                updated.add(cachedFolder);
            }
        }

        // Se o ID não apareceu, adiciona a pasta, como ocorre ao chegar à lista de um novo pai.
        if (!replaced) {
            updated.add(folder);
        }

        // Troca o valor da chave pela lista nova, mantida imutável como a resposta de leitura.
        cache.put(key, List.copyOf(updated));
    }

    /** Remove uma pasta de uma lista cacheada sem alterar os demais itens. */
    private void removeListEntryIfCached(Cache cache, String key, String folderId) {
        // Busca a lista antiga; não consulta o banco para reconstruir uma chave ausente.
        Cache.ValueWrapper cached = cache.get(key);
        // Sai se a lista não está cacheada ou se o valor encontrado não é uma lista.
        // Por causa do &&, cached.get() só é chamado quando cached não é null.
        if (cached == null || !(cached.get() instanceof List<?> values)) {
            return;
        }

        // Prepara uma lista nova que receberá somente os itens que permanecerão.
        List<FolderOutput> updated = new ArrayList<>(values.size());
        // Controla se o ID procurado foi encontrado na lista antiga.
        boolean removed = false;
        // Percorre a lista para preservar a ordem dos demais folders.
        for (Object value : values) {
            // Garante que o elemento possa ser tratado como FolderOutput.
            if (!(value instanceof FolderOutput cachedFolder)) {
                // Se o formato for inesperado, preserva a entrada antiga sem alterações.
                return;
            }

            // Compara pelo ID para localizar exatamente a pasta que mudou de pai.
            if (cachedFolder.id().equals(folderId)) {
                // Não copia esse item para a nova lista e registra que houve remoção.
                removed = true;
            } else {
                // Mantém irmãos e demais pastas que continuam sob o pai antigo.
                updated.add(cachedFolder);
            }
        }

        // Só substitui a entrada se o item estava presente; sem remoção, evita escrita desnecessária.
        if (removed) {
            // Grava a cópia sem a pasta movida e mantém o restante da lista.
            cache.put(key, List.copyOf(updated));
        }
    }

    /** Monta a chave do detalhe no formato <projectId>:<folderId>. */
    private String folderDetailKey(FolderOutput folder) {
        // Combina projeto e pasta para separar dados mesmo se IDs forem iguais entre projetos.
        return folder.projectId() + ":" + folder.id();
    }

    /**
     * Monta a chave de uma lista: roots:<projectId> para raízes ou
     * children:<projectId>:<parentId> para filhos.
     */
    private String folderListKey(String projectId, String parentFolderId) {
        // Usa uma chave diferente para a lista de raízes, que não possui ID de pai.
        return parentFolderId == null
                // Sem pai, seleciona todas as pastas raiz deste projeto.
                ? "roots:" + projectId
                // Com pai, seleciona somente as pastas filhas daquele pai no projeto.
                : "children:" + projectId + ":" + parentFolderId;
    }
}
