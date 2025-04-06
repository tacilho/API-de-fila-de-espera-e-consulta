-- Primeiro, vamos fazer um backup dos dados
DROP TABLE IF EXISTS fila_espera_backup;
CREATE TABLE fila_espera_backup AS SELECT * FROM fila_espera;

-- Remover a coluna existente
ALTER TABLE fila_espera DROP COLUMN prioridade;

-- Adicionar a coluna novamente com o tipo correto
ALTER TABLE fila_espera ADD COLUMN prioridade INTEGER DEFAULT 0;

-- Atualizar os dados do backup
UPDATE fila_espera fe
SET prioridade = CASE 
    WHEN (SELECT prioridade::boolean FROM fila_espera_backup feb WHERE feb.id = fe.id) THEN 1
    ELSE 0
END;

-- Adicionar a constraint para garantir valores entre 0 e 10
ALTER TABLE fila_espera ADD CONSTRAINT chk_prioridade CHECK (prioridade >= 0 AND prioridade <= 10);

-- Limpar o backup
DROP TABLE fila_espera_backup; 