#  Maiconsoft - Sistema de Gesto

> Sistema completo de gesto empresarial desenvolvido com Spring Boot, PostgreSQL e JavaScript moderno.

##  Estrutura do Projeto

```
MAICOSOFT/
  docker-compose.yml   # Docker Compose (DB + API + Frontend)
  scripts/             # Scripts utilitrios
    start.bat           # Iniciar tudo
    stop.bat            # Parar tudo
    logs.bat            # Ver logs
    status.bat          # Ver status
    rebuild.bat         # Rebuild containers
    reset.bat           # Reset completo
    README.md           # Guia dos scripts
  docs/                # Documentao
    README.md           # Este arquivo
    SETUP.md            # Setup detalhado
  frontend/            # Interface Web
    pages/              # HTML pages
    css/                # Estilos
    js/                 # JavaScript
    Dockerfile          # Build frontend
    nginx.conf          # Config Nginx
  maiconsoft_api/      # Backend API
     src/                # Cdigo Java
     Dockerfile          # Build backend
     pom.xml             # Maven config
```

##  Quick Start

### Pr-requisitos
-  **Docker Desktop** instalado e rodando
-  **Git** (para clonar)

**Apenas isso!** Todo o resto  automtico.

###  Executar Aplicao

#### Opo 1: Scripts (Recomendado)
```bash
# Clonar projeto
git clone https://github.com/nisze/MaicoSoft.git
cd MaicoSoft

# Iniciar tudo (Windows)
scripts\start.bat

# Linux/Mac
chmod +x scripts/*.sh
./scripts/start.sh
```

#### Opo 2: Docker Compose Direto
```bash
cd docker
docker-compose up -d
```

###  Primeira Execuo
- **Download de imagens**: ~2 minutos
- **Compilao Maven**: ~3-5 minutos
- **Total**: 5-7 minutos

Execues seguintes: ~30 segundos! 

##  Acessar Aplicao

Aps containers estarem rodando:

| Servio | URL | Descrio |
|---------|-----|-----------|
| **Frontend** | http://localhost:3000 | Interface web |
| **Backend API** | http://localhost:8090/api | REST API |
| **PostgreSQL** | localhost:5432 | Banco de dados |

##  Credenciais

### Banco de Dados
```
Host:     localhost
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

##  Sobre o Projeto

### Funcionalidades

-  **Gesto de Clientes** - Cadastro completo com CPF/CNPJ
-  **Gesto de Vendas** - Controle de oramentos e vendas
-  **Sistema de Cupons** - Descontos e promoes
-  **Gesto de Usurios** - Controle de acesso
-  **Dashboard** - Mtricas e indicadores
-  **Email** - Notificaes automticas
-  **Recuperao de Senha** - Via email com tokens

##  Tecnologias

### Backend
- **Java 17** - Linguagem base
- **Spring Boot 3.5.5** - Framework
- **Spring Security** - Autenticao
- **Spring Data JPA** - Persistncia
- **PostgreSQL 15** - Banco de dados
- **Flyway** - Migrations
- **Caffeine Cache** - Cache em memria
- **Maven** - Build tool

### Frontend
- **HTML5 / CSS3** - Marcao e estilos
- **JavaScript ES6+** - Lgica
- **Nginx** - Web server
- **Amber Theme** - Design system

### DevOps
- **Docker** - Containerizao
- **Docker Compose** - Orquestrao
- **Multi-stage builds** - Otimizao

##  Comandos teis

### Gerenciamento de Containers
```bash
# Iniciar
scripts\start.bat

# Ver logs
scripts\logs.bat

# Ver status
scripts\status.bat

# Parar
scripts\stop.bat

# Rebuild aps mudanas
scripts\rebuild.bat

# Reset completo (apaga dados!)
scripts\reset.bat
```

### Docker Compose Direto
```bash
cd docker

# Iniciar
docker-compose up -d

# Ver logs
docker-compose logs -f

# Status
docker-compose ps

# Parar
docker-compose down

# Rebuild
docker-compose up -d --build

# Reset (apaga volumes)
docker-compose down -v
```

##  Desenvolvimento

### Rodar Localmente (sem Docker)

Se preferir rodar o backend fora do Docker:

```bash
# Iniciar apenas PostgreSQL no Docker
cd docker
docker-compose up -d postgres

# Rodar backend localmente
cd maiconsoft_api
mvn spring-boot:run

# Frontend com Live Server (VS Code)
# Porta 5500 ou outra
```

### Estrutura do Cdigo

#### Backend (`maiconsoft_api/src/main/java`)
```
com.faculdae.maiconsoft_api/
 config/           # Configuraes (CORS, Security, etc)
 controllers/      # REST Controllers
 entities/         # Entidades JPA
 repositories/     # Repositories
 services/         # Lgica de negcio
 MaiconsoftApiApplication.java
```

#### Frontend (`frontend/`)
```
frontend/
 pages/           # Pginas HTML
    login.html
    dashboard.html
    clientes.html
    vendas.html
    ...
 css/            # Estilos
    style.css
    dashboard.css
    ...
 js/             # JavaScript
    app.js
    config.js
    ...
 images/         # Assets
```

##  Banco de Dados

### Migrations (Flyway)

Migrations esto em `maiconsoft_api/src/main/resources/db/migration/`

```
V1__Initial_Schema.sql          # Schema inicial
V2__Insert_Initial_Data.sql     # Dados iniciais
V13__Add_Test_Users.sql         # Usurios de teste
...
```

### Entidades Principais

- **User** - Usurios do sistema
- **Cliente** - Clientes da empresa
- **Venda** - Vendas/Oramentos
- **Cupom** - Cupons de desconto

##  Segurana

-  Senhas hasheadas com BCrypt
-  Reset de senha via email com tokens temporrios
-  Tokens expiram em 15 minutos
-  Cache Caffeine para tokens
-  CORS configurado

##  Email

Sistema de email configurado com Gmail:

- **Templates**: Thymeleaf em `resources/templates/email/`
- **Reset de senha**: Envio automtico de tokens
- **Configurvel**: Pode habilitar/desabilitar

##  Troubleshooting

### Backend no inicia
```bash
# Ver logs
scripts\logs.bat

# Ou
docker-compose logs backend

# Aguardar 90s na primeira vez (compilao Maven)
```

### Frontend no conecta
```bash
# Verificar se backend est rodando
curl http://localhost:8090/api

# Ver status
scripts\status.bat
```

### Reset completo
```bash
# Apaga tudo e recomea
scripts\reset.bat

# Ou manualmente
cd docker
docker-compose down -v
docker system prune -a
docker-compose up -d --build
```

##  Deploy em Produo

### Consideraes
1. Mudar senhas (banco, email, etc)
2. Configurar domnio prprio
3. Habilitar HTTPS (certificado SSL)
4. Backup automtico do banco
5. Monitoring e logs

### Docker Compose Produo
```yaml
# Ajustar no docker-compose.yml
environment:
  SPRING_DATASOURCE_PASSWORD: senha_forte_aqui
  # Usar variveis de ambiente secretas
```

##  Licena

Este projeto  um trabalho acadmico.

##  Autores

Desenvolvido por estudantes da faculdade.

##  Suporte

Para dvidas ou problemas:
1. Verifique a documentao do projeto
2. Verifique logs: `scripts\logs.bat`
3. Use o reset: `scripts\reset.bat`

##  Atualizaes

Para atualizar o projeto:

```bash
# Pull ltimas mudanas
git pull origin main

# Rebuild containers
scripts\rebuild.bat
```

##  Mais Informao

- [Scripts README](../scripts/README.md) - Guia dos scripts
- [SETUP.md](SETUP.md) - Setup detalhado passo a passo