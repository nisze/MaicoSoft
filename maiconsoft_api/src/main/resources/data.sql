-- ===============================
-- DADOS INICIAIS PARA DESENVOLVIMENTO
-- ===============================

-- Inserir roles padrão
INSERT INTO USER_ROLES (ROLE_NAME) VALUES ('ADMIN');
INSERT INTO USER_ROLES (ROLE_NAME) VALUES ('FUNCIONARIO');
INSERT INTO USER_ROLES (ROLE_NAME) VALUES ('DIRETOR');

-- Inserir usuário administrador padrão
INSERT INTO USERS (NOME, EMAIL, CPF, TELEFONE, SENHA, CODIGO_ACESSO, ID_ROLE) 
VALUES ('Administrador', 'admin@maiconsoft.com', '12345678901', '11999999999', 
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8inED.b8kKo2x9zJka', -- senha: admin123
        'ADM001', 1);

-- Inserir clientes de exemplo
INSERT INTO CLIENTES (CODIGO, RAZAO_SOCIAL, NOME_FANTASIA, TIPO, CPF_CNPJ, EMAIL, TELEFONE, 
                     ENDERECO, BAIRRO, CIDADE, ESTADO, CEP, LOJA, DATAHORA_CADASTRO, ID_USUARIO_CADASTRO)
VALUES 
('CLI001', 'João Silva Santos', 'João Silva', 'F', '12345678901', 'joao@email.com', '11987654321',
 'Rua das Flores, 123', 'Centro', 'São Paulo', 'SP', '01234567', 'LOJA1', CURRENT_TIMESTAMP, 1),
 
('CLI002', 'Maria Oliveira Ltda', 'Maria Tech', 'J', '12345678000123', 'maria@empresatech.com', '11876543210',
 'Av. Paulista, 456', 'Bela Vista', 'São Paulo', 'SP', '01310100', 'LOJA1', CURRENT_TIMESTAMP, 1),
 
('CLI003', 'Pedro Santos', 'Pedro Santos', 'F', '98765432109', 'pedro@email.com', '11765432109',
 'Rua Augusta, 789', 'Consolação', 'São Paulo', 'SP', '01305000', 'LOJA1', CURRENT_TIMESTAMP, 1);

-- Inserir cupons de exemplo
INSERT INTO CUPOM (CODIGO, NOME, DESCRICAO, DESCONTO_PERCENTUAL, DESCONTO_VALOR, VALOR_MINIMO, 
                   MAX_USOS, USOS_ATUAL, STATUS, VALIDADE)
VALUES 
('DESC10', 'Desconto 10%', 'Desconto de 10% para clientes especiais', 10.0, NULL, 100.0, 100, 0, 'ATIVO', '2025-12-31'),
('NOVO50', 'Novo Cliente', 'R$ 50 de desconto para novos clientes', NULL, 50.0, 200.0, 50, 0, 'ATIVO', '2025-12-31'),
('BLACK20', 'Black Friday', 'Desconto especial Black Friday', 20.0, NULL, 300.0, 200, 15, 'ATIVO', '2025-11-30');

-- Inserir vendas de exemplo
INSERT INTO VENDAS (NUMERO_ORCAMENTO, DATA_VENDA, VALOR_BRUTO, VALOR_DESCONTO, VALOR_TOTAL, 
                   STATUS, OBSERVACAO, ID_CLIENTE, ID_CUPOM, ID_USUARIO_CADASTRO, DATAHORA_CADASTRO)
VALUES 
('ORC001', '2025-10-01', 1500.00, 150.00, 1350.00, 'FINALIZADA', 'Venda realizada com sucesso', 1, 1, 1, CURRENT_TIMESTAMP),
('ORC002', '2025-10-15', 800.00, 0.00, 800.00, 'ATIVA', 'Aguardando pagamento', 2, NULL, 1, CURRENT_TIMESTAMP),
('ORC003', '2025-10-20', 2200.00, 440.00, 1760.00, 'FINALIZADA', 'Cliente utilizou cupom Black Friday', 3, 3, 1, CURRENT_TIMESTAMP);

-- Inserir logs de auditoria de exemplo
INSERT INTO DASHBOARD_LOG (USUARIO_ID, ID_USUARIO, ACAO, DESCRICAO, ENTIDADE, ENTIDADE_ID, 
                          DATA_HORA, IP_ORIGEM, USER_AGENT)
VALUES 
('admin@maiconsoft.com', 1, 'LOGIN', 'Usuário logou no sistema', 'USER', 1, CURRENT_TIMESTAMP, '127.0.0.1', 'Mozilla/5.0'),
('admin@maiconsoft.com', 1, 'CREATE_CLIENTE', 'Novo cliente cadastrado', 'CLIENTE', 1, CURRENT_TIMESTAMP, '127.0.0.1', 'Mozilla/5.0'),
('admin@maiconsoft.com', 1, 'CREATE_VENDA', 'Nova venda registrada', 'VENDA', 1, CURRENT_TIMESTAMP, '127.0.0.1', 'Mozilla/5.0');