#  Maiconsoft - Guia de Setup Completo

Este guia detalha passo a passo como configurar e executar o projeto Maiconsoft.

##  ndice

1. [Pr-requisitos](#pr-requisitos)
2. [Instalao](#instalao)
3. [Executar com Docker](#executar-com-docker)
4. [Executar Localmente](#executar-localmente)
5. [Configurao Avanada](#configurao-avanada)
6. [Troubleshooting](#troubleshooting)

##  Pr-requisitos

### Obrigatrio

- **Docker Desktop** 
  - Windows/Mac: https://www.docker.com/products/docker-desktop
  - Linux: `sudo apt install docker.io docker-compose`
  
- **Git** 
  - https://git-scm.com/downloads

### Opcional (apenas para desenvolvimento local)

- **Java 17+**
  - https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
  
- **Maven 3.9+**
  - https://maven.apache.org/download.cgi
  
- **IDE** (IntelliJ IDEA, Eclipse, VS Code)

##  Instalao

### 1. Clonar o Repositrio

```bash
git clone https://github.com/nisze/MaicoSoft.git
cd MaicoSoft
```

### 2. Verificar Estrutura

```
MaicoSoft/
 docker-compose.yml Deve existir
 scripts/           Deve existir
 frontend/          Deve existir
 maiconsoft_api/    Deve existir
```

##  Executar com Docker (Recomendado)

### Mtodo 1: Scripts Automatizados (Mais Fcil)

#### Windows

```bash
# Ir para pasta do projeto
cd MaicoSoft

# Iniciar tudo
scripts\start.bat
```

Aguarde 5-7 minutos na primeira execuo (compilao Maven).

```bash
# Ver logs
scripts\logs.bat

# Ver status
scripts\status.bat

# Parar
scripts\stop.bat
```

#### Linux/Mac

```bash
# Dar permisso aos scripts
chmod +x scripts/*.sh

# Iniciar
./scripts/start.sh

# Ver logs
./scripts/logs.sh

# Parar
./scripts/stop.sh
```

### Mtodo 2: Docker Compose Direto

```bash
# Ir para pasta docker
cd docker

# Iniciar containers
docker-compose up -d

# Ver logs
docker-compose logs -f

# Verificar status
docker-compose ps

# Parar
docker-compose down
```

### Acessar Aplicao

Aps containers estarem rodando com status **healthy**:

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8090/api
- **PostgreSQL**: localhost:5432

### Credenciais

**Usurios da Aplicao:**
- Admin: `ADM001` / `123456`
- Usurio: `W36K0D` / `123456`

**Banco de Dados:**
- Host: `localhost`
- Port: `5432`
- Database: `maiconsoft`
- User: `postgres`
- Password: `postgres123`

##  Executar Localmente (Sem Docker)

til para desenvolvimento e debugging.

### 1. Preparar Banco de Dados

#### Opo A: PostgreSQL no Docker (Recomendado)

```bash
cd docker
docker-compose up -d postgres
```

#### Opo B: PostgreSQL Instalado Localmente

Criar banco manualmente:
```sql
CREATE DATABASE maiconsoft;
```

### 2. Configurar Backend

```bash
cd maiconsoft_api

# Verificar application.properties
# src/main/resources/application.properties
```

Deve estar assim:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/maiconsoft
spring.datasource.username=postgres
spring.datasource.password=postgres123
```

### 3. Executar Backend

#### Com Maven

```bash
cd maiconsoft_api
mvn clean install
mvn spring-boot:run
```

#### Com IDE

1. Abrir projeto em IntelliJ/Eclipse/VS Code
2. Importar como projeto Maven
3. Aguardar download de dependncias
4. Executar `MaiconsoftApiApplication.java`

Backend rodando em: http://localhost:8090

### 4. Executar Frontend

#### Opo A: Live Server (VS Code)

1. Instalar extenso "Live Server"
2. Abrir `frontend/pages/login.html`
3. Clicar com boto direito  "Open with Live Server"

Frontend rodando em: http://localhost:5500

#### Opo B: Servidor HTTP Python

```bash
cd frontend
python -m http.server 3000
```

Frontend rodando em: http://localhost:3000

#### Opo C: Node.js http-server

```bash
npm install -g http-server
cd frontend
http-server -p 3000
```

##  Configurao Avanada

### Email (Recuperao de Senha)

Editar `maiconsoft_api/src/main/resources/application.properties`:

```properties
# Gmail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seu_email@gmail.com
spring.mail.password=sua_senha_app

# Habilitar/Desabilitar
app.email.enabled=true
```

**Criar senha de app Gmail:**
1. Acessar conta Google
2. Segurana  Verificao em duas etapas
3. Senhas de app  Gerar nova

### Portas Customizadas

#### Backend

Editar `application.properties`:
```properties
server.port=8090  # Mudar para outra porta
```

#### Frontend (Docker)

Editar `docker-compose.yml`:
```yaml
frontend:
  ports:
    - "3000:80"  # HOST:CONTAINER
```

#### PostgreSQL (Docker)

Editar `docker-compose.yml`:
```yaml
postgres:
  ports:
    - "5432:5432"  # Mudar porta host
```

### CORS

Adicionar origens permitidas em `application.properties`:
```properties
cors.allowed.origins=http://localhost,http://localhost:5500,http://127.0.0.1:5500
```

##  Troubleshooting

### Problema: Docker no est rodando

**Erro:** `docker: command not found` ou `Cannot connect to Docker daemon`

**Soluo:**
1. Verificar se Docker Desktop est instalado
2. Iniciar Docker Desktop
3. Aguardar at estar completamente iniciado

### Problema: Porta j em uso

**Erro:** `Port 8090 is already in use`

**Soluo:**

Windows:
```bash
# Ver quem est usando a porta
netstat -ano | findstr :8090

# Matar processo
taskkill /PID <nmero_do_pid> /F
```

Linux/Mac:
```bash
# Ver quem est usando a porta
lsof -i :8090

# Matar processo
kill -9 <PID>
```

Ou mudar a porta em `application.properties`.

### Problema: Backend no compila

**Erro:** Maven build failed

**Soluo:**
```bash
cd maiconsoft_api

# Limpar cache Maven
mvn clean

# Rebuild
mvn clean install -DskipTests

# Se ainda falhar, remover cache
rm -rf ~/.m2/repository
mvn clean install
```

### Problema: Erro de conexo com banco

**Erro:** `Connection refused` ou `Connection timeout`

**Soluo:**

1. Verificar se PostgreSQL est rodando:
```bash
docker-compose ps postgres
```

2. Verificar logs do PostgreSQL:
```bash
docker-compose logs postgres
```

3. Restart do PostgreSQL:
```bash
docker-compose restart postgres
```

4. Verificar credenciais em `application.properties`

### Problema: Frontend no conecta com backend

**Erro:** `Failed to fetch` ou `Network error`

**Soluo:**

1. Verificar se backend est rodando:
```bash
curl http://localhost:8090/api
```

2. Verificar `frontend/js/config.js`:
```javascript
const CONFIG = {
    API_BASE_URL: 'http://localhost:8090/api',  // Verificar URL
    ...
};
```

3. Verificar CORS no backend

### Problema: Containers no ficam "healthy"

**Sintoma:** `docker-compose ps` mostra status "unhealthy"

**Soluo:**

```bash
# Ver logs detalhados
docker-compose logs backend

# Aumentar start_period no docker-compose.yml
backend:
  healthcheck:
    start_period: 120s  # Aumentar de 90s para 120s
```

### Reset Completo

Se nada funcionar:

```bash
# Windows
scripts\reset.bat

# Linux/Mac
./scripts/reset.sh

# Ou manualmente
cd docker
docker-compose down -v
docker system prune -a
docker-compose up -d --build
```

##  Verificaes de Sade

### Backend

```bash
# Health check
curl http://localhost:8090

# API est respondendo
curl http://localhost:8090/api
```

### PostgreSQL

```bash
# Conectar via psql
docker exec -it maiconsoft-postgres psql -U postgres -d maiconsoft

# Listar tabelas
\dt

# Sair
\q
```

### Frontend

```bash
# Verificar se est servindo
curl http://localhost:3000
```

##  Prximos Passos

Aps setup completo:

1. **Explorar API**: http://localhost:8090/api
2. **Testar Login**: http://localhost:3000
3. **Ver Dashboard**: Fazer login e explorar funcionalidades
4. **Desenvolver**: Fazer mudanas e usar `scripts\rebuild.bat`

##  Recursos Adicionais

- [README Principal](README.md)
- [Scripts README](../scripts/README.md)

##  Dicas de Desenvolvimento

### Hot Reload Backend

Adicionar no `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>
```

### Debug no IntelliJ

1. Run  Edit Configurations
2. Add New  Spring Boot
3. Main class: `MaiconsoftApiApplication`
4. Run in Debug mode (Shift+F9)

### Ver Logs em Tempo Real

```bash
# Todos os servios
scripts\logs.bat

# Apenas backend
docker-compose logs -f backend

# ltimas 100 linhas
docker-compose logs --tail=100 backend
```

##  Checklist de Setup

- [ ] Docker Desktop instalado e rodando
- [ ] Repositrio clonado
- [ ] Containers iniciados (`scripts\start.bat`)
- [ ] Containers com status "healthy"
- [ ] Frontend acessvel em http://localhost:3000
- [ ] Backend acessvel em http://localhost:8090
- [ ] Login funcionando com credenciais de teste
- [ ] Dashboard carregando

Se todos os itens estiverem , setup est completo! 