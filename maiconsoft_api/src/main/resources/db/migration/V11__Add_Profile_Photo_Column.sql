-- ===============================
-- V11: ADD PROFILE PHOTO COLUMN
-- ===============================
-- Adiciona coluna para armazenar caminho da foto de perfil

-- ===============================
-- 1. ADICIONAR COLUNA PROFILE_PHOTO_PATH
-- ===============================
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'profile_photo_path'
    ) THEN
        ALTER TABLE users ADD COLUMN profile_photo_path VARCHAR(255);
    END IF;
END
$$;

-- ===============================
-- 2. COMENTÁRIOS PARA DOCUMENTAÇÃO
-- ===============================
COMMENT ON COLUMN users.profile_photo_path IS 'Caminho para a foto de perfil do usuário (relativo ao diretório de uploads)';

-- ===============================
-- 3. ÍNDICE PARA OTIMIZAÇÃO (OPCIONAL)
-- ===============================
-- Criar índice se não existir
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes 
        WHERE tablename = 'users' AND indexname = 'idx_users_profile_photo'
    ) THEN
        CREATE INDEX idx_users_profile_photo ON users(profile_photo_path);
    END IF;
END
$$;