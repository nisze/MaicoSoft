-- ===============================
-- V2: INSERT INITIAL DATA
-- ===============================
-- Inserção de dados iniciais para o sistema MAICONSOFT
-- Roles de usuário e usuário administrador padrão

-- ===============================
-- 1. INSERIR ROLES DE USUÁRIO
-- ===============================
INSERT INTO user_roles (id_role, role_name) VALUES 
(1, 'ADMIN'),
(2, 'DIRETOR'),
(3, 'FUNCIONARIO'),
(4, 'VENDEDOR')
ON CONFLICT (id_role) DO NOTHING;

-- ===============================
-- 2. INSERIR USUÁRIO ADMINISTRADOR PADRÃO
-- ===============================
-- Usuário: admin@maiconsoft.com (sem autenticação)
INSERT INTO users (id_user, codigo_acesso, nome, email, cpf, telefone, id_role, senha, created_at, updated_at) VALUES 
(1, 'ADM001', 'Administrador Sistema', 'admin@maiconsoft.com', '12345678901', '(11) 99999-9999', 1, '$2a$10$gI0mWhw4WoGjt3kKtSLdGe8s/rduhqbNGYgOQqhBgE3mvS1oL9N3u', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id_user) DO NOTHING;

-- ===============================
-- 3. CONFIGURAR SEQUENCES (POSTGRESQL)
-- ===============================
SELECT setval('user_roles_id_role_seq', COALESCE((SELECT MAX(id_role) FROM user_roles), 1), true);
SELECT setval('users_id_user_seq', COALESCE((SELECT MAX(id_user) FROM users), 1), true);
