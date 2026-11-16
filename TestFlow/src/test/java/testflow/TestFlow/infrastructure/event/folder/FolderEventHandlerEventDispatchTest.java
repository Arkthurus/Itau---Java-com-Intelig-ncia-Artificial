package testflow.TestFlow.infrastructure.event.folder;

import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import testflow.TestFlow.application.folder.output.FolderOutput;
import testflow.TestFlow.domain.folder.FolderId;
import testflow.TestFlow.domain.project.ProjectId;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FolderEventHandlerEventDispatchTest {

    @Test
    void publishesEveryFolderEventAndInvokesItsListeners() {
        try (var context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            ApplicationEventPublisher eventPublisher = context;
            CacheManager cacheManager = context.getBean(CacheManager.class);
            var cache = cacheManager.getCache("folders");
            var transaction = new TransactionTemplate(context.getBean(PlatformTransactionManager.class));

            ProjectId projectId = new ProjectId();
            FolderId folderId = new FolderId();
            FolderId oldParentId = new FolderId();
            FolderId newParentId = new FolderId();
            String projectKey = projectId.id().toString();
            String folderKey = folderId.id().toString();
            String oldParentKey = oldParentId.id().toString();
            String newParentKey = newParentId.id().toString();
            String detailKey = projectKey + ":" + folderKey;
            String oldChildrenKey = "children:" + projectKey + ":" + oldParentKey;
            String newChildrenKey = "children:" + projectKey + ":" + newParentKey;

            FolderOutput originalFolder = new FolderOutput(folderKey, "Antes", projectKey, oldParentKey);
            FolderOutput oldSibling = new FolderOutput(new FolderId().id().toString(), "Irma", projectKey, oldParentKey);
            FolderOutput newSibling = new FolderOutput(new FolderId().id().toString(), "Outra", projectKey, newParentKey);
            cache.put(detailKey, originalFolder);
            cache.put(oldChildrenKey, List.of(originalFolder, oldSibling));
            cache.put(newChildrenKey, List.of(newSibling));

            // Dispara criação, visualização e eventos de listagem; os listeners devem recebê-los.
            transaction.executeWithoutResult(status -> {
                eventPublisher.publishEvent(new FolderCreatedEvent("Nova", new FolderId(), 1));
                eventPublisher.publishEvent(new FolderViewedEvent(folderId, 2));
                eventPublisher.publishEvent(new FoldersListedEvent(3, 4));
                eventPublisher.publishEvent(new FolderTreeListedEvent(projectId, 3, 5));
            });

            // O update deve trocar o detalhe e a pasta na lista do pai atual após o commit.
            transaction.executeWithoutResult(status -> eventPublisher.publishEvent(
                    new FolderUpdatedEvent(folderId, projectId, oldParentId, 6, "Atualizada", "Antes")
            ));
            FolderOutput updatedFolder = new FolderOutput(folderKey, "Atualizada", projectKey, oldParentKey);
            assertEquals(updatedFolder, cache.get(detailKey).get());
            assertEquals(List.of(updatedFolder, oldSibling), cache.get(oldChildrenKey).get());

            // O move deve retirar a pasta da lista antiga, incluí-la na nova e atualizar o seu detalhe.
            transaction.executeWithoutResult(status -> eventPublisher.publishEvent(
                    new FolderMovedEvent(folderId, projectId, "Atualizada", oldParentId, newParentId, 7)
            ));
            FolderOutput movedFolder = new FolderOutput(folderKey, "Atualizada", projectKey, newParentKey);
            assertEquals(movedFolder, cache.get(detailKey).get());
            assertEquals(List.of(oldSibling), cache.get(oldChildrenKey).get());
            assertEquals(List.of(newSibling, movedFolder), cache.get(newChildrenKey).get());

            // Delete deve disparar o listener AFTER_COMMIT e remover toda a região de cache.
            transaction.executeWithoutResult(status -> {
                eventPublisher.publishEvent(new FolderDeletedEvent(folderId, 8));
            });
            assertNull(cache.get(detailKey));
            assertNull(cache.get(oldChildrenKey));
            assertNull(cache.get(newChildrenKey));

            System.out.println("FolderEventHandlerEventDispatchTest: todos os eventos foram processados com sucesso.");
        }
    }

    @Configuration(proxyBeanMethods = false)
    @EnableCaching
    @EnableTransactionManagement
    static class TestConfig {
        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("folders");
        }

        @Bean
        FolderEventHandler folderEventHandler(CacheManager cacheManager) {
            return new FolderEventHandler(cacheManager);
        }

        @Bean
        PlatformTransactionManager transactionManager() {
            return new TestTransactionManager();
        }
    }

    /** Transaction manager de teste que executa sincronizações sem acessar um banco de dados. */
    static class TestTransactionManager extends AbstractPlatformTransactionManager {
        @Override
        protected Object doGetTransaction() {
            return new Object();
        }

        @Override
        protected void doBegin(Object transaction, TransactionDefinition definition) {
        }

        @Override
        protected void doCommit(DefaultTransactionStatus status) {
        }

        @Override
        protected void doRollback(DefaultTransactionStatus status) {
        }
    }
}
