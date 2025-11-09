#  Maiconsoft - Docker Full Stack

##  Stack Completa

Este Docker Compose roda toda a aplicao com 3 containers:

- ** PostgreSQL 15** - Banco de dados (porta 5432)
- ** Spring Boot API** - Backend Java (porta 8090)
- ** Nginx** - Frontend esttico (porta 80)

##  Quick Start

### 1. Iniciar toda a aplicao

```bash
cd docker
docker-compose up -d
```

**Primeira execuo demora ~3-5 minutos:**
-  Compilao do backend Maven
-  Download das imagens Docker
-  Criao dos containers

### 2. Acompanhar logs

```bash
# Ver logs de todos os servios
docker-compose logs -f

# Ver apenas backend
docker-compose logs -f backend

# Ver apenas frontend
docker-compose logs -f frontend

# Ver apenas banco
docker-compose logs -f postgres
```

### 3. Verificar status

```bash
docker-compose ps
```

**Status esperado:**
```
NAME                    STATUS              PORTS
maiconsoft-postgres     Up (healthy)        5432->5432
maiconsoft-backend      Up (healthy)        8090->8090
maiconsoft-frontend     Up (healthy)        80->80
```

##  Acessar Aplicao

Aps os containers estarem com status **healthy**:

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8090/api
- **Banco de Dados**: localhost:5432

**Nota:** Frontend rodando no Live Server (VS Code) geralmente usa porta 5500.

##  Credenciais

### Banco de Dados PostgreSQL
```
Host:     localhost (ou postgres dentro da rede Docker)
Port:     5432
Database: maiconsoft
User:     postgres
Password: postgres123
```

### Usurios da Aplicao
```
Admin:    ADM001 / 123456
Usurio:  W36K0D / 123456
```

##  Comandos teis

### Parar aplicao
```bash
docker-compose down
```

### Parar e limpar volumes (apaga dados do banco)
```bash
docker-compose down -v
```

### Rebuild aps mudanas no cdigo
```bash
# Rebuild tudo
docker-compose up -d --build

# Rebuild apenas backend
docker-compose up -d --build backend

# Rebuild apenas frontend
docker-compose up -d --build frontend
```

### Reiniciar um servio especfico
```bash
docker-compose restart backend
docker-compose restart frontend
docker-compose restart postgres
```

### Ver uso de recursos
```bash
docker stats
```

### Acessar shell dos containers
```bash
# Backend
docker exec -it maiconsoft-backend sh

# Frontend
docker exec -it maiconsoft-frontend sh

# Banco de dados (psql)
docker exec -it maiconsoft-postgres psql -U postgres -d maiconsoft
```

##  Estrutura dos Volumes

Os dados persistem nos volumes Docker:

```
postgres_data     Dados do PostgreSQL
backend_uploads   Uploads de fotos de perfil
backend_logs      Logs da aplicao
```

##  Troubleshooting

### Backend no inicia

**Sintomas:** Container backend fica reiniciando ou status unhealthy

**Verificar:**
```bash
docker-compose logs backend
```

**Solues:**
1. Aguardar 90 segundos (primeira vez demora mais)
2. Verificar se PostgreSQL est healthy: `docker-compose ps`
3. Rebuild: `docker-compose up -d --build backend`

### Frontend mostra erro de conexo

**Sintomas:** Pgina carrega mas no conecta na API

**Verificar:**
```bash
# Status do backend
docker-compose ps backend

# Logs do backend
docker-compose logs backend

# Testar API diretamente
curl http://localhost:8090/api
```

### PostgreSQL no aceita conexes

**Verificar:**
```bash
docker-compose logs postgres
```

**Soluo:**
```bash
docker-compose restart postgres
```

### Limpar tudo e recomear do zero

```bash
# Parar tudo e remover volumes
docker-compose down -v

# Limpar cache Docker (opcional)
docker system prune -a

# Reconstruir tudo
docker-compose up -d --build
```

##  Monitoramento

### Verificar sade dos containers

```bash
docker-compose ps
```

Todos devem estar **Up (healthy)**

### Ver logs em tempo real

```bash
# Todos os servios
docker-compose logs -f --tail=100

# Apenas erros
docker-compose logs -f | grep -i error
```

### Inspecionar container

```bash
docker inspect maiconsoft-backend
docker inspect maiconsoft-frontend
docker inspect maiconsoft-postgres
```

##  Atualizao de Cdigo

Aps fazer mudanas no cdigo:

```bash
# 1. Rebuild e restart do servio alterado
docker-compose up -d --build backend

# 2. Verificar logs
docker-compose logs -f backend
```

##  Rede Docker

Os containers esto na rede `maiconsoft-network` e podem se comunicar pelos nomes:

- Backend pode acessar: `postgres:5432`
- Frontend pode acessar: `backend:8090`
- Externamente: `localhost:porta`

##  Notas Importantes

1. **Primeira execuo demora:** Maven precisa baixar dependncias e compilar
2. **Healthcheck:** Aguarde todos ficarem "healthy" antes de testar
3. **Volumes persistem dados:** Use `docker-compose down -v` para limpar
4. **Porta 80 em uso?** Pare outros servios (IIS, Apache, etc)
5. **Windows:** Pode precisar permitir acesso no Firewall

##  Ordem de Inicializao

O Docker Compose garante a ordem correta:

1. **PostgreSQL** inicia primeiro
2. **Backend** aguarda PostgreSQL estar healthy
3. **Frontend** inicia aps backend estar disponvel