CREATE TABLE IF NOT EXISTS perfil (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    profissao_atual VARCHAR(255) NOT NULL,
    profissao_desejada VARCHAR(255) NOT NULL,
    habilidades_atuais TEXT NOT NULL,
    descricao TEXT,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_perfil_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

CREATE INDEX idx_perfil_usuario ON perfil(usuario_id);
