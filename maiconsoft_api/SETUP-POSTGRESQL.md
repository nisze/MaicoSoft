# 🐘 Setup PostgreSQL Local - Maiconsoft

## 📋 Análise das Migrações

✅ **TODAS AS 8 MIGRAÇÕES ESTÃO PRONTAS PARA POSTGRESQL!**

### Migrações Analisadas:
- **V1__Initial_Schema.sql** - Schema inicial com sintaxe PostgreSQL (SERIAL, BIGSERIAL)
- **V2__Insert_Initial_Data.sql** - Dados iniciais com ON CONFLICT
- **V3__Add_Unique_Constraints.sql** - Constraints únicos
- **V4__Add_Foreign_Keys.sql** - Chaves estrangeiras
- **V5__Add_Check_Constraints.sql** - Check constraints
- **V6__Add_Indexes.sql** - Índices para performance
- **V7__Add_Triggers.sql** - Triggers de auditoria
- **V8__Popular_Dados_Exemplo.sql** - Dados de exemplo

**✅ Confirmado:** Todas usam sintaxe compatível com PostgreSQL e serão executadas automaticamente pelo Flyway quando você executar a aplicação.

## 🚀 Setup Rápido

### 1. Instalar PostgreSQL
```bash
# Windows - Download do site oficial
https://www.postgresql.org/download/windows/

# Ou via Chocolatey
choco install postgresql

# Ou via Docker
docker run --name maiconsoft-postgres -e POSTGRES_PASSWORD=admin123 -p 5432:5432 -d postgres:15
```

### 2. Criar Banco de Dados

**Opção A - Via psql:**
```sql
-- Conectar como postgres
psql -U postgres

-- Criar banco
CREATE DATABASE maiconsoft_dev;

-- Criar usuário (opcional)
CREATE USER maiconsoft_user WITH PASSWORD 'maiconsoft_dev_2025';
GRANT ALL PRIVILEGES ON DATABASE maiconsoft_dev TO maiconsoft_user;

-- Sair
\q
```

**Opção B - Via Docker:**
```bash
# Se usando Docker, o banco será criado automaticamente
docker exec -it maiconsoft-postgres psql -U postgres -c "CREATE DATABASE maiconsoft_dev;"
```

### 3. Configurar Aplicação

**Usar o arquivo já criado:**
```bash
# Executar com perfil PostgreSQL
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

**Ou copiar configuração:**
```bash
# Copiar template
cp application-example.properties application-postgres.properties
```

### 4. Executar Aplicação

```bash
# Com PostgreSQL local
mvn spring-boot:run -Dspring-boot.run.profiles=postgres

# Ou com perfil dev (se configurado para PostgreSQL)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 🔍 Verificações

### Verificar Migrações Executadas:
```sql
-- Conectar ao banco
psql -U postgres -d maiconsoft_dev

-- Ver histórico de migrações
SELECT installed_rank, version, description, type, script, installed_on, success 
FROM flyway_schema_history 
ORDER BY installed_rank;
```

### Verificar Tabelas Criadas:
```sql
-- Listar tabelas
\dt

-- Ver estrutura de uma tabela
\d usuarios
\d clientes
\d vendas
```

### Verificar Dados de Exemplo:
```sql
-- Ver usuários
SELECT id, nome, email, role FROM usuarios;

-- Ver clientes
SELECT id, nome, email, telefone FROM clientes LIMIT 5;

-- Ver vendas
SELECT v.id, c.nome as cliente, v.total, v.data_venda 
FROM vendas v 
JOIN clientes c ON v.cliente_id = c.id 
LIMIT 5;
```

## 📦 Configurações Importantes

### application-postgres.properties
```properties
# Banco PostgreSQL Local
spring.datasource.url=jdbc:postgresql://localhost:5432/maiconsoft_dev
spring.datasource.username=postgres
spring.datasource.password=admin123
spring.datasource.driver-class-name=org.postgresql.Driver

# Flyway - Migrações automáticas
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
```

## 🐞 Troubleshooting

### Erro de Conexão:
```bash
# Verificar se PostgreSQL está rodando
psql -U postgres -c "SELECT version();"

# No Windows, verificar serviço
Get-Service postgresql*
```

### Erro de Migração:
```bash
# Limpar esquema Flyway (cuidado!)
psql -U postgres -d maiconsoft_dev -c "DROP TABLE IF EXISTS flyway_schema_history CASCADE;"

# Recriar banco
psql -U postgres -c "DROP DATABASE IF EXISTS maiconsoft_dev;"
psql -U postgres -c "CREATE DATABASE maiconsoft_dev;"
```

### Logs Detalhados:
```properties
# Adicionar ao application-postgres.properties para debug
logging.level.org.flywaydb=DEBUG
logging.level.org.springframework.jdbc=DEBUG
```

## ✅ Checklist Final

- [ ] PostgreSQL instalado e rodando
- [ ] Banco `maiconsoft_dev` criado
- [ ] Arquivo `application-postgres.properties` configurado
- [ ] Aplicação executada com perfil PostgreSQL
- [ ] 8 migrações executadas com sucesso
- [ ] Dados de exemplo carregados
- [ ] Endpoints testados via Postman/navegador

## 🎯 Resumo

**Todas as suas migrações estão 100% prontas para PostgreSQL!** 

Quando você executar a aplicação pela primeira vez com PostgreSQL:
1. Flyway detectará o banco vazio
2. Executará automaticamente as 8 migrações em ordem (V1 → V8)
3. Criará todas as tabelas, índices, constraints e triggers
4. Populará com dados iniciais e exemplos
5. Aplicação ficará pronta para uso

**Comando para executar:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

🎉 **Pronto para desenvolvimento local com PostgreSQL!**