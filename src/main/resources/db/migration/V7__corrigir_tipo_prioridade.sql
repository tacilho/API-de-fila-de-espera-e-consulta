-- Backup dos dados existentes
CREATE TABLE fila_espera_backup AS SELECT * FROM fila_espera;

-- Remove a coluna existente
ALTER TABLE fila_espera DROP COLUMN prioridade;

-- Adiciona a coluna com o tipo correto
ALTER TABLE fila_espera ADD COLUMN prioridade INTEGER NOT NULL DEFAULT 0;

-- Atualiza os dados baseado no backup
UPDATE fila_espera fe
SET prioridade = CASE 
    WHEN feb.prioridade::text = 'true' THEN 1
    WHEN feb.prioridade::text = 'false' THEN 0
    ELSE COALESCE(feb.prioridade::integer, 0)
END
FROM fila_espera_backup feb
WHERE fe.id = feb.id;

-- Adiciona a constraint para garantir valores válidos
ALTER TABLE fila_espera ADD CONSTRAINT prioridade_range CHECK (prioridade >= 0 AND prioridade <= 10);

-- Remove a tabela de backup
DROP TABLE fila_espera_backup; 