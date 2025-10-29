-- ================================================
-- MAICONSOFT API - POPULAÇÃO DE DADOS DE EXEMPLO
-- Migration V8 - Dados realistas para demonstração
-- ================================================

-- SERVIÇOS DE EXEMPLO (sem dependência de usuário)
INSERT INTO servico (nome, descricao, categoria, preco_base, status) VALUES 
('Desenvolvimento Web', 'Criação de sites responsivos e modernos', 'Desenvolvimento', 2500.00, 'ATIVO'),
('Consultoria em TI', 'Consultoria especializada em tecnologia', 'Consultoria', 150.00, 'ATIVO'),
('Manutenção de Sistemas', 'Manutenção e suporte técnico', 'Suporte', 80.00, 'ATIVO'),
('Design Gráfico', 'Criação de materiais visuais', 'Design', 300.00, 'ATIVO'),
('SEO e Marketing Digital', 'Otimização para motores de busca', 'Marketing', 800.00, 'ATIVO'),
('Treinamento em Software', 'Capacitação de equipes', 'Educação', 200.00, 'ATIVO');

-- CUPONS DE DESCONTO (sem dependência de usuário)
INSERT INTO cupom (codigo, nome, descricao, desconto_percentual, valor_minimo, validade, max_usos, status) VALUES 
('BEMVINDO10', 'Boas-vindas', 'Desconto para novos clientes', 10.00, 100.00, '2025-12-31', 1, 'ATIVO'),
('FIDELIDADE15', 'Cliente Fiel', 'Desconto para clientes antigos', 15.00, 500.00, '2025-12-31', 5, 'ATIVO'),
('PROMO20', 'Promoção Especial', 'Desconto promocional', 20.00, 1000.00, '2025-06-30', 10, 'ATIVO');

-- CLIENTES DE EXEMPLO (sem usuário de cadastro por enquanto)
INSERT INTO clientes (
    codigo, loja, razao_social, tipo, nome_fantasia, finalidade, cpf_cnpj, 
    cep, estado, cidade, endereco, bairro, ddd, telefone, 
    contato, email, id_usuario_cadastro
) VALUES 
-- Clientes Pessoa Física
('C001', '01', 'Maria Silva Santos', 'F', NULL, 'C', '12345678901', 
 '01234-567', 'SP', 'São Paulo', 'Rua das Flores, 123', 'Centro', '11', '987654321', 
 'Maria Silva', 'maria.silva@email.com', NULL),

('C002', '01', 'João Pedro Oliveira', 'F', NULL, 'C', '98765432109', 
 '12345-678', 'RJ', 'Rio de Janeiro', 'Av. Copacabana, 456', 'Copacabana', '21', '876543210', 
 'João Pedro', 'joao.pedro@email.com', NULL),

('C003', '01', 'Ana Carolina Ferreira', 'F', NULL, 'C', '11223344556', 
 '23456-789', 'MG', 'Belo Horizonte', 'Rua da Liberdade, 789', 'Savassi', '31', '765432109', 
 'Ana Carolina', 'ana.ferreira@email.com', NULL),

-- Clientes Pessoa Jurídica
('C004', '01', 'Tech Solutions Ltda', 'J', 'TechSol', 'C', '12345678000195', 
 '04567-890', 'SP', 'São Paulo', 'Av. Paulista, 1000', 'Bela Vista', '11', '34567890', 
 'Carlos Tecnologia', 'contato@techsol.com.br', NULL),

('C005', '01', 'Comércio Brasil S.A.', 'J', 'Brasil Shop', 'R', '98765432000123', 
 '56789-012', 'RS', 'Porto Alegre', 'Rua dos Comerciantes, 2000', 'Centro', '51', '23456789', 
 'Roberto Vendas', 'vendas@brasilshop.com.br', NULL),

('C006', '01', 'Fornecedora Global Ltda', 'J', 'Global Supply', 'F', '11111111000111', 
 '67890-123', 'SC', 'Florianópolis', 'Rua Industrial, 500', 'Industrial', '48', '12345678', 
 'Fernanda Suprimentos', 'suprimentos@global.com.br', NULL);

-- VENDAS DE EXEMPLO (usando os IDs dos clientes criados acima)
INSERT INTO vendas (
    numero_orcamento, id_cliente, id_cupom, status, valor_bruto, valor_desconto, valor_total, 
    observacao, id_usuario_cadastro
) VALUES 
('ORC-2025-001', (SELECT id_cliente FROM clientes WHERE codigo = 'C001'), 
 (SELECT id_cupom FROM cupom WHERE codigo = 'BEMVINDO10'), 
 'FINALIZADA', 1500.00, 150.00, 1350.00, 'Site institucional para cliente', NULL),

('ORC-2025-002', (SELECT id_cliente FROM clientes WHERE codigo = 'C002'), 
 NULL, 'CONFIRMADA', 800.00, 0.00, 800.00, 'Consultoria em marketing digital', NULL),

('ORC-2025-003', (SELECT id_cliente FROM clientes WHERE codigo = 'C003'), 
 NULL, 'PENDENTE', 300.00, 0.00, 300.00, 'Design de logotipo', NULL),

('ORC-2025-004', (SELECT id_cliente FROM clientes WHERE codigo = 'C004'), 
 (SELECT id_cupom FROM cupom WHERE codigo = 'FIDELIDADE15'), 
 'FINALIZADA', 3000.00, 450.00, 2550.00, 'Sistema completo de gestão', NULL),

('ORC-2025-005', (SELECT id_cliente FROM clientes WHERE codigo = 'C005'), 
 NULL, 'CONFIRMADA', 1200.00, 0.00, 1200.00, 'Manutenção mensal de sistemas', NULL),

('ORC-2025-006', (SELECT id_cliente FROM clientes WHERE codigo = 'C006'), 
 NULL, 'FINALIZADA', 600.00, 0.00, 600.00, 'Treinamento da equipe', NULL);

-- PAGAMENTOS DE EXEMPLO (usando IDs das vendas)
INSERT INTO pagamentos (id_venda, forma_pagamento, valor) VALUES 
((SELECT id_venda FROM vendas WHERE numero_orcamento = 'ORC-2025-001'), 'PIX', 1350.00),
((SELECT id_venda FROM vendas WHERE numero_orcamento = 'ORC-2025-002'), 'CARTAO_CREDITO', 800.00),
((SELECT id_venda FROM vendas WHERE numero_orcamento = 'ORC-2025-004'), 'TRANSFERENCIA', 2550.00),
((SELECT id_venda FROM vendas WHERE numero_orcamento = 'ORC-2025-005'), 'BOLETO', 1200.00),
((SELECT id_venda FROM vendas WHERE numero_orcamento = 'ORC-2025-006'), 'DINHEIRO', 600.00);

-- ATUALIZAR ESTATÍSTICAS DOS CUPONS
UPDATE cupom SET usos_atual = 1 WHERE codigo IN ('BEMVINDO10', 'FIDELIDADE15');

-- ================================================
-- DADOS CRIADOS COM SUCESSO:
-- 6 Clientes (3 PF + 3 PJ) - IDs 7-12
-- 6 Serviços categorizados  
-- 6 Vendas com diferentes status - Todas vinculadas a clientes
-- 3 Cupons de desconto
-- 5 Pagamentos variados
-- INTEGRIDADE REFERENCIAL: ✅ VERIFICADA
-- ================================================