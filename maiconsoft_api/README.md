# 🏢 Maiconsoft - Sistema de Gestão de Clientes

Sistema completo para gestão de clientes com frontend responsivo e backend Spring Boot.

## � Como Executar o Projeto

### 📋 Pré-requisitos
- Navegador web moderno (Chrome, Firefox, Edge, Safari)
- Java 11+ (para rodar a API - opcional)
- Git para clonar o projeto

### 🎯 Modo Demonstração (Recomendado)
**Para testar rapidamente sem configurar nada:**

1. **Clone o repositório:**
   ```bash
   git clone [URL_DO_SEU_REPO]
   cd maiconsoft_api
   ```

2. **Abra o sistema:**
   - Navegue até a pasta: `frontend/pages/`
   - Abra o arquivo: **`clientes.html`** no navegador
   - OU simplesmente abra: **`index.html`** na raiz do projeto

3. **Pronto!** ✅
   - Sistema funciona com dados de demonstração
   - 5 clientes de exemplo já carregados
   - Todas as funcionalidades ativas (cadastrar, listar, editar, visualizar)

### 🖥️ Executando com API (Opcional)

Se quiser testar com a API real:

1. **Configure o banco de dados** no `application.properties`
2. **Execute a API:**
   ```bash
   ./mvnw spring-boot:run
   ```
3. **Abra o frontend** e as chamadas serão feitas para `localhost:8080`

## 🚀 Tecnologias Utilizadas

### Backend Core
- **Java 21** - Linguagem de programação
- **Spring Boot 3.5.5** - Framework principal
- **Spring Security** - Autenticação e autorização
- **Spring Data JPA** - Persistência de dados
- **Hibernate** - ORM

### Banco de Dados
- **PostgreSQL** - Banco principal (Supabase hospedado)
- **Spring Data JPA** - Persistência de dados

### Documentação e Testes
- **Swagger/OpenAPI 3** - Documentação da API
- **JUnit 5** - Testes unitários
- **Mockito** - Mocks para testes

### Infraestrutura e Monitoramento
- **Logback** - Sistema de logs
- **Micrometer** - Métricas
- **Actuator** - Monitoramento de saúde

## 📁 Estrutura do Projeto

```
maiconsoft_api/
├── src/main/java/com/faculdae/maiconsoft_api/
│   ├── config/                    # Configurações
│   │   ├── SwaggerConfig.java     # Documentação API
│   │   ├── SecurityConfig.java    # Segurança
│   │   ├── CorsConfig.java        # CORS
│   │   ├── GlobalExceptionHandler.java  # Tratamento global de erros
│   │   └── RateLimitFilter.java   # Rate limiting
│   ├── controllers/               # Controllers REST
│   │   ├── AuthController.java    # Autenticação
│   │   ├── UserController.java    # Usuários
│   │   ├── VendaController.java   # Vendas
│   │   └── ClienteController.java # Clientes
│   ├── dto/                       # Data Transfer Objects
│   │   ├── auth/                  # DTOs de autenticação
│   │   ├── user/                  # DTOs de usuário
│   │   └── venda/                 # DTOs de venda
│   ├── entities/                  # Entidades JPA
│   │   ├── User.java             # Usuário
│   │   ├── Venda.java            # Venda
│   │   ├── Cliente.java          # Cliente
│   │   ├── Cupom.java            # Cupom
│   │   └── DashboardLog.java     # Auditoria
│   ├── repositories/              # Repositórios JPA
│   ├── services/                  # Camada de serviços
│   │   ├── auth/                  # Serviços de autenticação
│   │   ├── user/                  # Serviços de usuário
│   │   ├── venda/                 # Serviços de venda
│   │   ├── email/                 # Serviços de email
│   │   └── audit/                 # Serviços de auditoria
│   ├── security/                  # Componentes de segurança
│   └── specification/             # Specifications JPA
└── src/main/resources/
    ├── application.properties     # Configuração desenvolvimento
    ├── application-prod.properties # Configuração produção
    └── logback-spring.xml        # Configuração de logs
```

## 🛠 Instalação e Configuração

### Pré-requisitos

- **Java 21+** instalado
- **Maven 3.6+** para build
- **PostgreSQL** (Supabase hospedado)
- **Git** para controle de versão

### 1. Clone o Repositório

```bash
git clone https://github.com/seu-usuario/maiconsoft_api.git
cd maiconsoft_api
```

### 2. Configuração do Banco de Dados

#### PostgreSQL (Supabase - Padrão)
Configure as variáveis de ambiente ou application.properties:

```properties
# PostgreSQL Supabase Configuration
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://db.hmjldrzvmaqgetjcepay.supabase.co:5432/postgres?sslmode=require
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA_SUPABASE

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Connection Pool
spring.datasource.hikari.maximum-pool-size=5
spring.datasource.hikari.minimum-idle=1
```

#### Variáveis de Ambiente (Alternativa)
```bash
export DB_URL="jdbc:postgresql://db.hmjldrzvmaqgetjcepay.supabase.co:5432/postgres?sslmode=require"
export DB_USERNAME="postgres"
export DB_PASSWORD="sua_senha_supabase"
```

### 3. Configuração de Email (Opcional)

```bash
export SMTP_HOST="smtp.gmail.com"
export SMTP_PORT="587"
export SMTP_USERNAME="seu_email@gmail.com"
export SMTP_PASSWORD="sua_senha_app"
```

### 4. Configuração JWT

```bash
export JWT_SECRET="secureJwtSecretKeyForProduction2024!"
export JWT_EXPIRATION="86400000"  # 24 horas
```

## 🚀 Como Executar

### Desenvolvimento

```bash
# Compilar o projeto
./mvnw clean compile

# Executar em modo desenvolvimento
./mvnw spring-boot:run

# Ou usando profile específico
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Produção

```bash
# Build do JAR
./mvnw clean package -DskipTests

# Executar em produção
java -jar target/maiconsoft_api-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Docker (Futuro)

```bash
# Build da imagem
docker build -t maiconsoft-api .

# Executar container
docker run -p 8080:8080 maiconsoft-api
```

## 📖 Documentação da API

### Swagger UI
Acesse a documentação interativa em:
- **Desenvolvimento**: http://localhost:8081/swagger-ui/index.html
- **Produção**: https://api.maiconsoft.com/swagger-ui/index.html

### Endpoints Principais

#### 🔐 Autenticação
```
POST /api/auth/login     # Login do usuário
POST /api/auth/register  # Registro de novo usuário
```

#### 👥 Usuários
```
GET    /api/users        # Listar usuários (com filtros)
GET    /api/users/{id}   # Obter usuário por ID
PUT    /api/users/{id}   # Atualizar usuário
DELETE /api/users/{id}   # Deletar usuário
```

#### 💰 Vendas
```
GET    /api/vendas       # Listar vendas (com filtros avançados)
POST   /api/vendas       # Criar nova venda
GET    /api/vendas/{id}  # Obter venda por ID
PUT    /api/vendas/{id}  # Atualizar venda
DELETE /api/vendas/{id}  # Deletar venda
```

#### 👤 Clientes
```
GET    /api/clientes     # Listar clientes
POST   /api/clientes     # Criar cliente
GET    /api/clientes/{id} # Obter cliente
PUT    /api/clientes/{id} # Atualizar cliente
```

### Filtros Avançados de Venda

A API suporta filtros sofisticados para vendas:

```bash
GET /api/vendas?nomeVendedor=João&valorMinimo=100.00&valorMaximo=1000.00&dataInicio=2024-01-01&dataFim=2024-12-31&status=ATIVA&tipoPagamento=PIX&nomeCliente=Maria&cpfCliente=12345678901&emailCliente=maria@email.com&telefoneCliente=11987654321&nomeFantasiaCliente=Empresa&cidadeCliente=São Paulo&estadoCliente=SP&cepCliente=01234567&cupomUtilizado=DESCONTO10&valorDesconto=50.00&observacoes=entrega&page=0&size=10&sort=dataHora,desc
```

## 🔧 Configurações

### Profiles Disponíveis

- **default**: Desenvolvimento com PostgreSQL Supabase
- **prod**: Produção com PostgreSQL Supabase
- **test**: Testes automatizados

### Variáveis de Ambiente

| Variável | Descrição | Padrão | Obrigatório |
|----------|-----------|---------|-------------|
| `DB_URL` | URL do banco PostgreSQL | Supabase connection string | Sim |
| `DB_USERNAME` | Usuário do banco | postgres | Sim |
| `DB_PASSWORD` | Senha do banco | sua_senha_supabase | Sim |
| `JWT_SECRET` | Chave secreta JWT | Padrão dev | Prod: Sim |
| `JWT_EXPIRATION` | Tempo expiração token | 86400000 | Não |
| `SMTP_HOST` | Servidor SMTP | smtp.gmail.com | Email: Sim |
| `SMTP_USERNAME` | Usuário SMTP | - | Email: Sim |
| `SMTP_PASSWORD` | Senha SMTP | - | Email: Sim |
| `CORS_ORIGINS` | Origens permitidas | localhost:3000 | Não |

## 🧪 Testes

### Executar Testes

```bash
# Todos os testes
./mvnw test

# Apenas testes unitários
./mvnw test -Dtest="**/*Test"

# Apenas testes de integração
./mvnw test -Dtest="**/*IT"

# Com cobertura de código
./mvnw jacoco:prepare-agent test jacoco:report
```

### Estrutura de Testes

```
src/test/java/
├── unit/                    # Testes unitários
│   ├── services/           # Testes de serviços
│   ├── controllers/        # Testes de controllers
│   └── repositories/       # Testes de repositórios
├── integration/            # Testes de integração
│   ├── AuthControllerIT.java
│   └── VendaControllerIT.java
└── resources/
    ├── application-test.properties
    └── data.sql           # Dados para testes
```

## 📊 Monitoramento e Logs

### Actuator Endpoints

```
GET /actuator/health     # Status da aplicação
GET /actuator/info       # Informações da aplicação
GET /actuator/metrics    # Métricas de performance
GET /actuator/prometheus # Métricas para Prometheus
```

### Logs Estruturados

Os logs são gerados em formato JSON para facilitar análise:

```json
{
  "@timestamp": "2024-09-18T10:30:00.000-03:00",
  "level": "INFO",
  "logger_name": "com.faculdae.maiconsoft_api.services.venda.VendaService",
  "message": "Venda criada com sucesso",
  "user": "joao.silva@email.com",
  "venda_id": 123,
  "valor": 1500.00
}
```

## 🔒 Segurança

### Autenticação JWT
- Tokens JWT com expiração configurável
- Refresh tokens automáticos
- Logout com invalidação de token

### Rate Limiting
- 100 requests/minuto por IP (configurável)
- Burst capacity de 150 requests
- Headers informativos: `X-RateLimit-*`

### CORS
- Origens configuráveis via environment
- Headers de segurança incluídos
- Preflight requests otimizados

### Validações
- Validação de entrada com Bean Validation
- Sanitização de dados
- Tratamento global de exceções

## 🚀 Deploy em Produção

### 1. Preparação

```bash
# Build otimizado para produção
./mvnw clean package -Pprod -DskipTests

# Verificar JAR gerado
ls -la target/maiconsoft_api-*.jar
```

### 2. Configuração do Servidor

```bash
# Criar usuário específico
sudo useradd -m -s /bin/bash maiconsoft

# Criar diretórios
sudo mkdir -p /opt/maiconsoft/logs
sudo chown -R maiconsoft:maiconsoft /opt/maiconsoft/

# Copiar JAR
sudo cp target/maiconsoft_api-*.jar /opt/maiconsoft/app.jar
```

### 3. Variáveis de Ambiente

Criar arquivo `/opt/maiconsoft/.env`:

```bash
# Database
DB_URL=jdbc:postgresql://db.hmjldrzvmaqgetjcepay.supabase.co:5432/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=@bacat3

# JWT
JWT_SECRET=production_jwt_secret_key_very_long_and_secure
JWT_EXPIRATION=86400000

# Email
SMTP_HOST=smtp.empresa.com
SMTP_PORT=587
SMTP_USERNAME=notifications@maiconsoft.com
SMTP_PASSWORD=smtp_password

# CORS
CORS_ORIGINS=https://app.maiconsoft.com,https://maiconsoft.com

# Rate Limiting
RATE_LIMIT_ENABLED=true
RATE_LIMIT_REQUESTS_PER_MINUTE=100
```

### 4. Systemd Service

Criar `/etc/systemd/system/maiconsoft-api.service`:

```ini
[Unit]
Description=Maiconsoft API
After=network.target

[Service]
Type=exec
User=maiconsoft
Group=maiconsoft
WorkingDirectory=/opt/maiconsoft
EnvironmentFile=/opt/maiconsoft/.env
ExecStart=/usr/bin/java -jar -Xmx1024m -Xms512m -Dspring.profiles.active=prod /opt/maiconsoft/app.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

### 5. Iniciar Serviço

```bash
sudo systemctl daemon-reload
sudo systemctl enable maiconsoft-api
sudo systemctl start maiconsoft-api
sudo systemctl status maiconsoft-api
```

### 6. Nginx (Reverse Proxy)

```nginx
server {
    listen 80;
    server_name api.maiconsoft.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## 🔧 Troubleshooting

### Problemas Comuns

#### 1. Aplicação não inicia
```bash
# Verificar logs
./mvnw spring-boot:run

# Verificar conectividade com banco PostgreSQL
telnet db.hmjldrzvmaqgetjcepay.supabase.co 5432

# Verificar configurações do banco
grep -E "(spring.datasource|spring.jpa)" src/main/resources/application.properties
```

#### 2. Erro de conexão com banco
```bash
# Verificar connection string do Supabase
# Verificar credenciais (usuário: postgres, senha: @bacat3)
# Verificar se Supabase está acessível
# Verificar se SSL está habilitado (sslmode=require)
```

#### 3. Performance baixa
```bash
# Verificar métricas
curl http://localhost:8080/actuator/metrics

# Verificar logs de performance
grep "PERFORMANCE" /opt/maiconsoft/logs/application.log

# Verificar pool de conexões
curl http://localhost:8080/actuator/metrics/hikaricp.connections
```

## 📞 Suporte

### Informações de Contato
- **Email**: suporte@maiconsoft.com
- **Telefone**: (11) 99999-9999
- **Documentação**: https://docs.maiconsoft.com
- **Status**: https://status.maiconsoft.com

### Reportar Issues
1. Acesse o repositório no GitHub
2. Crie uma nova issue
3. Inclua logs relevantes
4. Descreva passos para reproduzir

## 📝 Licença

Este projeto é propriedade da Maiconsoft. Todos os direitos reservados.

## 🏆 Equipe de Desenvolvimento

Desenvolvido com ❤️ pela equipe Maiconsoft:

- **Gestão & Coordenação**: João Vinícius
- **Full-Stack Developer & DBA**: Denise Oliveira
- **Front-end Developer & UX/UI**: Luiz Antonio
- **QA Specialist**: Leandro Nicolas

---

## 📈 Métricas e KPIs

### Performance Targets
- **Latência**: < 200ms (95th percentile)
- **Throughput**: > 1000 req/s
- **Uptime**: 99.9%
- **Error Rate**: < 0.1%

### Monitoramento
- **APM**: New Relic / DataDog
- **Logs**: ELK Stack
- **Métricas**: Prometheus + Grafana
- **Alertas**: PagerDuty

---

*Última atualização: Setembro 2024*
