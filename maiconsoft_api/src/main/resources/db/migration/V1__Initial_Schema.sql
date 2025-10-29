-- ================================================
-- MAICONSOFT API - SCHEMA INICIAL
-- Migration V1 - Criação das tabelas principais
-- ================================================

-- Criação da tabela de roles de usuário
CREATE TABLE user_roles (
    id_role SERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

-- Criação da tabela de usuários
CREATE TABLE users (
    id_user BIGSERIAL PRIMARY KEY,
    codigo_acesso VARCHAR(6) NOT NULL UNIQUE,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    telefone VARCHAR(20),
    id_role INTEGER NOT NULL REFERENCES user_roles(id_role),
    senha VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Criação da tabela de clientes
CREATE TABLE clientes (
    id_cliente BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL UNIQUE,
    loja VARCHAR(5) NOT NULL,
    razao_social VARCHAR(150) NOT NULL,
    tipo VARCHAR(1) NOT NULL CHECK (tipo IN ('F', 'J')), -- F=Física, J=Jurídica
    nome_fantasia VARCHAR(150),
    finalidade VARCHAR(1) CHECK (finalidade IN ('C', 'F', 'R')), -- C=Consumidor, F=Fornecedor, R=Revenda
    cpf_cnpj VARCHAR(14) NOT NULL,
    cep VARCHAR(10),
    pais VARCHAR(50) DEFAULT 'Brasil',
    estado VARCHAR(2),
    cod_municipio VARCHAR(20),
    cidade VARCHAR(100),
    endereco VARCHAR(200),
    bairro VARCHAR(100),
    ddd VARCHAR(5),
    telefone VARCHAR(20),
    abertura DATE,
    contato VARCHAR(100),
    email VARCHAR(100),
    homepage VARCHAR(100),
    datahora_cadastro TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    descricao TEXT,
    id_usuario_cadastro BIGINT REFERENCES users(id_user),
    
    -- Constraints adicionais
    CONSTRAINT chk_cpf_cnpj_length CHECK (
        (tipo = 'F' AND LENGTH(cpf_cnpj) = 11) OR 
        (tipo = 'J' AND LENGTH(cpf_cnpj) = 14)
    )
);

-- Criação da tabela de cupons
CREATE TABLE cupom (
    id_cupom BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nome VARCHAR(100),
    descricao VARCHAR(150),
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO' CHECK (status IN ('ATIVO', 'INATIVO', 'EXPIRADO')),
    desconto_percentual DECIMAL(5,2) CHECK (desconto_percentual >= 0 AND desconto_percentual <= 100),
    desconto_valor DECIMAL(10,2) CHECK (desconto_valor >= 0),
    valor_minimo DECIMAL(10,2) DEFAULT 0,
    validade DATE,
    max_usos INTEGER DEFAULT 1,
    usos_atual INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraint para garantir que pelo menos um tipo de desconto seja definido
    CONSTRAINT chk_desconto CHECK (desconto_percentual IS NOT NULL OR desconto_valor IS NOT NULL)
);

-- Criação da tabela de vendas
CREATE TABLE vendas (
    id_venda BIGSERIAL PRIMARY KEY,
    numero_orcamento VARCHAR(20) NOT NULL UNIQUE,
    id_cliente BIGINT NOT NULL REFERENCES clientes(id_cliente),
    id_cupom BIGINT REFERENCES cupom(id_cupom),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE' CHECK (status IN ('PENDENTE', 'CONFIRMADA', 'CANCELADA', 'FINALIZADA')),
    valor_bruto DECIMAL(10,2) NOT NULL CHECK (valor_bruto >= 0),
    valor_desconto DECIMAL(10,2) DEFAULT 0 CHECK (valor_desconto >= 0),
    valor_total DECIMAL(10,2) NOT NULL CHECK (valor_total >= 0),
    data_venda DATE DEFAULT CURRENT_DATE,
    datahora_cadastro TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    observacao TEXT,
    id_usuario_cadastro BIGINT REFERENCES users(id_user),
    
    -- Constraint para validar valor total
    CONSTRAINT chk_valor_total CHECK (valor_total = valor_bruto - COALESCE(valor_desconto, 0))
);

-- Criação da tabela de serviços
CREATE TABLE servico (
    id_servico BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao TEXT,
    categoria VARCHAR(50),
    preco_base DECIMAL(10,2) NOT NULL CHECK (preco_base >= 0),
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO' CHECK (status IN ('ATIVO', 'INATIVO')),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Criação da tabela de pagamentos
CREATE TABLE pagamentos (
    id_pagamento BIGSERIAL PRIMARY KEY,
    id_venda BIGINT NOT NULL REFERENCES vendas(id_venda) ON DELETE CASCADE,
    forma_pagamento VARCHAR(50) NOT NULL CHECK (forma_pagamento IN ('DINHEIRO', 'CARTAO_CREDITO', 'CARTAO_DEBITO', 'PIX', 'TRANSFERENCIA', 'BOLETO')),
    valor DECIMAL(10,2) NOT NULL CHECK (valor > 0),
    data_pagamento TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    
    -- Índice para melhorar performance nas consultas
    INDEX idx_pagamentos_venda (id_venda),
    INDEX idx_pagamentos_data (data_pagamento)
);

-- Criação da tabela de logs do dashboard (auditoria)
CREATE TABLE dashboard_log (
    id_log BIGSERIAL PRIMARY KEY,
    data_hora TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    acao VARCHAR(100) NOT NULL,
    entidade VARCHAR(50),
    entidade_id BIGINT,
    usuario_id VARCHAR(50),
    id_usuario BIGINT REFERENCES users(id_user),
    ip_origem VARCHAR(45),
    user_agent VARCHAR(500),
    descricao TEXT,
    metadados TEXT, -- JSON para dados adicionais
    
    -- Índices para melhorar performance
    INDEX idx_dashboard_log_data (data_hora),
    INDEX idx_dashboard_log_usuario (id_usuario),
    INDEX idx_dashboard_log_entidade (entidade, entidade_id)
);

-- ================================================
-- ÍNDICES ADICIONAIS PARA PERFORMANCE
-- ================================================

-- Índices para tabela de clientes
CREATE INDEX idx_clientes_codigo ON clientes(codigo);
CREATE INDEX idx_clientes_cpf_cnpj ON clientes(cpf_cnpj);
CREATE INDEX idx_clientes_email ON clientes(email);
CREATE INDEX idx_clientes_tipo ON clientes(tipo);
CREATE INDEX idx_clientes_cadastro ON clientes(datahora_cadastro);

-- Índices para tabela de usuários
CREATE INDEX idx_users_codigo_acesso ON users(codigo_acesso);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_cpf ON users(cpf);

-- Índices para tabela de vendas
CREATE INDEX idx_vendas_cliente ON vendas(id_cliente);
CREATE INDEX idx_vendas_status ON vendas(status);
CREATE INDEX idx_vendas_data ON vendas(data_venda);
CREATE INDEX idx_vendas_orcamento ON vendas(numero_orcamento);

-- Índices para tabela de cupons
CREATE INDEX idx_cupom_codigo ON cupom(codigo);
CREATE INDEX idx_cupom_status ON cupom(status);
CREATE INDEX idx_cupom_validade ON cupom(validade);

-- ================================================
-- COMENTÁRIOS NAS TABELAS E COLUNAS
-- ================================================

COMMENT ON TABLE user_roles IS 'Roles/perfis de usuários do sistema';
COMMENT ON TABLE users IS 'Usuários do sistema';
COMMENT ON TABLE clientes IS 'Cadastro de clientes (pessoas físicas e jurídicas)';
COMMENT ON TABLE cupom IS 'Cupons de desconto para vendas';
COMMENT ON TABLE vendas IS 'Registro de vendas e orçamentos';
COMMENT ON TABLE servico IS 'Catálogo de serviços oferecidos';
COMMENT ON TABLE pagamentos IS 'Registro de pagamentos das vendas';
COMMENT ON TABLE dashboard_log IS 'Log de auditoria do sistema';

COMMENT ON COLUMN clientes.tipo IS 'F = Pessoa Física, J = Pessoa Jurídica';
COMMENT ON COLUMN clientes.finalidade IS 'C = Consumidor Final, F = Fornecedor, R = Revenda';
COMMENT ON COLUMN vendas.status IS 'Status da venda: PENDENTE, CONFIRMADA, CANCELADA, FINALIZADA';
COMMENT ON COLUMN cupom.status IS 'Status do cupom: ATIVO, INATIVO, EXPIRADO';