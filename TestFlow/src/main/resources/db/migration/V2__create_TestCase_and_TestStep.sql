CREATE TABLE test_case (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    folder_id UUID NOT NULL,
    name VARCHAR(150) NOT NULL,
    objective TEXT,
    preconditions TEXT,
    created_on TIMESTAMPTZ NOT NULL,
    -- Exige que a pasta referenciada pertença ao mesmo projeto do caso de teste.
    -- O project_id e o folder_id do caso de teste são comparados com os da pasta.
    -- Ao excluir a pasta, exclui também todos os casos de teste vinculados a ela.
    CONSTRAINT fk_test_case_folder -- Nome da restricao, usado para identifica-la no banco.
        FOREIGN KEY (project_id, folder_id) -- Colunas do TestCase usadas para localizar a pasta.
            REFERENCES folder (project_id, id) -- Busca uma pasta com o mesmo project_id e o id correspondente ao folder_id.
            ON DELETE CASCADE -- Ao excluir a pasta, exclui tambem os casos de teste associados.
);

-- Permite localizar os casos de teste associados a uma pasta dentro de um projeto.
CREATE INDEX idx_test_case_folder
    ON test_case (project_id, folder_id);

CREATE TABLE testStep (
    id UUID PRIMARY KEY,
    testCaseId UUID NOT NULL,
    stepOrder INTEGER NOT NULL,
    action TEXT NOT NULL,
    expectedResult TEXT NOT NULL,
    -- Exige que cada TestStep pertença a um TestCase de teste existente.
    -- Ao excluir o caso de teste, exclui também todos os seus passos.
    CONSTRAINT fk_test_step_test_case -- Nome da restricao, usado para identifica-la no banco.
        FOREIGN KEY (testCaseId) -- Coluna do TestStep que guarda o identificador do caso de teste.
            REFERENCES test_case (id) -- Busca na tabela TestCase o testCaseId informado pelo TestStep.
            ON DELETE CASCADE, -- Ao excluir o caso de teste, exclui tambem os passos associados.
    -- Garante que a ordem de cada passo seja positiva e comece em 1.
    CONSTRAINT chk_test_step_order_positive
        CHECK (stepOrder >= 1),
    -- Garante que cada posição da ordem seja usada uma única vez dentro do caso de teste.
    CONSTRAINT uq_test_step_order
        UNIQUE (testCaseId, stepOrder) -- Impede dois passos do mesmo caso de teste de usarem a mesma posição.
        DEFERRABLE INITIALLY DEFERRED -- Verifica a unicidade no commit, permitindo trocar posições na mesma transação.
);
