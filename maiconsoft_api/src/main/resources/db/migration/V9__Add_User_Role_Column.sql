-- ===============================
-- V9: ADD USER ROLE COLUMN IF NOT EXISTS
-- ===============================
-- Adiciona a coluna id_role na tabela users se ela não existir

-- ===============================
-- 1. ADICIONAR COLUNA ID_ROLE SE NÃO EXISTIR
-- ===============================
-- Adicionar coluna id_role (pode falhar se já existir, mas é seguro)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'id_role'
    ) THEN
        ALTER TABLE users ADD COLUMN id_role INTEGER;
    END IF;
END
$$;

-- ===============================
-- 2. ATUALIZAR USUÁRIOS EXISTENTES COM ROLES
-- ===============================
-- Atualizar usuários com tipo ADMIN para usar role ADMIN (id=1)
UPDATE users 
SET id_role = 1 
WHERE (tipo_usuario = 'ADMIN' OR tipo_usuario = 'admin') 
  AND (id_role IS NULL OR id_role = 0);

-- Atualizar usuários com tipo DIRETOR para usar role DIRETOR (id=2)
UPDATE users 
SET id_role = 2 
WHERE (tipo_usuario = 'DIRETOR' OR tipo_usuario = 'diretor') 
  AND (id_role IS NULL OR id_role = 0);

-- Atualizar usuários com tipo FUNCIONARIO para usar role FUNCIONARIO (id=3)
UPDATE users 
SET id_role = 3 
WHERE (tipo_usuario = 'FUNCIONARIO' OR tipo_usuario = 'funcionario' OR tipo_usuario IS NULL) 
  AND (id_role IS NULL OR id_role = 0);

-- Atualizar usuários com tipo VENDEDOR para usar role VENDEDOR (id=4)
UPDATE users 
SET id_role = 4 
WHERE (tipo_usuario = 'VENDEDOR' OR tipo_usuario = 'vendedor') 
  AND (id_role IS NULL OR id_role = 0);

-- ===============================
-- 3. DEFINIR CONSTRAINT PARA id_role
-- ===============================
-- Atualizar users sem role para FUNCIONARIO (fallback)
UPDATE users 
SET id_role = 3 
WHERE id_role IS NULL OR id_role = 0;

-- Adicionar foreign key constraint se não existir
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_users_role' AND table_name = 'users'
    ) THEN
        ALTER TABLE users ADD CONSTRAINT fk_users_role 
        FOREIGN KEY (id_role) REFERENCES user_roles(id_role);
    END IF;
END
$$;

-- ===============================
-- 4. COMENTÁRIOS PARA DOCUMENTAÇÃO
-- ===============================
COMMENT ON COLUMN users.id_role IS 'Chave estrangeira para user_roles - define permissões do usuário';
COMMENT ON COLUMN users.tipo_usuario IS 'Campo mantido para compatibilidade - use user_role.role_name';