-- ===============================
-- V12: ADD COMPROVANTE FIELDS TO VENDAS
-- ===============================
-- Adiciona campos para anexo de comprovante nas vendas

-- ===============================
-- 1. ADICIONAR COLUNAS DE COMPROVANTE
-- ===============================
-- Adicionar coluna para caminho do arquivo do comprovante
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'vendas' AND column_name = 'comprovante_path'
    ) THEN
        ALTER TABLE vendas ADD COLUMN comprovante_path VARCHAR(255);
    END IF;
END
$$;

-- Adicionar coluna para data de upload do comprovante
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'vendas' AND column_name = 'comprovante_upload_date'
    ) THEN
        ALTER TABLE vendas ADD COLUMN comprovante_upload_date TIMESTAMP WITH TIME ZONE;
    END IF;
END
$$;

-- ===============================
-- 2. COMENTÁRIOS PARA DOCUMENTAÇÃO
-- ===============================
COMMENT ON COLUMN vendas.comprovante_path IS 'Caminho do arquivo de comprovante da venda';
COMMENT ON COLUMN vendas.comprovante_upload_date IS 'Data e hora do upload do comprovante';

-- ===============================
-- 3. ATUALIZAR STATUS DAS VENDAS EXISTENTES
-- ===============================
-- Vendas sem comprovante ficam como PENDENTE
-- Vendas com comprovante já anexado (se houver) ficam como CONFIRMADA
UPDATE vendas 
SET status = 'PENDENTE' 
WHERE status = 'FINALIZADA' AND comprovante_path IS NULL;

-- ===============================
-- 4. INFORMAÇÕES SOBRE A MIGRAÇÃO
-- ===============================
-- Esta migração adiciona suporte para anexo de comprovantes nas vendas
-- Regras implementadas:
-- - Venda sem comprovante = STATUS 'PENDENTE'
-- - Venda com comprovante = STATUS 'CONFIRMADA'
-- - Upload de comprovante altera status automaticamente