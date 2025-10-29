-- ===============================
-- V7: ADD USER AUTHENTICATION FIELDS
-- ===============================
-- Adiciona campos de autenticação simples para funcionários
-- Código de acesso e senha (sem JWT)

-- ===============================
-- 1. ADICIONAR CAMPOS DE AUTENTICAÇÃO
-- ===============================
ALTER TABLE users ADD COLUMN IF NOT EXISTS codigo_acesso VARCHAR(6) UNIQUE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS senha VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS ativo BOOLEAN DEFAULT TRUE;

-- ===============================
-- 2. GERAR CÓDIGOS DE ACESSO PARA USUÁRIOS EXISTENTES
-- ===============================
-- Atualizar usuário admin com código padrão
UPDATE users 
SET codigo_acesso = 'ADM001', 
    senha = '$2a$10$Xl0yhvzLIaJCDdKBS0Lld.bnbubE27U0UMcgnvn8hSu.KJ8rA3.hC', -- Senha: 123456
    ativo = true
WHERE email = 'admin@maiconsoft.com' AND codigo_acesso IS NULL;

-- ===============================
-- 3. COMENTÁRIOS E ÍNDICES
-- ===============================
COMMENT ON COLUMN users.codigo_acesso IS 'Código de acesso único do funcionário (6 caracteres)';
COMMENT ON COLUMN users.senha IS 'Senha criptografada (BCrypt)';
COMMENT ON COLUMN users.ativo IS 'Status ativo/inativo do usuário';

-- Criar índice para busca por código de acesso
CREATE INDEX IF NOT EXISTS idx_users_codigo_acesso ON users(codigo_acesso);
CREATE INDEX IF NOT EXISTS idx_users_ativo ON users(ativo);