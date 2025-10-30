# 🗄️ Configuração de Banco - Maiconsoft

## 📋 **Arquivos de Configuração**

### ✅ **Arquivos Ativos:**
- **`application.properties`** - Configuração principal (aponta para Supabase)
- **`application-supabase.properties`** - Banco hospedado (PRINCIPAL)
- **`application-postgres.properties`** - PostgreSQL local (BACKUP)
- **`application-example.properties`** - Template (não usar)

### ❌ **Arquivos Removidos:**
- ~~`application-dev.properties`~~ - Removido (era H2)
- ~~`application-prod.properties`~~ - Removido (Supabase é produção)
- ~~`application-dev.zip`~~ - Removido (backup desnecessário)

## 🚀 **Como Usar**

### **Cenário 1: Supabase Funcionando (PADRÃO)**
```bash
# Simplesmente execute (usa Supabase por padrão)
mvn spring-boot:run

# Ou no IDE, deixe: spring.profiles.active=supabase
```

### **Cenário 2: Sem Acesso ao Supabase (DNS/IPv4)**
```bash
# Use PostgreSQL local como backup
mvn spring-boot:run -Dspring-boot.run.profiles=postgres

# Ou no IDE, mude para: spring.profiles.active=postgres
```

## 🔧 **Configuração Rápida**

### **Para usar PostgreSQL Local:**

1. **Criar banco:**
   ```sql
   CREATE DATABASE maiconsoft_dev;
   ```

2. **Executar aplicação:**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=postgres
   ```

3. **Verificar:**
   - Migrações V1-V8 executarão automaticamente
   - Dados de exemplo serão carregados

## 🎯 **Troca Rápida no VS Code**

**Em `application.properties`, altere a linha:**

```properties
# Para Supabase (padrão)
spring.profiles.active=supabase

# Para PostgreSQL local (backup)
spring.profiles.active=postgres
```

## ✅ **Resumo**

- **🌐 Supabase:** Principal, sempre funcionando
- **🏠 PostgreSQL Local:** Backup para problemas de rede
- **🔄 Troca fácil:** Apenas uma linha de configuração

**Pronto para funcionar em qualquer situação! 🎉**