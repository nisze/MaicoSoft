#  Maiconsoft API - Backend Spring Boot

Backend REST API para o sistema Maiconsoft de gesto de clientes, vendas e relatrios com tema de construo civil.

##  Incio Rpido

### **Opo 1: Docker (Recomendado)**
`ash
# Na raiz do projeto
cd scripts
start.bat
`
Acesse: http://localhost:8090/api

### **Opo 2: Desenvolvimento Local**
`ash
# Certifique-se de ter Java 17+ e PostgreSQL
cd maiconsoft_api
mvnw spring-boot:run
`
Acesse: http://localhost:8090/api

##  Pr-requisitos

### Docker:
- Docker Desktop 4.0+
- Docker Compose 2.0+

### Desenvolvimento Local:
- Java 17 ou superior
- Maven 3.6+
- PostgreSQL 12+

##  Stack Tecnolgico

- **Java 17** (Eclipse Temurin)
- **Spring Boot 3.5.5**
- **Spring Data JPA** + Hibernate
- **PostgreSQL 15**
- **Flyway** (migraes de banco)
- **Caffeine Cache** (cache in-memory)
- **JavaMail** (envio de emails)
- **Maven** (build)
- **Docker** (containerizao)

##  Estrutura do Projeto

`
maiconsoft_api/
 Dockerfile                     # Container da API
 pom.xml                        # Dependncias Maven
 src/main/java/com/faculdae/maiconsoft_api/
    controllers/               # REST Controllers
       AuthController.java    # Autenticao e recuperao de senha
       UserController.java    # CRUD Usurios + foto perfil
       ClienteController.java # CRUD Clientes
       VendaController.java   # CRUD Vendas + comprovantes
       CupomController.java   # CRUD Cupons
       RelatorioController.java # Relatrios
    models/                    # Entidades JPA
    repositories/              # Repositrios JPA
    services/                  # Lgica de negcio
    config/                    # Configuraes
 src/main/resources/
     application.properties     # Configurao nica
     db/migration/             # Migraes Flyway
     templates/email/          # Templates de email
     logback-spring.xml        # Logs
`

##  Endpoints Principais

| Recurso | Endpoint | Mtodos |
|---------|----------|---------|
| **Autenticao** | /api/auth/login | POST |
| | /api/auth/forgot-password | POST |
| | /api/auth/reset-password | POST |
| **Usurios** | /api/usuarios | GET, POST, PUT, DELETE |
| | /api/usuarios/{id}/photo | POST, GET |
| **Clientes** | /api/clientes | GET, POST, PUT, DELETE |
| **Vendas** | /api/vendas | GET, POST, PUT, DELETE |
| | /api/vendas/{id}/comprovante | POST, GET |
| **Cupons** | /api/cupons | GET, POST, PUT, DELETE |
| **Relatrios** | /api/relatorios/** | GET |

##  Configurao

### application.properties
`properties
# Server
server.port=8090

# Database (Docker usa postgres:postgres123)
spring.datasource.url=jdbc:postgresql://localhost:5432/maiconsoft
spring.datasource.username=postgres
spring.datasource.password=postgres123

# Flyway
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true

# File Upload
app.upload.dir=uploads/

# Email (Gmail)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=empresamaiconsoft@gmail.com
spring.mail.password=cvjznokkvtzuaqzm

# Cache
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=500,expireAfterWrite=15m
`

##  Build e Deploy

### Build local:
`ash
mvnw clean package
java -jar target/maiconsoft_api-0.0.1-SNAPSHOT.jar
`

### Docker:
`ash
# Na raiz do projeto
docker-compose up --build
`

##  Testes

### Postman Collection:
Importe a collection em postman/Maiconsoft_API_Collection.json

### Credenciais de Teste:
| Login | Senha | Perfil |
|-------|-------|--------|
| ADM001 | 123456 | ADMIN |
| W36K0D | 123456 | VENDEDOR |

##  Logs

Logs so salvos em:
- **Console**: Nvel INFO
- **Arquivo**: logs/maiconsoft.log (rotao diria)

##  Segurana

- CORS configurado para frontend
- Validao de inputs
- Cache para reset de senha (15min expiration)
- Upload de arquivos com validao de tipo/tamanho

##  Documentao Adicional

- /docs/README.md - Viso geral do projeto
- /docs/SETUP.md - Guia de instalao detalhado
- /scripts/README.md - Scripts de gerenciamento

##  Troubleshooting

### Porta 8090 em uso:
`ash
# Windows
netstat -ano | findstr :8090
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8090
kill -9 <PID>
`

### Banco de dados no conecta:
`ash
# Verificar se PostgreSQL est rodando
docker ps | grep postgres

# Ver logs do backend
cd scripts
logs.bat backend
`

### Rebuild completo:
`ash
cd scripts
rebuild.bat
`

---

**Desenvolvido com  para o setor de construo civil**
