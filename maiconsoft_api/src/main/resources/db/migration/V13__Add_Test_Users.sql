-- USUARIOS DE TESTE
-- IMPORTANTE: Mantendo a mesma senha do usuario original (ADM001)
-- A senha eh a mesma que ja estava funcionando no sistema
-- Codigos com max 6 caracteres

DELETE FROM users WHERE id_user > 1;

-- Manter a senha original do ADM001 (nao alterar)
-- Todos os usuarios de teste usam a senha "123456" (mesmo hash do ADM001)

-- Inserir usuarios adicionais com a MESMA senha do ADM001: "123456"
INSERT INTO users (codigo_acesso, nome, email, cpf, telefone, id_role, senha, ativo, created_at, updated_at) VALUES 
('DIR001', 'Carlos Diretor', 'diretor@maiconsoft.com', '11122233344', '(11) 98888-8888', 2, '$2a$10$gI0mWhw4WoGjt3kKtSLdGe8s/rduhqbNGYgOQqhBgE3mvS1oL9N3u', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('FUN001', 'Maria Funcionaria', 'funcionario@maiconsoft.com', '22233344455', '(11) 97777-7777', 3, '$2a$10$gI0mWhw4WoGjt3kKtSLdGe8s/rduhqbNGYgOQqhBgE3mvS1oL9N3u', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('VEN001', 'Joao Vendedor', 'vendedor@maiconsoft.com', '33344455566', '(11) 96666-6666', 4, '$2a$10$gI0mWhw4WoGjt3kKtSLdGe8s/rduhqbNGYgOQqhBgE3mvS1oL9N3u', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);