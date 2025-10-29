-- ===============================
-- V5: FIX USER DATA FOR NO-AUTH
-- ===============================
-- Atualiza dados existentes após remoção da autenticação
-- Garante que usuários tenham tipo correto

-- ===============================
-- 1. ADICIONAR COLUNAS DE TIMESTAMP SE NÃO EXISTIREM
-- ===============================
ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

-- ===============================
-- 2. ATUALIZAR USUÁRIO ADMIN EXISTENTE
-- ===============================
-- Garantir que o usuário admin tenha o tipo correto
UPDATE users 
SET tipo_usuario = 'ADMIN' 
WHERE email = 'admin@maiconsoft.com' AND tipo_usuario IS NOT NULL;

-- ===============================
-- 3. ATUALIZAR OUTROS USUÁRIOS
-- ===============================
-- Garantir que outros usuários tenham tipo padrão
UPDATE users 
SET tipo_usuario = 'FUNCIONARIO' 
WHERE tipo_usuario IS NULL OR tipo_usuario = '';

-- ===============================
-- 4. ATUALIZAR TIMESTAMPS PARA REGISTROS EXISTENTES
-- ===============================
UPDATE users 
SET created_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP 
WHERE created_at IS NULL OR updated_at IS NULL;

-- ===============================
-- 5. VERIFICAÇÃO FINAL
-- ===============================
-- Logs para verificar
-- SELECT id_user, nome, email, tipo_usuario FROM users;