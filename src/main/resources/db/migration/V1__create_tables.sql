-- Criação do schema
CREATE SCHEMA IF NOT EXISTS public;

-- Criação da tabela 'mesas'
CREATE TABLE IF NOT EXISTS public.mesas (
    id BIGSERIAL PRIMARY KEY,
    numero INTEGER NOT NULL,
    capacidade INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('DISPONIVEL', 'OCUPADA')),
    mesa_agrupada_id BIGINT,
    capacidade_agrupada INTEGER,
    FOREIGN KEY (mesa_agrupada_id) REFERENCES public.mesas(id) ON DELETE SET NULL
);

-- Criação da tabela 'fila_espera'
CREATE TABLE IF NOT EXISTS public.fila_espera (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    pessoas INTEGER NOT NULL CHECK (pessoas >= 1),
    hora_entrada TIMESTAMP NOT NULL,
    prioridade INTEGER NOT NULL DEFAULT 0,
    observacoes TEXT,
    status VARCHAR(20) NOT NULL CHECK (status IN ('AGUARDANDO', 'CHAMADO', 'ATENDIDO'))
);

-- Criação da tabela 'historico_alocacao'
CREATE TABLE IF NOT EXISTS public.historico_alocacao (
    id BIGSERIAL PRIMARY KEY,
    nome_cliente VARCHAR(100) NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    data_hora_chamada TIMESTAMP NOT NULL,
    mesa_id BIGINT NOT NULL,
    FOREIGN KEY (mesa_id) REFERENCES public.mesas(id) ON DELETE SET NULL
);

-- Inserção de dados iniciais para teste
INSERT INTO public.mesas (numero, capacidade, status, capacidade_agrupada) VALUES
    (1, 4, 'DISPONIVEL', 4),
    (2, 2, 'DISPONIVEL', 2),
    (3, 6, 'OCUPADA', 6),
    (4, 4, 'DISPONIVEL', 4);

-- Índices para melhorar performance
CREATE INDEX idx_fila_espera_status ON public.fila_espera(status);
CREATE INDEX idx_fila_espera_hora_entrada ON public.fila_espera(hora_entrada);
CREATE INDEX idx_mesas_status ON public.mesas(status); 