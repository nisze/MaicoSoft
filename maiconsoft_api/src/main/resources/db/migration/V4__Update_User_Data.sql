-- ===============================
-- V4: UPDATE USER DATA FOR NO-AUTH
-- ===============================
-- Atualiza dados existentes após remoção da autenticação
-- Adiciona valores padrão para usuários existentes

-- ===============================
-- 1. ATUALIZAR USUÁRIO ADMIN EXISTENTE
-- ===============================
-- Garantir que o usuário admin tenha o tipo correto
UPDATE users 
SET tipo_usuario = 'ADMIN' 
WHERE email = 'admin@maiconsoft.com';

-- ===============================
-- 2. ATUALIZAR OUTROS USUÁRIOS
-- ===============================
-- Garantir que outros usuários tenham tipo padrão
UPDATE users 
SET tipo_usuario = 'FUNCIONARIO' 
WHERE tipo_usuario IS NULL OR tipo_usuario = '';

UPDATE users 
SET updated_at = CURRENT_TIMESTAMP 
WHERE updated_at IS NULL;