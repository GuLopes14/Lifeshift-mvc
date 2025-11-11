CREATE TABLE IF NOT EXISTS plano (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    perfil_id BIGINT NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    conteudo_gerado JSONB NOT NULL,
    data_geracao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_plano_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_plano_perfil FOREIGN KEY (perfil_id) REFERENCES perfil(id) ON DELETE CASCADE
);

CREATE INDEX idx_plano_usuario ON plano(usuario_id);
CREATE INDEX idx_plano_perfil ON plano(perfil_id);
CREATE INDEX idx_plano_data_geracao ON plano(data_geracao DESC);
