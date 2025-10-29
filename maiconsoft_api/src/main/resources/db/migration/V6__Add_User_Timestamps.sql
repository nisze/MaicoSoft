-- ===============================
-- V6: ADD USER TIMESTAMPS
-- ===============================
-- Adiciona colunas de timestamp que foram removidas mas são necessárias na entidade

-- ===============================
-- 1. ADICIONAR COLUNAS DE TIMESTAMP
-- ===============================
ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

-- ===============================
-- 2. ATUALIZAR REGISTROS EXISTENTES
-- ===============================
UPDATE users 
SET created_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP 
WHERE created_at IS NULL OR updated_at IS NULL;

-- ===============================
-- 3. COMENTÁRIOS
-- ===============================
COMMENT ON COLUMN users.created_at IS 'Data/hora de criação do registro';
COMMENT ON COLUMN users.updated_at IS 'Data/hora da última atualização do registro';