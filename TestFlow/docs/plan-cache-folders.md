# Plano de cache de consultas de folders

## Objetivo

Usar o cache Redis já configurado para evitar repetir consultas ao banco para pastas raiz, subpastas de um pai e folders individuais. O cache guarda resultados de consultas; não guarda caminho percorrido nem estado de expansão da árvore na interface.

## Implementação

- Manter `GetFolderByIdUseCase` cacheado com a chave composta por projeto e folder.
- Cachear `FindRootFoldersByProjectIdUseCase` por projeto.
- Cachear `FindSubFoldersUseCase` por projeto e pasta pai.
- Usar o cache existente `folders` e manter o TTL de 30 minutos.
- Não alterar o cache após criação; manter somente o listener de log existente.
- Após atualização (`PUT`), atualizar o detalhe e a lista de raiz/filhos correspondente somente se essas entradas já estiverem cacheadas.
- Após movimentação (`PATCH`), atualizar o `parentFolderId` no detalhe cacheado e mover a folder entre as listas de raiz/filhos de origem e destino, somente quando já estiverem cacheadas.
- Manter a invalidação total de `folders` em um listener síncrono `AFTER_COMMIT` após exclusão (`DELETE`).
- Garantir uma transação ao excluir o projeto e limpar `folders` em um listener `AFTER_COMMIT`, pois o banco exclui as pastas em cascata.
- Manter os listeners assíncronos de log separados dos listeners síncronos de invalidação.
- Manter os DTOs de resposta serializáveis para armazenamento no Redis.

## Critérios de aceitação

- Repetir uma consulta com os mesmos parâmetros reutiliza o resultado armazenado.
- Consultas de projetos ou pais diferentes usam entradas distintas.
- Update e move atualizam entradas existentes sem apagar as demais; create não altera o cache; delete limpa todas as entradas.
- As entradas permanecem disponíveis antes do commit e não são invalidadas quando a transação é revertida.
- Listas vazias cacheadas recebem a pasta em update/move quando aplicável; create não as altera e delete as limpa.
