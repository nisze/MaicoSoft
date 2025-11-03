# 🐳 MAICONSOFT - DOCKER DEPLOYMENT

Sistema completo containerizado com Docker para fácil deploy e desenvolvimento.

## 🎯 DUAS CONFIGURAÇÕES DISPONÍVEIS

### 🏠 **EM CASA** → Docker + Supabase (Recomendado)
- Banco de dados **Supabase** (PostgreSQL na nuvem)
- Ideal para desenvolvimento pessoal
- Dados persistem na nuvem

### 🎓 **NA FACULDADE** → Docker + PostgreSQL Local
- Banco **PostgreSQL** rodando em container
- Funciona sem internet
- Dados ficam no container local

## 🚀 EXECUÇÃO RÁPIDA

```bash
# 🏠 PARA USAR EM CASA (Supabase)
docker-casa.bat

# 🎓 PARA USAR NA FACULDADE (PostgreSQL local)
docker-faculdade.bat
```

> ✅ **Simples assim!** Os scripts automatizam tudo.

## 📋 Pré-requisitos

- **Docker Desktop** (Windows/Mac) ou **Docker Engine** (Linux)
- **Docker Compose** (geralmente incluído no Docker Desktop)
- **Git** (para clonar o repositório)

### ⚠️ ATENÇÃO USUÁRIOS WINDOWS

Se você tiver **problemas de conectividade** com Supabase no Docker Desktop:
1. **Primeiro teste:** Execute `./docker-diagnostico-windows.bat`
2. **Se falhar:** Use `./docker-alternativo-windows.bat` (modo sem Docker)
3. **Causas comuns:** Firewall, proxy corporativo, configuração WSL2

> 🔧 **Solução rápida:** A maioria dos problemas é resolvida reiniciando o Docker Desktop

## 📥 Para Quem Vai Clonar o Projeto

### 🔑 Informações Essenciais:

**1. Senha do Supabase (OBRIGATÓRIA):**
```
SUPABASE_PASSWORD=CkTMz5oUISI5gIUn
```

**2. Configurações de Email (JÁ CONFIGURADAS):**
O projeto já vem com credenciais de email configuradas no `application.properties`:
```
spring.mail.username=empresamaiconsoft@gmail.com
spring.mail.password=cvjznokkvtzuaqzm
```
> ✅ **Sistema de email funciona automaticamente!**

**3. Configurações Opcionais (.env):**
```
# Se quiser usar suas próprias credenciais de email
MAIL_USERNAME=seu-email@gmail.com
MAIL_PASSWORD=sua-senha-de-app-do-gmail
```

### 🚀 Passo a Passo Rápido:

**OPÇÃO 1 - Casa (Supabase):**
```bash
# 1. Clonar o repositório
git clone https://github.com/nisze/MaicoSoft.git
cd MaicoSoft

# 2. Copiar arquivo de ambiente
cp .env.example .env

# 3. Executar em casa
docker-casa.bat
```

**OPÇÃO 2 - Faculdade (PostgreSQL local):**
```bash
# 1. Clonar o repositório
git clone https://github.com/nisze/MaicoSoft.git
cd MaicoSoft

# 2. Executar na faculdade
docker-faculdade.bat
```

**Acessos:**
- 🌐 **Frontend:** http://localhost
- 📡 **Backend:** http://localhost:8090
- 📖 **Swagger:** http://localhost:8090/swagger-ui.html

> ✅ **Pronto!** O projeto funciona em qualquer lugar!

## 🔄 DETALHES DAS CONFIGURAÇÕES

### 🏠 **docker-casa.bat** (Supabase)
- **Arquivo:** `docker-compose.supabase.yml`
- **Banco:** Supabase PostgreSQL (nuvem)
- **Vantagens:** 
  - ✅ Dados persistem entre execuções
  - ✅ Acesso remoto aos dados
  - ✅ Backup automático
- **Requisitos:** Internet para conectar com Supabase

### 🎓 **docker-faculdade.bat** (PostgreSQL Local)
- **Arquivo:** `docker-compose.local.yml`
- **Banco:** PostgreSQL 15 em container
- **Vantagens:**
  - ✅ Funciona offline
  - ✅ Performance local
  - ✅ Controle total dos dados
- **Credenciais:**
  ```
  Host: localhost:5432
  Database: maiconsoft
  User: postgres
  Password: postgres123
  ```

## 🛠️ COMANDOS MANUAIS (Opcional)

Se preferir executar manualmente:

**Para Casa (Supabase):**
```bash
# Parar versão local (se estiver rodando)
docker-compose -f docker-compose.local.yml down

# Iniciar versão Supabase
docker-compose -f docker-compose.supabase.yml up --build -d
```

**Para Faculdade (PostgreSQL local):**
```bash
# Parar versão Supabase (se estiver rodando)
docker-compose -f docker-compose.supabase.yml down

# Iniciar versão local
docker-compose -f docker-compose.local.yml up --build -d
```

**Comandos úteis:**
```bash
# Ver logs
docker-compose -f docker-compose.local.yml logs -f
docker-compose -f docker-compose.supabase.yml logs -f

# Parar tudo
docker-compose -f docker-compose.local.yml down
docker-compose -f docker-compose.supabase.yml down
```

## ⚠️ ATENÇÃO: Docker não instalado no Windows

**SITUAÇÃO DETECTADA:** Docker/Docker Desktop não está instalado neste sistema.

### 📋 Opções disponíveis:

**Opção 1 - Instalar Docker Desktop (Recomendado):**
1. Baixe: https://www.docker.com/products/docker-desktop/
2. Instale Docker Desktop
3. Reinicie o computador
4. Execute: `resolver-docker-windows.bat`

**Opção 2 - Executar Modo Local (IMEDIATO):**
```bash
# Executar sem Docker
cd maiconsoft_api
./run-with-supabase.bat
```

**Opção 3 - Frontend + Backend Separados:**
```bash
# Terminal 1 - Backend
cd maiconsoft_api
./run-with-supabase.bat

# Terminal 2 - Frontend (servidor local)
cd frontend
python -m http.server 3000
# ou
npx serve -s . -l 3000
```

> ✅ **RECOMENDAÇÃO:** Use a **Opção 2** para testar imediatamente.

### 1. Configurar Variáveis de Ambiente

```bash
# Copiar arquivo de exemplo
cp .env.example .env

# Editar variáveis (OBRIGATÓRIO)
# Windows: notepad .env
# Linux/Mac: nano .env
```

**Variáveis obrigatórias no .env:**
```bash
SUPABASE_PASSWORD=CkTMz5oUISI5gIUn
```

> ⚠️ **IMPORTANTE:** Use exatamente esta senha para o projeto funcionar corretamente!

### 2. Iniciar Aplicação

**Windows:**
```bash
# Executar script automatizado
docker-start.bat

# OU manualmente
docker-compose up --build -d
```

**Linux/Mac:**
```bash
# Dar permissão ao script
chmod +x docker-start.sh

# Executar script automatizado
./docker-start.sh

# OU manualmente
docker-compose up --build -d
```

### 3. Acessar Aplicação

- 🌐 **Frontend:** http://localhost
- 📡 **Backend API:** http://localhost:8090
- 📖 **Swagger UI:** http://localhost:8090/swagger-ui.html

## 🛠️ Comandos Úteis

### Gerenciamento Básico
```bash
# Iniciar containers
docker-compose up -d

# Parar containers
docker-compose down

# Reiniciar containers
docker-compose restart

# Ver status
docker-compose ps
```

### Logs e Debugging
```bash
# Ver logs de todos os serviços
docker-compose logs -f

# Ver logs específicos
docker-compose logs -f maiconsoft-api
docker-compose logs -f maiconsoft-frontend

# Entrar no container do backend
docker-compose exec maiconsoft-api sh

# Entrar no container do frontend
docker-compose exec maiconsoft-frontend sh
```

### Rebuild e Limpeza
```bash
# Rebuild sem cache
docker-compose build --no-cache

# Remover containers e volumes
docker-compose down -v

# Limpar imagens não utilizadas
docker system prune -f
```

## 🏗️ Arquitetura dos Containers

### Backend (Spring Boot)
- **Base Image:** `openjdk:21-jdk-slim`
- **Porta:** 8090
- **Volumes:** 
  - `./uploads:/app/uploads` (arquivos de upload)
  - `./logs:/app/logs` (logs da aplicação)

### Frontend (Nginx)
- **Base Image:** `nginx:alpine`
- **Porta:** 80 (HTTP), 443 (HTTPS - futuro)
- **Proxy:** API requests são proxy para o backend

## 🔧 Configurações Avançadas

### Variáveis de Ambiente Disponíveis

**Backend (.env):**
```bash
# Database (OBRIGATÓRIO)
SUPABASE_PASSWORD=CkTMz5oUISI5gIUn

# Email (OPCIONAL - só se quiser usar suas próprias credenciais)
# O sistema já vem com email configurado no application.properties:
# empresamaiconsoft@gmail.com / cvjznokkvtzuaqzm
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=seu-email@gmail.com
MAIL_PASSWORD=sua-senha-de-app
```

> 📧 **Email já configurado:** O projeto vem com credenciais funcionais no `application.properties`. Configure no `.env` apenas se quiser usar suas próprias credenciais.

**Configurações do Docker Compose:**
- Ajuste de memória JVM: `JAVA_OPTS`
- Configurações de banco: `SPRING_DATASOURCE_*`
- Configurações de email: `SPRING_MAIL_*`

### Portas Customizadas

Para alterar as portas, edite o `docker-compose.yml`:

```yaml
services:
  maiconsoft-frontend:
    ports:
      - "8080:80"  # Alterar porta do frontend
      
  maiconsoft-api:
    ports:
      - "9090:8090"  # Alterar porta do backend
```

## 🔒 Segurança

### Práticas Implementadas:
- ✅ Containers executam com usuário não-root
- ✅ Variáveis sensíveis via environment
- ✅ Nginx com headers de segurança
- ✅ Health checks configurados

### Recomendações para Produção:
- Use HTTPS (configure certificados SSL)
- Configure firewall adequadamente
- Use Docker secrets para dados sensíveis
- Monitore logs e recursos

## 📊 Monitoramento

### Health Checks
```bash
# Verificar saúde dos containers
docker-compose ps

# Status específico
curl http://localhost:8090/api/health
curl http://localhost/
```

### Logs Estruturados
```bash
# Logs em tempo real
docker-compose logs -f --tail=100

# Logs de erro específicos
docker-compose logs maiconsoft-api | grep ERROR
```

## 🐛 Troubleshooting

### Problemas Comuns

**1. Container não inicia:**
```bash
# Verificar logs
docker-compose logs maiconsoft-api

# Verificar configurações
docker-compose config
```

**2. Erro de conexão com banco:**
```bash
# Verificar variáveis de ambiente
docker-compose exec maiconsoft-api env | grep SPRING

# Testar conectividade
docker-compose exec maiconsoft-api ping db.hmjldrzvmaqgetjcepay.supabase.co
```

**🚨 PROBLEMA ESPECÍFICO WINDOWS + SUPABASE:**
```bash
# Se tiver erro "could not connect to server" no Windows:

# 1. Verificar se Docker Desktop está usando WSL2
docker version

# 2. Testar conectividade do host
ping db.hmjldrzvmaqgetjcepay.supabase.co

# 3. Verificar configuração DNS do Docker
docker run --rm alpine nslookup db.hmjldrzvmaqgetjcepay.supabase.co

# 4. Se continuar falhando, usar IP direto:
# Descobrir IP do Supabase
nslookup db.hmjldrzvmaqgetjcepay.supabase.co

# Então editar docker-compose.yml temporariamente:
# SPRING_DATASOURCE_URL: jdbc:postgresql://[IP]:5432/postgres?sslmode=require
```

**💡 SOLUÇÕES PARA WINDOWS:**
1. **Reiniciar Docker Desktop** - Resolve 80% dos problemas de rede
2. **Verificar Windows Defender/Firewall** - Pode bloquear conexões
3. **Usar WSL2 em vez de Hyper-V** no Docker Desktop
4. **Verificar proxy corporativo** se estiver em rede da empresa

### 🔧 Solução Alternativa para Windows

Se os problemas de conexão persistirem, você pode usar o **modo desenvolvimento local:**

**Opção 1 - Script Automático:**
```bash
# Execute o script que automatiza tudo
./docker-alternativo-windows.bat
```

**Opção 2 - Manual:**
```bash
# 1. Parar containers
docker-compose down

# 2. Executar apenas o backend local
cd maiconsoft_api
./run-with-supabase.bat

# 3. Executar frontend separadamente
# Em outro terminal
cd frontend
# Servir arquivos estáticos (usar Live Server no VS Code ou similar)
```

**🔍 Script de Diagnóstico:**
```bash
# Execute para diagnosticar problemas
./docker-diagnostico-windows.bat
```

Esta abordagem evita problemas de rede do Docker no Windows.

**2. Erro de conexão com banco:**
```bash
# Verificar variáveis de ambiente
docker-compose exec maiconsoft-api env | grep SPRING

# Testar conectividade
docker-compose exec maiconsoft-api ping db.hmjldrzvmaqgetjcepay.supabase.co
```

**🚨 PROBLEMA ESPECÍFICO WINDOWS + SUPABASE:**
```bash
# Se tiver erro "could not connect to server" no Windows:

# 1. Verificar se Docker Desktop está usando WSL2
docker version

# 2. Testar conectividade do host
ping db.hmjldrzvmaqgetjcepay.supabase.co

# 3. Verificar configuração DNS do Docker
docker run --rm alpine nslookup db.hmjldrzvmaqgetjcepay.supabase.co

# 4. Se continuar falhando, usar IP direto:
# Descobrir IP do Supabase
nslookup db.hmjldrzvmaqgetjcepay.supabase.co

# Então editar docker-compose.yml temporariamente:
# SPRING_DATASOURCE_URL: jdbc:postgresql://[IP]:5432/postgres?sslmode=require
```

**💡 SOLUÇÕES PARA WINDOWS:**
1. **Reiniciar Docker Desktop** - Resolve 80% dos problemas de rede
2. **Verificar Windows Defender/Firewall** - Pode bloquear conexões
3. **Usar WSL2 em vez de Hyper-V** no Docker Desktop
4. **Verificar proxy corporativo** se estiver em rede da empresa

**3. Frontend não carrega:**
```bash
# Verificar nginx
docker-compose exec maiconsoft-frontend nginx -t

# Verificar proxy
curl -I http://localhost/api/health
```

**4. Problemas de permissão:**
```bash
# Verificar volumes
docker-compose exec maiconsoft-api ls -la /app/uploads

# Corrigir permissões (se necessário)
sudo chown -R $USER:$USER ./uploads ./logs
```

## 🔄 Backup e Restore

### Backup de Uploads
```bash
# Criar backup
tar -czf uploads-backup-$(date +%Y%m%d).tar.gz uploads/

# Restaurar backup
tar -xzf uploads-backup-YYYYMMDD.tar.gz
```

### Backup de Logs
```bash
# Arquivar logs antigos
docker-compose exec maiconsoft-api sh -c "tar -czf /app/logs/archive-$(date +%Y%m%d).tar.gz /app/logs/*.log && rm /app/logs/*.log"
```

## 📈 Performance

### Otimizações Implementadas:
- Multi-stage build para reduzir tamanho das imagens
- Cache de dependências Maven
- Compressão gzip no Nginx
- Health checks otimizados

### Monitoramento de Recursos:
```bash
# Uso de recursos
docker stats

# Uso específico
docker stats maiconsoft-api maiconsoft-frontend
```

## 🌍 Deploy em Produção

### Cloud Providers
- **AWS:** Use ECS ou EKS
- **Google Cloud:** Use Cloud Run ou GKE
- **Azure:** Use Container Instances ou AKS
- **DigitalOcean:** Use App Platform ou Kubernetes

### CI/CD
O projeto está preparado para integração com:
- GitHub Actions
- GitLab CI
- Jenkins
- Azure DevOps

---

## 📞 Suporte

Para problemas específicos do Docker:
1. Verifique os logs: `docker-compose logs -f`
2. Teste a conectividade de rede
3. Valide as variáveis de ambiente
4. Consulte a documentação oficial do Docker

**Comandos de diagnóstico:**
```bash
docker version
docker-compose version
docker system info
docker network ls
```