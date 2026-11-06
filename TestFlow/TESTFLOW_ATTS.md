# TestFlow --- Documento de Arquitetura e Regras de Negócio

**Versão:** 1.1\
**Atualização:** 05/11/2026\
**Objetivo:** manter o contexto arquitetural, as decisões de negócio e o
andamento da implementação do TestFlow para continuidade do
desenvolvimento em outras sessões ou ferramentas.

------------------------------------------------------------------------

## 1. Visão geral

O TestFlow é uma aplicação para organizar, documentar e executar casos
de teste de software.

A aplicação permitirá: - Organizar testes por projetos, pastas e
subpastas. - Cadastrar casos de teste e seus passos. - Registrar
diferentes execuções de um mesmo caso de teste. - Registrar resultados
reais e evidências (imagens) por passo executado. - Preservar o
histórico de execuções. - Gerar relatórios/PDFs das execuções
futuramente.

Uma execução não substitui a anterior: um mesmo `TestCase` poderá
possuir várias `TestExecution`.

## 2. Tecnologias

Tecnologias utilizadas ou planejadas no projeto: - Java 25 - Spring Boot
4.1.1 - Gradle - Spring Web - Spring Data JPA - Spring Data REST / HAL
Explorer - PostgreSQL 17 - Docker Engine via Docker Compose (ambiente de
desenvolvimento) - Spring Validation - Spring REST Docs - Spring Boot
Actuator

A aplicação utiliza Hibernate/JPA para persistência e
criação/atualização do schema, conforme configuração do projeto.

## 3. Organização arquitetural

A arquitetura separa regras de negócio, casos de uso, persistência e
interface HTTP.

Estrutura conceitual atual:

``` text
src/main/java/
├── domain/
│   ├── project/
│   ├── folder/
│   ├── testcase/
│   └── testexecution/
├── application/
│   ├── project/
│   │   ├── input/
│   │   ├── output/
│   │   └── usecases/
│   └── folder/
│       ├── input/
│       ├── output/
│       └── usecases/
├── infrastructure/
│   ├── entity/
│   │   ├── project/
│   │   └── folder/
│   ├── repository/
│   │   ├── project/
│   │   └── folder/
│   └── event/
│       ├── project/
│       └── folder/
└── http/
    ├── controller/
    ├── request/
    └── response/
```

A estrutura é uma referência conceitual e pode evoluir conforme a
implementação.

### Responsabilidades

-   **domain:** modelos de domínio, IDs, interfaces de repositório,
    exceções e regras de negócio.
-   **application:** casos de uso, inputs e outputs.
-   **infrastructure:** entidades JPA, repositórios Spring Data,
    implementações das interfaces de domínio, mappers e eventos.
-   **http:** controllers e DTOs de entrada/saída HTTP.

O domínio deve continuar independente de entidades JPA e de detalhes do
Spring.

## 4. Modelo de domínio e regras de negócio

### 4.1 Hierarquia Project \> Folder \> Folder

-   Um `Project` pode possuir várias `Folder`.
-   Toda `Folder` pertence obrigatoriamente a exatamente um `Project`.
-   Uma `Folder` pode possuir uma pasta pai (`parentFolderId`).
-   `parentFolderId == null` representa uma pasta raiz, diretamente
    vinculada ao projeto.
-   Subpastas são representadas pela mesma entidade `Folder`,
    relacionadas recursivamente por `parentFolderId`.
-   Uma pasta filha deve pertencer ao mesmo projeto da pasta pai.
-   A validação de consistência entre o projeto da pasta filha e o da
    pasta pai é responsabilidade da camada de aplicação/Use Case.
-   Um `TestCase` obrigatoriamente pertence a uma `Folder`; não haverá
    `TestCase` diretamente na raiz do projeto.

### 4.2 Nomes de pastas

Decisão tomada em 05/11/2026: - Pastas podem ter nomes repetidos entre
projetos diferentes. - Pastas também podem ter nomes repetidos dentro do
mesmo projeto, inclusive no mesmo nível hierárquico. - A identificação
de uma pasta é feita pelo seu ID, não pelo nome. - Portanto, a coluna
`name` de `FolderEntity` não deve ter unicidade global
(`unique = true`).

### 4.3 Movimentação de pastas --- regras futuras

Ao implementar movimentação: - Deve ser possível mover uma pasta para a
raiz do projeto (`parentFolderId = null`). - A pasta não pode ser movida
para dentro dela mesma. - A pasta não pode ser movida para dentro de uma
de suas descendentes. - A nova pasta pai, se informada, deve pertencer
ao mesmo projeto. - Uma movimentação não pode quebrar a regra de que
todas as pastas da hierarquia pertencem ao mesmo projeto.

Essas regras serão implementadas principalmente em um futuro
`MoveFolderUseCase`.

## 5. Modelo de casos de teste e execuções

-   `TestCase` representa a definição de um cenário de teste.
-   Um `TestCase` possui nome, descrição/objetivo, pré-condições, uma
    `Folder` e uma lista de `TestStep`.
-   Cada `TestStep` descreve uma ação e o resultado esperado.
-   `TestExecution` representa uma execução específica de um `TestCase`.
-   Um mesmo `TestCase` pode ter várias execuções, preservando
    histórico.
-   `TestStepExecution` representa o resultado de um passo em uma
    execução.
-   A execução do passo armazena resultado real, status e evidências.
-   Evidências/imagens pertencem à execução do passo
    (`TestStepExecution`), não à definição original (`TestStep`).
-   Status iniciais previstos: `PASSED` e `FAILED`.
-   A geração de PDF das execuções é uma funcionalidade planejada.

Pode ser útil armazenar em `TestStepExecution` um snapshot da ação e do
resultado esperado originais, para preservar o contexto histórico mesmo
se o `TestStep` for alterado depois.

## 6. IDs e persistência

-   `ProjectId` e `FolderId` são tipos de domínio que encapsulam UUID.
-   As entidades de persistência utilizam `UUID`.
-   O domínio não deve depender de `ProjectEntity` ou `FolderEntity`.
-   A conversão entre IDs do domínio e entidades JPA é responsabilidade
    dos mappers/repositórios de infraestrutura.

### 6.1 Decisão sobre relacionamentos JPA

Decisão reafirmada em 05/11/2026: - A `FolderEntity` continuará
armazenando `projectId` e `parentFolderId` como campos `UUID`. - Não
será adotado `@ManyToOne` neste momento. - Os campos UUID representam os
identificadores relacionados, mas, isoladamente, não informam ao
Hibernate que são associações JPA. - Não é necessário transformar esses
campos em objetos `ProjectEntity` e `FolderEntity` para manter o domínio
desacoplado. - A aplicação continuará validando as regras de negócio nos
Use Cases.

### 6.2 Chaves estrangeiras (FKs) --- decisão e pendência

Objetivo desejado: - `folder.project_id` deve referenciar
`project.id`. - `folder.parent_folder_id`, quando não nulo, deve
referenciar `folder.id`.

Importante: - Com os campos UUID simples e sem associações JPA, o
Hibernate não deduz automaticamente essas FKs apenas pelo nome/tipo dos
campos. - A FK é uma restrição do banco de dados; ela garante que o ID
referenciado exista. - A FK de `parent_folder_id` não garante que pai e
filha pertençam ao mesmo projeto. Essa regra continua sendo validada
pelo Use Case.

Decisão de implementação: - Preferência atual: manter os UUIDs simples
e, quando formos adicionar as FKs, criá-las via SQL, sem introduzir
`@ManyToOne`. - Foi considerada a alternativa de criar
`src/main/resources/schema.sql` e configurar a inicialização SQL para
ocorrer depois do Hibernate. Entretanto, o usuário ainda **não
implementou nada dessa configuração**. - Também foi mencionada a
possibilidade futura de usar Flyway, mas isso exigiria adicionar
dependência e migrations; não é a opção mais simples para o estágio
atual.

Pendências antes de aplicar FKs: 1. Conferir o valor atual de
`spring.jpa.hibernate.ddl-auto` no
`application.properties`/`application.yml`. 2. Conferir os nomes reais
das tabelas e colunas geradas pelo Hibernate (`project`, `folder`, `id`,
`project_id`, `parent_folder_id`, ou os nomes efetivamente
configurados). 3. Verificar se já existem dados inválidos antes de
adicionar as restrições. 4. Definir a execução do SQL depois da criação
das tabelas pelo Hibernate. 5. Evitar executar
`ALTER TABLE ... ADD CONSTRAINT` repetidamente sem verificar se a
constraint já existe.

SQL conceitual a ser adaptado aos nomes reais do schema:

``` sql
ALTER TABLE folder
ADD CONSTRAINT fk_folder_project
FOREIGN KEY (project_id)
REFERENCES project(id);

ALTER TABLE folder
ADD CONSTRAINT fk_folder_parent
FOREIGN KEY (parent_folder_id)
REFERENCES folder(id);
```

Nenhum arquivo `schema.sql`, migration ou configuração de FK foi criado
até o momento desta atualização.

## 7. Folder --- entidade de domínio

A classe de domínio `Folder` possui: - `FolderId id` - `String name` -
`ProjectId projectId` - `FolderId parentFolderId` (nullable)

O construtor de criação gera um novo `FolderId`, valida nome não vazio e
`projectId` não nulo. O construtor de reconstrução recebe o ID
existente.

`parentFolderId` deve continuar podendo ser `null`; não usar `Optional`
como campo da entidade.

Foi observado que Lombok `@Setter` em todos os campos pode permitir
alterações irrestritas. Como melhoria futura, considerar setters mais
restritos e métodos de domínio explícitos para alterações permitidas.

## 8. Folder --- entidade de persistência

A `FolderEntity` compartilhada pelo usuário possui a seguinte estrutura
conceitual:

``` java
@Entity
@Data
@RequiredArgsConstructor
public class FolderEntity {
    @Id
    private UUID id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 60)
    private String name;

    @NotNull
    private UUID projectId;

    @Nullable
    private UUID parentFolderId;

    @Nullable
    @Column(nullable = false, updatable = false)
    private Instant createdOn;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if (createdOn == null) {
            createdOn = Instant.now();
        }
    }
}
```

Ajustes discutidos: - Remover `unique = true` de `name`, pois nomes
repetidos serão permitidos. - `createdOn` tem `nullable = false` no
banco e é preenchido em `@PrePersist`; portanto, remover `@Nullable`
desse campo. - Considerar `@Getter`, `@Setter` e `@NoArgsConstructor` em
vez de `@Data` e `@RequiredArgsConstructor`, para evitar `equals`,
`hashCode` e `toString` gerados indiscriminadamente em entidade JPA e
deixar explícito o construtor sem argumentos. - `projectId` e
`parentFolderId` continuam como UUIDs simples por decisão atual. - Ainda
é necessário verificar se as anotações de validação (`@NotBlank`,
`@NotNull`) estão sendo aplicadas no ciclo de persistência desejado; as
constraints `nullable = false` do banco são uma proteção separada.

## 9. Repositórios de Folder

A interface de domínio `FolderRepository` foi discutida com operações
semelhantes a:

``` java
public interface FolderRepository {
    Folder save(Folder folder);
    Optional<Folder> findById(FolderId id);
    List<Folder> findAll();
    void delete(FolderId id);
    boolean updateName(FolderId id, String name);
    List<Folder> findFolderByProjectId(ProjectId projectId);
    List<Folder> findByParentFolderId(FolderId parentFolderId);
}
```

A assinatura real pode ter evoluído; confirmar no código antes de
alterar.

A infraestrutura possui: - `FolderEntityRepository`, repositório Spring
Data JPA. - `JpaFolderRepository`, implementação da interface de
domínio, responsável por converter domínio ↔ entidade.

Observações já discutidas: - `save()` foi concebido como operação de
criação: verifica se o ID já existe e lança exceção específica em caso
de duplicidade. - A verificação `existsById` não elimina condição de
corrida; a PK do banco é a proteção final. - `updateName()` faz
atualização direcionada e retorna `true` se a consulta afetar linhas. O
comportamento quando o nome novo é igual ao antigo pode depender do
banco/consulta. - Para consultas futuras, implementar busca por projeto
e busca por pasta pai. - Em operações JPA de atualização direta,
observar `@Modifying` e transação.

## 10. Inputs e Outputs

-   Input é o conjunto de dados de que o Use Case precisa; não precisa
    corresponder exatamente ao corpo JSON.
-   O Controller pode combinar parâmetros de rota e corpo HTTP para
    construir o Input.
-   Output é o resultado que o Use Case entrega ao chamador; o
    Controller pode convertê-lo em resposta HTTP.

### FolderOutput

Foi discutido que o output básico deve conter: - `id` - `name` -
`projectId` - `parentFolderId` (nullable)

Não precisa conter `projectName` ou `parentFolderName` neste momento.

Para exibição hierárquica futura, criar um output específico, por
exemplo `FolderTreeOutput`, com `children`, e um
`FindFolderTreeByProjectIdUseCase`. Evitar complicar o output básico.

### Endpoints conceituais

-   Criar pasta raiz: `POST /projects/{projectId}/folders`, body com
    `name`; o Controller monta o Input com `parentFolderId = null`.
-   Criar subpasta: `POST /folders/{parentFolderId}/subfolders`, body
    com `name`; o projeto pode ser derivado da pasta pai ou informado e
    validado.
-   A API ainda pode evoluir; esses caminhos são sugestões, não contrato
    definitivo.

## 11. CreateFolderUseCase --- andamento

Código mais recente compartilhado pelo usuário (com imports e formatação
normalizados):

``` java
@Service
public class CreateFolderUseCase {
    private final FolderRepository folderRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CreateFolderUseCase(
            FolderRepository folderRepository,
            ApplicationEventPublisher eventPublisher
    ) {
        this.folderRepository = folderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public FolderOutput execute(CreateFolderInput input) {
        long start = System.nanoTime();

        if (input.parentFolderId() != null) {
            var parentFolder = folderRepository.findById(input.parentFolderId())
                    .orElseThrow(() ->
                            new ParentFolderNotFoundException(input.parentFolderId())
                    );

            if (!input.projectId().equals(parentFolder.getProjectId())) {
                throw new FolderProjectMismatchException(
                        parentFolder.getName(),
                        parentFolder.getProjectId(),
                        input.name(),
                        input.projectId()
                );
            }
        }

        var folder = new Folder(
                input.name(),
                input.projectId(),
                input.parentFolderId()
        );

        var saved = folderRepository.save(folder);

        long duration = (System.nanoTime() - start) / 1_000_000;

        eventPublisher.publishEvent(
                new FolderCreatedEvent(saved.getName(), saved.getId(), duration)
        );

        return FolderOutput.from(saved);
    }
}
```

Regras já cobertas: - Se `parentFolderId` for informado, a pasta pai
precisa existir. - A pasta pai e a nova pasta precisam pertencer ao
mesmo projeto. - Cria o objeto de domínio somente depois dessas
validações. - Persiste a pasta. - Publica evento de criação. - Retorna
`FolderOutput`. - Executa dentro de `@Transactional`.

Pendência importante: - Ainda falta validar que `input.projectId()`
corresponde a um projeto existente, inclusive na criação de pasta raiz
(`parentFolderId == null`). A recomendação foi injetar
`ProjectRepository` e buscar o projeto, lançando
`ProjectNotFoundException` quando ausente. - Remover imports não
utilizados, como `CacheEvict` se ainda estiver presente. - O código não
foi apresentado como compilado/testado; revisar imports, nomes reais das
classes e assinaturas.

### FolderProjectMismatchException

A decisão foi usar nomes e IDs dos projetos disponíveis, sem exigir o ID
da pasta nova (que ainda não existe antes da persistência). A versão
discutida recebe: - nome da pasta pai; - `ProjectId` da pasta pai; -
nome da nova pasta; - `ProjectId` informado para a nova pasta.

A mensagem pode explicar que os projetos são diferentes. A exceção é
lançada antes de persistir.

## 12. UpdateFolderUseCase --- andamento

A versão discutida: - Busca a pasta por `FolderId`, lançando
`FolderNotFoundException` se não existir. - Guarda o nome antigo. -
Converte `parentFolderId` para String quando não nulo. - Executa
`folderRepository.updateName(id, input.name())`. - Se não houver linha
afetada, lança `FolderNotFoundException`. - Publica
`FolderUpdatedEvent`. - Retorna `FolderOutput` com nome novo e demais
dados da pasta.

Observações: - A entidade de domínio carregada mantém o nome antigo
porque `updateName()` atualiza diretamente no banco; o Output usa
`input.name()`, o que é coerente para essa operação simples. - Para
alterações com múltiplos campos ou regras, preferir carregar, alterar o
objeto de domínio e persistir. - O `duration` deve ser calculado como
`(System.nanoTime() - start) / 1_000_000`. - A invalidação de cache com
`@CacheEvict` só funciona corretamente se a chave corresponder à usada
no cache. Para garantir invalidação após commit, considerar estratégia
explícita after-commit. - Importar
`org.springframework.transaction.annotation.Transactional` para
transações Spring.

## 13. Eventos e handlers

Eventos de domínio/aplicação são publicados por
`ApplicationEventPublisher`.

Para eventos de criação/atualização/exclusão que dependem de transação,
foi adotado o padrão:

``` java
@Async
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void handle(FolderCreatedEvent event) {
    // ...
}
```

Pontos importantes: - Não é necessário alterar o record do evento nem a
chamada `publishEvent` ao trocar `@EventListener` por
`@TransactionalEventListener`. - `@TransactionalEventListener` executa
após commit e, por padrão, não executa se não houver transação ativa. -
`@Async` executa o handler em outra thread; falhas no handler não
desfazem a transação já confirmada. - Para efeitos externos críticos que
precisem de entrega confiável, considerar Transactional Outbox no
futuro. - `@EnableAsync` deve ficar em uma classe `@Configuration`
dedicada, não no handler. - `@RepositoryEventHandler` é para eventos do
Spring Data REST; não é necessário para eventos publicados manualmente
via `ApplicationEventPublisher`. - Eventos de visualização podem não
ocorrer dentro de transação; avaliar `@EventListener` comum para esses
casos.

## 14. Funcionalidades específicas de Folder --- sequência sugerida

Após finalizar CRUD: 1. `FindRootFoldersByProjectIdUseCase`: buscar
pastas do projeto com `parentFolderId == null`. 2.
`FindSubFoldersUseCase`: buscar somente filhos imediatos de uma pasta.
3. `MoveFolderUseCase`: mover pasta, validar mesmo projeto e impedir
ciclos. 4. `FindFolderPathUseCase`: obter caminho hierárquico, se
necessário. 5. `FindFolderTreeByProjectIdUseCase`: montar árvore para o
frontend.

Para montar a árvore inicialmente, buscar todas as pastas do projeto em
uma consulta e montar a hierarquia em memória, evitando uma consulta por
nível.

`ReorderFoldersUseCase` fica para depois e exigiria um atributo de
ordenação.

## 15. Pendências imediatas / próximos passos

1.  Revisar a `FolderEntity`:
    -   remover `unique = true` de `name`;
    -   remover `@Nullable` de `createdOn`;
    -   avaliar trocar `@Data` + `@RequiredArgsConstructor` por
        `@Getter` + `@Setter` + `@NoArgsConstructor`;
    -   manter `projectId` e `parentFolderId` como UUIDs simples.
2.  Revisar o mapper `JpaFolderRepository` para manter a conversão entre
    UUIDs e IDs de domínio.
3.  Adicionar ao `CreateFolderUseCase` a validação de existência do
    projeto via `ProjectRepository`.
4.  Verificar `application.properties`/`application.yml`, especialmente
    `spring.jpa.hibernate.ddl-auto`.
5.  Decidir e implementar as FKs via SQL quando o schema estiver
    confirmado. Ainda não foi criado `schema.sql` nem migration.
6.  Implementar e testar as consultas por projeto e por pasta pai.
7.  Escrever testes para criação de pasta raiz e subpasta, projeto
    inexistente, pasta pai inexistente e divergência de projeto.

### Cenários de teste de criação

-   Criar pasta raiz com projeto existente: sucesso.
-   Criar subpasta com pai existente e mesmo projeto: sucesso.
-   Criar subpasta com pai inexistente: `ParentFolderNotFoundException`.
-   Criar subpasta com projeto diferente do pai:
    `FolderProjectMismatchException`.
-   Criar pasta raiz com projeto inexistente:
    `ProjectNotFoundException`.

------------------------------------------------------------------------

## 16. Como usar este documento com ChatGPT ou Codex

Este documento serve como contexto e registro das decisões do TestFlow.

-   Ao iniciar uma nova conversa, anexar ou colar `TESTFLOW.md` e pedir
    que o assistente o leia antes de sugerir alterações.
-   No Codex, manter o arquivo na raiz do repositório e solicitar
    explicitamente que ele leia `TESTFLOW.md` antes de trabalhar.
-   O documento descreve decisões e progresso, mas não substitui o
    código-fonte: sempre conferir a implementação real antes de assumir
    que algo já foi concluído.
-   Ao final de cada sessão de desenvolvimento, atualizar este documento
    com decisões novas, alterações de regras e pendências, preservando o
    histórico útil e removendo instruções que tenham sido superadas.

## 17. Decisão de arquitetura — migrations iniciais (06/11/2026)

A migration inicial do Flyway (`V1`) deve criar somente as tabelas das entidades já configuradas no código e prontas para persistência. No estado atual, isso inclui `Project` e `Folder`. Entidades planejadas, mas ainda não implementadas/configuradas, não devem ganhar tabelas antecipadamente. Quando cada nova entidade for implementada e seu mapeamento estiver definido, o schema correspondente será adicionado em uma migration Flyway posterior.