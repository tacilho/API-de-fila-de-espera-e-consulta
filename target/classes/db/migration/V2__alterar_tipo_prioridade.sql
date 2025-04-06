-- Primeiro, fazemos backup dos dados existentes
CREATE TABLE fila_espera_backup AS SELECT * FROM fila_espera;

-- Removemos o valor padrão da coluna prioridade
ALTER TABLE fila_espera ALTER COLUMN prioridade DROP DEFAULT;

-- Depois, alteramos o tipo da coluna prioridade
ALTER TABLE fila_espera ALTER COLUMN prioridade TYPE integer USING CASE WHEN prioridade THEN 1 ELSE 0 END;

-- Adicionamos o novo valor padrão
ALTER TABLE fila_espera ALTER COLUMN prioridade SET DEFAULT 0;

-- Atualizamos os valores de prioridade para o novo padrão
UPDATE fila_espera SET prioridade = 0 WHERE prioridade = 0;
UPDATE fila_espera SET prioridade = 10 WHERE prioridade = 1;

-- Adicionamos uma constraint para garantir que a prioridade esteja entre 0 e 10
ALTER TABLE fila_espera ADD CONSTRAINT check_prioridade_range CHECK (prioridade >= 0 AND prioridade <= 10);

-- Verificamos se a alteração foi bem sucedida
SELECT id, nome, prioridade FROM fila_espera ORDER BY id; 