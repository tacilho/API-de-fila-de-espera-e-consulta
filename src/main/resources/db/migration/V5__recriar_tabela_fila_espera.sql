-- Fazer backup dos dados
DROP TABLE IF EXISTS fila_espera_backup;
CREATE TABLE fila_espera_backup AS SELECT * FROM fila_espera;

-- Remover a tabela existente
DROP TABLE IF EXISTS fila_espera;

-- Recriar a tabela com o tipo correto
CREATE TABLE fila_espera (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL UNIQUE,
    pessoas INTEGER NOT NULL,
    prioridade INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL,
    posicao INTEGER,
    data_entrada TIMESTAMP NOT NULL,
    observacoes TEXT
);

-- Restaurar os dados
INSERT INTO fila_espera (id, nome, telefone, pessoas, prioridade, status, posicao, data_entrada, observacoes)
SELECT id, nome, telefone, pessoas, 
       CASE WHEN prioridade::text = 'true' THEN 1 ELSE 0 END,
       status, posicao, data_entrada, observacoes
FROM fila_espera_backup;

-- Adicionar a constraint para garantir valores entre 0 e 10
ALTER TABLE fila_espera ADD CONSTRAINT chk_prioridade CHECK (prioridade >= 0 AND prioridade <= 10);

-- Limpar o backup
DROP TABLE fila_espera_backup; 