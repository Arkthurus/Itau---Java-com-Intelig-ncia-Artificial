# TestFlow --- Documento de Arquitetura e Regras de Negócio

**Versão:** 1.2\
**Atualização:** 09/11/2026\
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
possuir várias `TestCaseExecution`.

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

-   `TestCase` representa a definição de um cenário de teste e pertence
    a exatamente uma `Folder`. Seu escopo de projeto é armazenado nele;
    entidades abaixo dele não duplicam `projectId` ou `folderId`.
-   `TestCase`, `TestStep`, `TestCaseExecution` e `TestStepExecution`
    possuem identificadores próprios e únicos. A identidade própria de
    `TestStep` não o torna independente do agregado `TestCase`.
-   Um `TestCase` possui título, objetivo e pré-condições, além de uma
    lista ordenada de `TestStep`. O objetivo e as pré-condições são
    opcionais; cada passo exige ação e resultado esperado.
-   Depois de finalizar a criação de um `TestCase`, o usuário pode
    escolher entre gerar uma execução ou editar o caso. A opção de editar
    libera a alteração do título, objetivo, pré-condições e `TestStep`s;
    essas alterações modificam a definição do caso, não execuções já
    iniciadas.
-   `TestStep` pertence ao agregado `TestCase`: tem identidade própria,
    mas é criado, alterado, reordenado e excluído por operações do
    `TestCase`, nunca isoladamente.
-   `TestCaseExecution` representa uma execução específica de um
    `TestCase`. Um caso pode ter várias execuções, preservando o
    histórico.
-   Ao iniciar uma execução, o sistema cria e persiste
    `TestCaseExecution` com `executionStatus = IN_PROGRESS` e um
    snapshot dos dados do caso de teste, incluindo título, textos e
    passos. Na tela de execução, os dados copiados do `TestCase` e dos
    `TestStep`s são somente para leitura; não podem ser editados nessa
    tela. Eles não mudam quando a definição original é editada.
-   Cada passo copiado é um `TestStepExecution` com identidade própria
    e cópia da ordem, ação e resultado esperado do `TestStep` original.
    Ele também armazena o resultado observado, o status do passo e
    anexos opcionais. O vínculo com o passo original é opcional; a cópia
    não depende da existência posterior desse passo.
-   Anexos/imagens pertencem ao `TestStepExecution`, nunca ao
    `TestStep` da definição. Não é obrigatório anexar imagens.
-   Ao finalizar, todos os passos da execução devem estar preenchidos.
    O `executionStatus` passa a `FINALIZED`, e o `testStatus` geral é
    calculado a partir dos status dos passos: `FAILED` se algum passo
    falhar; caso contrário, `PASSED`. Nenhum dos status gerais é
    informado diretamente pelo cliente.
-   Uma execução finalizada é imutável. Alterações posteriores no
    `TestCase` ou nos seus passos não alteram seu snapshot.
-   A exclusão de um `TestCase` é permitida e remove suas execuções e
    respectivos resultados em cascata. A exclusão de uma `Folder`
    também remove os casos de teste e todo o conteúdo subordinado.
-   A geração de PDF das execuções é uma funcionalidade planejada.

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

## 14. Funcionalidades específicas de Folder --- andamento

Implementados:

- `FindRootFoldersByProjectIdUseCase`: lista as pastas raiz de um projeto
  (`parentFolderId == null`).
- `FindSubFoldersUseCase`: lista as filhas imediatas de uma pasta.
- `MoveFolderUseCase`: movimenta uma pasta, valida se o projeto pai é o
  mesmo e impede movimentações que criariam ciclos.
- `FindFolderTreeByProjectIdUseCase`: monta a árvore completa de pastas
  do projeto, começando pelas raízes e buscando recursivamente os
  descendentes.

`FindFolderTreeByProjectIdUseCase` retorna uma lista de
`FolderTreeOutput`, que contém os dados da pasta e uma lista `children`
com as subárvores. O use case mantém os IDs do caminho atual durante a
recursão para detectar ciclos; remove cada ID ao sair do ramo para não
confundir pastas irmãs com ciclos.

Após montar a árvore, o use case conta recursivamente as pastas e publica
`FolderTreeListedEvent` com o ID do projeto, a quantidade total de pastas
e a duração da consulta/montagem. `FolderEventHandler` recebe o evento
com `@EventListener` e registra essas informações.

Testes adicionados para árvore com vários níveis, projeto sem raízes,
detecção de ciclo e publicação do evento.

Pontos a considerar:

- A implementação atual consulta os filhos de cada pasta durante a
  recursão. Em árvores grandes isso pode gerar várias consultas ao banco;
  avaliar futuramente buscar todas as pastas do projeto em lote e montar
  a hierarquia em memória.
- Ainda não foi criado endpoint HTTP para expor a árvore; não há
  `FolderController` na estrutura atual.
- `FindFolderPathUseCase` pode ser criado se houver necessidade de obter
  somente o caminho ancestral de uma pasta.
- `ReorderFoldersUseCase` fica para depois e exigiria um atributo de
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
6.  Escrever testes para criação de pasta raiz e subpasta, projeto
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

### Padrão de comentários no código

Comentários adicionados ou alterados devem ser didáticos e próximos do trecho
explicado, descrevendo regras de negócio, decisões ou efeitos não óbvios.
Em migrations, seguir o padrão da V1, inclusive comentários junto às linhas
de `CONSTRAINT`, `FOREIGN KEY`, `REFERENCES` e `ON DELETE`, quando aplicável.
Ao adicionar ou alterar operações `.stream()` e `.map()`, explicar a
transformação realizada quando isso ajudar a entender a lógica. Evitar
comentários que apenas repetem o que o código já deixa claro, como explicar
um `return`, `toList()` ou uma operação cujo nome já descreve seu efeito.
## 18. Atualização de implementação (09/11/2026)

Esta seção registra o estado implementado até 09/11/2026. As pendências das seções anteriores refletem o histórico do documento; os itens abaixo foram concluídos ou confirmados nessa data.

### 18.1 Itens anteriormente pendentes concluídos

- `FolderEntity` revisada: `name` permite nomes repetidos; `createdOn` não é anotado como nullable; Lombok usa `@Getter`, `@Setter` e `@NoArgsConstructor`; `projectId` e `parentFolderId` permanecem UUIDs simples.
- `JpaFolderRepository` converte entre IDs de domínio e UUIDs das entidades JPA.
- `CreateFolderUseCase` valida a existência do projeto, a existência da pasta pai quando informada e a consistência do projeto entre pai e filha antes de persistir.
- A migration Flyway V1 cria as tabelas `project` e `folder` e define as FKs. A FK de pai é composta (`project_id`, `parent_folder_id`) para referenciar (`project_id`, `id`), consolidando no banco a regra de que pai e filha pertencem ao mesmo projeto. A constraint `UNIQUE (project_id, id)` dá suporte a essa referência composta. As FKs usam exclusão em cascata.
- `spring.jpa.hibernate.ddl-auto=validate` está configurado; o Hibernate valida o schema criado pelas migrations.
- Regras de nome e obrigatoriedade são validadas no domínio e protegidas também por constraints do banco. As anotações Bean Validation presentes nas entidades/requests complementam essas validações quando aplicáveis.
- Os eventos de ações transacionais usam `@TransactionalEventListener(phase = AFTER_COMMIT)`: os handlers de log recebem os eventos após o commit, para registrar ações confirmadas. Os handlers assíncronos ficam habilitados por configuração dedicada em `AsyncConfig`.
- Foram adicionados testes dos use cases de pastas descritos abaixo. Os testes atuais cobrem a camada de aplicação; controllers de Folder e respectivos testes HTTP ainda serão implementados quando o controller for criado.

### 18.2 Use cases implementados

**Folder:**

- `CreateFolderUseCase`: cria pasta raiz ou subpasta após validar projeto e pai.
- `UpdateFolderUseCase`: atualiza o nome da pasta e publica evento.
- `DeleteFolderUseCase`: verifica existência, remove a pasta e publica evento.
- `GetFolderByIdUseCase`: consulta pasta por ID, com cache e evento de visualização.
- `GetAllFoldersUseCase`: lista pastas e publica evento com quantidade e duração.
- `FindRootFoldersByProjectIdUseCase`: lista pastas raiz de um projeto.
- `FindSubFoldersUseCase`: lista filhas imediatas de uma pasta.
- `FindFolderTreeByProjectIdUseCase`: monta a árvore do projeto, detecta ciclos e publica evento com contagem e duração.
- `MoveFolderUseCase`: move para outra pasta ou para a raiz; valida existência, projeto e ciclos.

O contrato atual de `FolderRepository` também inclui atualização do pai (`updateParentFolder`), busca de raízes por projeto (`findRootFoldersByProjectId`) e busca de filhas por pai (`findByParentFolderId`), além das operações básicas de salvar, consultar, listar, excluir e atualizar nome.

**Project:**

- `CreateProjectUseCase`: cria projeto.
- `UpdateProjectUseCase`: atualiza nome do projeto e publica evento.
- `DeleteProjectUseCase`: exclui projeto.
- `GetProjectByIdUseCase`: consulta projeto por ID.
- `GetAllProjectsUseCase`: lista projetos.

Os nomes das classes acima refletem os nomes atuais no código, inclusive `UserCase` nas duas classes de consulta de projeto.

### 18.3 Itens que continuam planejados

- Criar `FolderController` e expor os casos de uso de Folder por HTTP; depois, adicionar testes de controller/API.
- Implementar entidades e casos de uso de `TestCase`, `TestStep`, `TestExecution` e `TestStepExecution`, incluindo resultados reais e evidências por passo executado.
- Gerar PDFs de execuções.
- Avaliar otimização da montagem da árvore para buscar pastas em lote em vez de consultar os filhos recursivamente.
- Criar buscas de caminho ancestral ou reordenação de pastas se houver necessidade.

## 19. Implementação HTTP de Folder (10/11/2026)

Foi criado `FolderController` com as rotas básicas de CRUD:

- `POST /folders`: cria uma pasta; recebe `name`, `projectId` e `parentFolderId` (nulo para pasta raiz), responde `201 Created` com `FolderResponse`.
- `GET /folders/{id}`: consulta uma pasta por ID.
- `PUT /folders/{id}`: atualiza o nome da pasta.
- `DELETE /folders/{id}`: exclui a pasta e responde `204 No Content`.

`CreateFolderRequest` recebe UUIDs simples no JSON e os converte para `ProjectId` e `FolderId` de domínio. Os requests de criação e atualização limitam o nome a 60 caracteres, alinhado à coluna `folder.name`.

A listagem global plana (`GetAllFoldersUseCase`) não foi exposta neste controller; a consulta da árvore por projeto poderá ser adicionada como rota própria. A alteração de cache de leitura/atualização/exclusão foi alinhada para evitar respostas antigas e a exclusão passou a ocorrer em transação, permitindo que seu evento `AFTER_COMMIT` seja processado após a confirmação.

## 20. Rotas de Folder aninhadas por projeto (10/11/2026)

Esta modelagem substitui as rotas iniciais registradas na seção 19 e representa o contrato atual. As rotas HTTP de Folder usam o projeto como recurso pai. O `projectId` vem da URL; a criação não o repete no body. Operações sobre uma pasta verificam que ela pertence ao projeto informado.

- `POST /projects/{projectId}/folders`: cria pasta; `parentFolderId` opcional no body define se é raiz ou subpasta.
- `GET /projects/{projectId}/folders`: lista pastas raiz.
- `GET /projects/{projectId}/folders/tree`: retorna a árvore completa.
- `GET /projects/{projectId}/folders/{parentFolderId}/subfolders`: lista filhas diretas.
- `GET /projects/{projectId}/folders/{folderId}`: consulta pasta.
- `PUT /projects/{projectId}/folders/{folderId}`: atualiza nome.
- `DELETE /projects/{projectId}/folders/{folderId}`: exclui pasta.
- `PATCH /projects/{projectId}/folders/{folderId}/parent`: move pasta; body com `parentFolderId` UUID ou `null` para mover à raiz.

### 20.1 Validação do projeto nas consultas de pastas (10/11/2026)

As consultas de pastas raiz, subpastas e árvore completa verificam a existência do projeto antes de consultar as pastas. Se o projeto não existir, lançam `ProjectNotFoundException`, convertida pelo `GlobalExceptionHandler` em HTTP 404. A consulta de subpastas também verifica se a pasta pai pertence ao projeto indicado.

## 21. Testes HTTP e documentação da API (10/11/2026)

Foram criados testes MockMvc com Spring REST Docs para os endpoints de Project e Folder, cobrindo sucesso, conversão de requests/responses, códigos HTTP e projeto inexistente. Os use cases de raízes, árvore completa e subpastas validam a existência do projeto e lançam `ProjectNotFoundException` antes das consultas.

Os cenários HTTP cobertos incluem: criar, listar, buscar, atualizar e excluir projetos; erro 404 para projeto inexistente; criar pasta raiz, listar raízes, carregar árvore completa, listar filhas diretas, buscar, atualizar, excluir e mover uma pasta para a raiz; e erro 404 nas consultas de Folder para projeto inexistente. Os testes de use case cobrem árvore com vários níveis, projeto sem raízes, ciclo na árvore, busca de subpastas e movimentação válida, entre projetos diferentes e para dentro de uma descendente.

A documentação v2 consolidada de Project e Folder está em `src/docs/asciidoc/documentacao.adoc`; a versão anterior foi preservada em `src/docs/archive/documentacao-v1.adoc`. O PDF correspondente à versão atual foi gerado como `src/docs/asciidoc/projectsDoc-v2.pdf`.

Verificação: `gradlew test` e `gradlew asciidoctor` concluídos com sucesso.

## 22. Outros ajustes e verificações (10/11/2026)

- Os mapeadores HTTP `ProjectResponse` e `FolderResponse` usam `from(Output)`; as chamadas do `ProjectController` foram atualizadas para esse nome. `FolderTreeResponse` também converte recursivamente com `from(FolderTreeOutput)`.
- `CreateFolderRequest` agora recebe `parentFolderId` como UUID opcional no body e recebe `projectId` pela rota; converte os valores para IDs de domínio. Os requests de criação e atualização limitam o nome a 60 caracteres, conforme a coluna persistida.
- As consultas por pasta pai, leitura, atualização, exclusão e movimentação verificam o escopo do `projectId` informado. A listagem de subpastas, raízes e árvore também verifica a existência do projeto e lança `ProjectNotFoundException` quando ausente.
- `MoveFolderRequest` aceita UUID do novo pai ou `null` para mover à raiz. O use case mantém a validação de ciclos, projeto e existência.
- Corrigida a expressão da chave de cache em `GetFolderByIdUseCase`; atualização e exclusão invalidam o cache de pastas. `DeleteFolderUseCase` passou a ser transacional para que seu evento `AFTER_COMMIT` ocorra após a confirmação.
- Os testes de `ProjectController` e `FolderController` usam MockMvc/REST Docs. Os testes de use cases foram atualizados para fornecer `ProjectRepository` e o projeto no escopo, e para usar as novas assinaturas de movimentação e busca de subpastas.
- O aviso de depreciação Gradle foi rastreado com `--warning-mode all --stacktrace`: `StartParameter.isConfigurationCacheRequested` é acessado pela biblioteca Grolifant usada pelo plugin Asciidoctor Gradle 4.0.5. O código da aplicação não origina esse aviso. A migração para o novo modelo do plugin Asciidoctor 5.x foi avaliada, mas não aplicada.

As classes de consulta de Project atualmente se chamam `GetProjectByIdUseCase` e `GetAllProjectsUseCase`; os nomes antigos com `UserCase` foram renomeados.

## 23. Modelo e início da implementação de TestCase (13/11/2026)

Esta seção registra as decisões alinhadas para o bloco de casos de teste e
execuções. Ela complementa a seção 5 e prevalece sobre descrições anteriores
que ainda tratem snapshots ou status de execução como decisões pendentes.

### 23.1 Agregados e organização

- `TestCase` é o agregado que contém a definição do cenário; `TestStep` é
  interno a esse agregado. Ambos possuem UUID próprio, mas não haverá
  repositório, controller ou operações independentes para `TestStep`.
- `TestCaseExecution` é um agregado separado, referencia o caso por ID e
  mantém seu próprio snapshot. `TestStepExecution` pertence à execução e
  também possui UUID próprio.
- A organização de pacotes acordada é `domain/testcase/` para `TestCase`,
  com subpacotes `teststep/` e `testexecution/`; dentro de `testexecution/`,
  `teststepexecution/` contém `TestStepExecution` e seu ID. Nomes de pacotes
  devem ser minúsculos.
- O bloco terá dois controllers: um para `TestCase` e outro para
  `TestCaseExecution`. Os passos são manipulados no contexto do respectivo
  recurso pai, sem controllers próprios.

### 23.2 Comportamento acordado

- Na manutenção de `TestCase`, o usuário pode editar nome, objetivo,
  pré-condições e a coleção de passos. Depois de criar o caso, a interface
  oferece as opções de iniciar uma execução ou editar o caso.
- Ao iniciar uma execução, cria-se e persiste-se `TestCaseExecution` com
  status de execução `IN_PROGRESS`, copiando o título e os textos do caso,
  além dos passos e sua ordem. Esses dados copiados são somente para leitura
  na execução e permanecem iguais mesmo se a definição original for editada.
- Cada passo copiado é um `TestStepExecution` com novo ID, cópia da ordem,
  ação e resultado esperado, e campos próprios para resultado observado,
  status do passo e anexos. O vínculo ao `TestStep` original pode ser
  opcional; a cópia não depende da existência posterior do passo original.
- Anexos são opcionais. Para finalizar, todos os passos precisam estar
  preenchidos. A finalização altera `executionStatus` de `IN_PROGRESS` para
  `FINALIZED`, calcula `testStatus` como falha se algum passo falhou ou
  sucesso caso contrário, e torna a execução imutável.
- A exclusão direta de `TestCase` é permitida e remove suas execuções e
  snapshots. A exclusão de uma pasta também remove os casos e todo o
  conteúdo subordinado. A futura interface deve avisar que o conteúdo será
  excluído junto com a pasta.

### 23.3 Domínio iniciado e próximos passos

- O domínio inicial já contém `TestCase`, `TestCaseId`, `TestStep` e
  `TestStepId`. A criação gera IDs UUID; a reconstituição aceita IDs
  existentes.
- `TestCase` exige nome não vazio com até 150 caracteres, `ProjectId`,
  `FolderId` e ao menos um passo. Objetivo e pré-condições são opcionais;
  valores nulos ou em branco são normalizados para nulo.
- `TestStep` exige ação e resultado esperado não vazios. A ordem começa em
  1 e é atribuída pelo `TestCase` conforme a posição na lista, inclusive
  quando a definição é atualizada; a identidade do passo é preservada ao
  recalcular a ordem.
- O construtor de criação de `TestCase` gera um ID; o construtor de
  reconstituição recebe o ID persistido. `TestStep` segue o mesmo princípio,
  recebendo ID e ordem existentes quando reconstituído.
- A migration V2 foi iniciada para criar `test_case` e `test_step`, com FK
  composta que valida o projeto da pasta, exclusão em cascata e unicidade
  adiada da ordem dos passos. A migration ainda deve ser revisada e validada
  com Flyway antes de ser considerada concluída.
- Antes de avançar, consolidar os arquivos de domínio em um único package:
  há código iniciado tanto em `testcase` quanto em `testCase`. Depois,
  completar testes do agregado e avançar para persistência, casos de uso,
  controllers e testes HTTP. As tabelas de execução ficam para uma migration
  posterior, depois de TestCase estar pronto.
