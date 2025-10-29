# 🚀 Guia de Setup para Desenvolvedores - Maiconsoft

> Instruções completas para configurar o ambiente de desenvolvimento do projeto Maiconsoft.

## 📋 Pré-requisitos Obrigatórios

### Java Development Kit (JDK)
```bash
# Verifique se o Java 17+ está instalado
java -version

# Deve retornar algo como:
# openjdk version "17.0.x" ou superior
```

### Maven
```bash
# Verifique se o Maven está instalado
mvn -version

# Deve retornar Maven 3.6+ ou superior
```

### PostgreSQL (Recomendado para Produção)
- **Download:** https://www.postgresql.org/download/
- **Versão:** 13+ ou superior
- **Porta padrão:** 5432

## ⚙️ Setup Passo a Passo

### 1. Clone o Repositório
```bash
git clone https://github.com/nisze/MaicoSoft.git
cd MaicoSoft
```

### 2. Configure o Banco de Dados

#### Opção A: PostgreSQL (Recomendado)
```sql
-- Conecte ao PostgreSQL como superuser
psql -U postgres

-- Crie o banco de dados
CREATE DATABASE maiconsoft;

-- Crie um usuário específico (opcional)
CREATE USER maiconsoft_user WITH PASSWORD 'sua_senha_aqui';
GRANT ALL PRIVILEGES ON DATABASE maiconsoft TO maiconsoft_user;
```

#### Opção B: H2 Database (Desenvolvimento)
- **Vantagem:** Não precisa instalar nada
- **Limitação:** Dados são perdidos ao reiniciar a aplicação

### 3. Configure o Application Properties

```bash
# Navegue até a pasta de recursos
cd maiconsoft_api/src/main/resources/

# Copie o arquivo de exemplo
cp application-example.properties application.properties
```

#### Edite o `application.properties`:

**Para PostgreSQL:**
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/maiconsoft
spring.datasource.username=maiconsoft_user
spring.datasource.password=sua_senha_aqui
spring.datasource.driver-class-name=org.postgresql.Driver

# Profile
spring.profiles.active=prod
```

**Para H2 (desenvolvimento rápido):**
```properties
# Database Configuration
spring.datasource.url=jdbc:h2:mem:maiconsoft
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Profile
spring.profiles.active=dev
```

#### Configure Email (Gmail exemplo):
```properties
# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seu_email@gmail.com
spring.mail.password=sua_senha_de_app_gmail
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

maiconsoft.email.from=seu_email@gmail.com
```

> **⚠️ Importante:** Para Gmail, use "senhas de aplicativo" ao invés da senha normal.

#### Configure JWT:
```properties
# Gere uma chave forte (recomendado: 64+ caracteres)
maiconsoft.jwt.secret=SuaChaveJWTMuitoForteLongaESeguraParaProducao123456789
maiconsoft.jwt.expiration=86400000
```

### 4. Execute a Aplicação

#### Backend (Spring Boot):
```bash
cd maiconsoft_api
mvn spring-boot:run
```

✅ **API estará rodando em:** `http://localhost:8090`

#### Frontend (Servidor Estático):

**Opção 1: Python (mais simples)**
```bash
cd frontend
python -m http.server 3001
```

**Opção 2: Node.js**
```bash
cd frontend
npx http-server -p 3001 -c-1
```

**Opção 3: VS Code Live Server**
1. Instale a extensão "Live Server"
2. Abra qualquer arquivo HTML em `frontend/pages/`
3. Clique com botão direito → "Open with Live Server"

✅ **Frontend estará rodando em:** `http://localhost:3001`

## 🧪 Testando a Instalação

### 1. Teste a API
```bash
# Teste de conectividade
curl http://localhost:8090/api/test

# Deve retornar: {"status": "API funcionando!"}
```

### 2. Teste o Frontend
- Acesse: http://localhost:3001/pages/login.html
- Use as credenciais de teste:
  - **Usuário:** `admin`
  - **Senha:** `123`

### 3. Teste o Email (Opcional)
- Cadastre um novo cliente no sistema
- Verifique se o email de boas-vindas foi enviado

## 🛠️ Comandos Úteis

### Maven
```bash
# Limpar e compilar
mvn clean compile

# Executar testes
mvn test

# Gerar JAR para produção
mvn clean package
```

### Git
```bash
# Verificar status
git status

# Adicionar mudanças
git add .

# Fazer commit
git commit -m "Sua mensagem aqui"

# Enviar para o repositório
git push origin master
```

### Banco de Dados
```bash
# Conectar ao PostgreSQL
psql -U maiconsoft_user -d maiconsoft

# Ver tabelas criadas
\dt

# Ver dados de uma tabela
SELECT * FROM clientes LIMIT 5;
```

## 🐛 Troubleshooting

### Erro: "Port 8090 already in use"
```bash
# Windows - Encontrar processo na porta 8090
netstat -ano | findstr :8090

# Matar processo (substitua PID)
taskkill /PID xxxx /F

# Linux/Mac - Encontrar e matar processo
lsof -ti:8090 | xargs kill -9
```

### Erro: "Could not connect to database"
1. Verifique se o PostgreSQL está rodando
2. Confirme as credenciais no `application.properties`
3. Teste a conexão manualmente:
```bash
psql -U maiconsoft_user -d maiconsoft -h localhost
```

### Erro: "JWT token invalid"
1. Verifique se a chave JWT no `application.properties` está configurada
2. Limpe o localStorage do navegador
3. Faça login novamente

### Frontend não carrega
1. Verifique se o servidor estático está rodando na porta 3001
2. Confirme se a API está rodando na porta 8090
3. Verifique o console do navegador para erros CORS

## 📧 Configuração de Email para Desenvolvimento

### Gmail (Recomendado)
1. Acesse sua conta Google
2. Vá em "Conta" → "Segurança"
3. Ative "Verificação em duas etapas"
4. Gere uma "Senha de app" para o Maiconsoft
5. Use essa senha no `application.properties`

### Outlook/Hotmail
```properties
spring.mail.host=smtp.live.com
spring.mail.port=587
spring.mail.username=seu_email@outlook.com
spring.mail.password=sua_senha_de_app
```

## 🔐 Variáveis de Ambiente (Produção)

Para produção, use variáveis de ambiente ao invés de hardcoded:

```bash
# Exemplo de .env para produção
export DB_URL=jdbc:postgresql://localhost:5432/maiconsoft
export DB_USERNAME=maiconsoft_user
export DB_PASSWORD=senha_super_forte
export JWT_SECRET=chave_jwt_ultra_secreta
export EMAIL_USERNAME=contato@maiconsoft.com
export EMAIL_PASSWORD=senha_email
```

## 📞 Suporte

Se tiver problemas:

1. **Verifique os logs** da aplicação em `maiconsoft_api/logs/`
2. **Consulte a documentação** no README.md
3. **Abra uma issue** no GitHub
4. **Entre em contato:** contato@maiconsoft.com.br

---

**Happy Coding! 🚀**