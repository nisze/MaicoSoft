-- ===============================
-- V10: REMOVE TIPO_USUARIO COLUMN
-- ===============================
-- Remove a coluna tipo_usuario agora que usamos user_roles

-- ===============================
-- 1. VERIFICAR SE TODOS OS USUARIOS TEM ROLE DEFINIDA
-- ===============================
-- Garantir que nenhum usuário fique sem role
UPDATE users 
SET id_role = 3 -- FUNCIONARIO como padrão
WHERE id_role IS NULL OR id_role = 0;

-- ===============================
-- 2. REMOVER COLUNA TIPO_USUARIO
-- ===============================
-- Remover a coluna tipo_usuario (agora redundante)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'tipo_usuario'
    ) THEN
        ALTER TABLE users DROP COLUMN tipo_usuario;
    END IF;
END
$$;

-- ===============================
-- 3. TORNAR ID_ROLE OBRIGATÓRIO
-- ===============================
-- Tornar id_role NOT NULL se ainda não for
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'id_role' 
        AND is_nullable = 'YES'
    ) THEN
        ALTER TABLE users ALTER COLUMN id_role SET NOT NULL;
    END IF;
END
$$;

-- ===============================
-- 4. COMENTÁRIOS FINAIS
-- ===============================
COMMENT ON COLUMN users.id_role IS 'Chave estrangeira obrigatória para user_roles - define o perfil e permissões do usuário';