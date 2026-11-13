CREATE TABLE project (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_on TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE folder (
    id UUID PRIMARY KEY,
    name VARCHAR(60) NOT NULL,
    project_id UUID NOT NULL,
    parent_folder_id UUID,
    created_on TIMESTAMP WITH TIME ZONE NOT NULL,
    -- Necessária para a FK composta apontar para (project_id, id).
    CONSTRAINT uq_folder_project_id_id
        UNIQUE (project_id, id),

    -- Ao excluir um projeto, exclui suas pastas.
    CONSTRAINT fk_folder_project
        FOREIGN KEY (project_id)
            REFERENCES project (id)
            ON DELETE CASCADE,

    -- Exige que a pasta pai tenha o mesmo project_id da filha.
    -- Também exclui as subpastas quando o pai é excluído.
    CONSTRAINT fk_folder_parent_same_project -- Nome da restricao, usado para identifica-la no banco.
        FOREIGN KEY (project_id, parent_folder_id) -- Colunas da pasta filha que participam da verificacao.
            REFERENCES folder (project_id, id) --Busca na Table FOLDER uma pasta com o id da parent_folder_id e o mesmo project_id da pasta filha.
            ON DELETE CASCADE-- Ao excluir a pasta pai, exclui tambem suas filhas e descendentes.
);
