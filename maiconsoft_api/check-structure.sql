-- ============================================
-- Verificar estrutura atual do banco PostgreSQL
-- SUPABASE DATABASE - Maiconsoft API
-- ============================================

-- Verificar se estamos conectados ao banco correto
SELECT current_database() as banco_atual, current_user as usuario_atual;

-- Verificar todas as tabelas existentes
SELECT 
    table_name as "Nome da Tabela",
    table_type as "Tipo"
FROM information_schema.tables 
WHERE table_schema = 'public'
ORDER BY table_name;

-- Verificar estrutura da tabela DASHBOARD_LOG (se existir)
SELECT 
    column_name as "Coluna",
    data_type as "Tipo",
    character_maximum_length as "Tamanho",
    is_nullable as "Permite NULL",
    column_default as "Valor Padrão"
FROM information_schema.columns
WHERE table_name = 'dashboard_log'
  AND table_schema = 'public'
ORDER BY ordinal_position;

-- Verificar estrutura da tabela USERS (se existir)
SELECT 
    column_name as "Coluna",
    data_type as "Tipo",
    character_maximum_length as "Tamanho",
    is_nullable as "Permite NULL",
    column_default as "Valor Padrão"
FROM information_schema.columns
WHERE table_name = 'users'
  AND table_schema = 'public'
ORDER BY ordinal_position;

-- Verificar estrutura da tabela CLIENTES (se existir)
SELECT 
    column_name as "Coluna",
    data_type as "Tipo",
    character_maximum_length as "Tamanho",
    is_nullable as "Permite NULL",
    column_default as "Valor Padrão"
FROM information_schema.columns
WHERE table_name = 'clientes'
  AND table_schema = 'public'
ORDER BY ordinal_position;

-- Verificar todas as constraints (chaves estrangeiras, etc.)
SELECT 
    tc.table_name as "Tabela",
    tc.constraint_name as "Nome da Constraint",
    tc.constraint_type as "Tipo",
    kcu.column_name as "Coluna"
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu 
    ON tc.constraint_name = kcu.constraint_name
WHERE tc.table_schema = 'public'
ORDER BY tc.table_name, tc.constraint_type;

-- Verificar índices existentes
SELECT 
    schemaname as "Schema",
    tablename as "Tabela",
    indexname as "Nome do Índice",
    indexdef as "Definição"
FROM pg_indexes
WHERE schemaname = 'public'
ORDER BY tablename, indexname;
