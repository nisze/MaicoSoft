-- ===============================
-- V3: REMOVE SECURITY FIELDS
-- ===============================
-- Remove campos relacionados à autenticação e autorização
-- Simplifica o sistema removendo password, roles e códigos de acesso

-- ===============================
-- 1. REMOVER CAMPOS DE SEGURANÇA DA TABELA USERS
-- ===============================

-- Primeiro remove a constraint de foreign key para roles
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_id_role_fkey;

-- Remove colunas relacionadas à segurança
ALTER TABLE users DROP COLUMN IF EXISTS senha;
ALTER TABLE users DROP COLUMN IF EXISTS id_role;
ALTER TABLE users DROP COLUMN IF EXISTS codigo_acesso;

-- ===============================
-- 2. ATUALIZAR ESTRUTURA DA TABELA USERS
-- ===============================

-- Adiciona campo para identificar tipo de usuário sem complexidade de roles
ALTER TABLE users ADD COLUMN IF NOT EXISTS tipo_usuario VARCHAR(20) DEFAULT 'FUNCIONARIO';

-- Atualiza o usuário admin existente
UPDATE users SET tipo_usuario = 'ADMIN' WHERE email = 'admin@maiconsoft.com';

-- ===============================
-- 3. REMOVER TABELA DE ROLES (OPCIONAL - MANTER PARA HISTÓRICO)
-- ===============================
-- DROP TABLE IF EXISTS user_roles CASCADE;

-- ===============================
-- 4. ADICIONAR COMENTÁRIOS
-- ===============================
COMMENT ON COLUMN users.tipo_usuario IS 'Tipo de usuário: ADMIN, FUNCIONARIO, VENDEDOR';
COMMENT ON TABLE users IS 'Tabela de usuários simplificada sem autenticação JWT';