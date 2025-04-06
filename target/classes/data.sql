-- Criar a tabela fila_espera se não existir
CREATE TABLE IF NOT EXISTS fila_espera (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    telefone VARCHAR(20) NOT NULL UNIQUE,
    pessoas INTEGER NOT NULL,
    prioridade BOOLEAN NOT NULL DEFAULT false,
    status VARCHAR(20) NOT NULL DEFAULT 'AGUARDANDO',
    posicao INTEGER,
    data_entrada TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacoes TEXT
);

-- Criar a tabela historico_alocacao se não existir
CREATE TABLE IF NOT EXISTS historico_alocacao (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    mesa_id BIGINT NOT NULL,
    data_alocacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Atualizar registros existentes para ter data_entrada
UPDATE fila_espera 
SET data_entrada = CURRENT_TIMESTAMP 
WHERE data_entrada IS NULL;

-- Garantir que todos os registros tenham status
UPDATE fila_espera 
SET status = 'AGUARDANDO' 
WHERE status IS NULL;

-- Garantir que todos os registros tenham posição
UPDATE fila_espera 
SET posicao = 1 
WHERE posicao IS NULL; 