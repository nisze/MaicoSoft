# 🏗️ Maiconsoft - Sistema de Gestão para Construção Civil

> Sistema completo de gestão empresarial focado em construção civil, desenvolvido com Spring Boot e JavaScript moderno.

## 📋 Sobre o Projeto

O **Maiconsoft** é um sistema de gestão empresarial especializado para empresas de construção civil, oferecendo funcionalidades completas para:

- 👥 **Gestão de Clientes** - Cadastro, edição e controle de clientes
- 💰 **Gestão de Vendas** - Orçamentos, vendas e controle de status
- 🎫 **Sistema de Cupons** - Criação e gestão de cupons de desconto
- 👤 **Gestão de Usuários** - Controle de acesso e permissões
- 📊 **Relatórios Avançados** - Análises e métricas de performance
- 📧 **Notificações por Email** - Sistema automatizado de comunicação

## 🚀 Tecnologias Utilizadas

### Backend
- **Java 17+**
- **Spring Boot 3.x**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA**
- **PostgreSQL** (Produção)
- **H2 Database** (Desenvolvimento)
- **Maven**
- **Thymeleaf** (Templates de email)

### Frontend
- **HTML5** / **CSS3**
- **JavaScript ES6+**
- **Material Design Icons**
- **Responsive Design**

## 📁 Estrutura do Projeto

```
MAICOSOFT/
├── maiconsoft_api/          # Backend Spring Boot
│   ├── src/main/java/       # Código fonte Java
│   ├── src/main/resources/  # Recursos e configurações
│   ├── target/              # Build artifacts
│   └── pom.xml              # Dependências Maven
├── frontend/                # Frontend da aplicação
│   ├── js/                  # Scripts JavaScript
│   ├── css/                 # Estilos CSS
│   ├── pages/               # Páginas HTML
│   └── images/              # Recursos de imagem
├── .gitignore               # Arquivos ignorados pelo Git
└── README.md                # Documentação do projeto
```

## ⚙️ Configuração e Instalação

### Pré-requisitos

- **Java 17+** instalado
- **Maven 3.6+** instalado
- **PostgreSQL** (para produção) ou usar H2 (desenvolvimento)
- **Node.js** (opcional, para servir o frontend)

### 🔧 Configuração do Backend

1. **Clone o repositório:**
```bash
git clone https://github.com/nisze/MaicoSoft.git
cd MaicoSoft
```

2. **Configure o banco de dados:**
```bash
# Copie o arquivo de exemplo
cp maiconsoft_api/src/main/resources/application-example.properties maiconsoft_api/src/main/resources/application.properties
```

3. **Edite o `application.properties` com suas configurações:**
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/maiconsoft
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

# Email
spring.mail.username=seu_email@gmail.com
spring.mail.password=sua_senha_de_app

# JWT
maiconsoft.jwt.secret=sua_chave_jwt_muito_forte
```

4. **Execute a aplicação:**
```bash
cd maiconsoft_api
mvn spring-boot:run
```

A API estará disponível em `http://localhost:8090`

### 🖥️ Configuração do Frontend

1. **Navegue até a pasta frontend:**
```bash
cd frontend
```

2. **Sirva os arquivos estáticos:**

**Opção 1: Python (simples)**
```bash
python -m http.server 3001
```

**Opção 2: Node.js (http-server)**
```bash
npx http-server -p 3001 -c-1
```

**Opção 3: Live Server (VS Code)**
- Instale a extensão "Live Server"
- Clique com botão direito em qualquer arquivo HTML
- Selecione "Open with Live Server"

O frontend estará disponível em `http://localhost:3001`

## 📊 Funcionalidades Principais

### 👥 Gestão de Clientes
- ✅ Cadastro completo de clientes (PF/PJ)
- ✅ Busca e filtros avançados
- ✅ Histórico de vendas por cliente
- ✅ Notificações automáticas por email

### 💰 Gestão de Vendas
- ✅ Criação de orçamentos e vendas
- ✅ Controle de status (Pendente, Confirmada, Finalizada, Cancelada)
- ✅ Aplicação de cupons de desconto
- ✅ Relatórios de vendas

### 🎫 Sistema de Cupons
- ✅ Criação de cupons com desconto percentual ou fixo
- ✅ Controle de validade e usos
- ✅ Status automático (Ativo, Inativo, Expirado, Esgotado)
- ✅ Aplicação automática em vendas

### 📊 Relatórios e Dashboard
- ✅ Dashboard com métricas em tempo real
- ✅ Relatórios de vendas por período
- ✅ Análise de performance
- ✅ Exportação em PDF e Excel

## 🔐 Segurança

- **JWT Authentication** - Autenticação baseada em tokens
- **Role-based Access Control** - Controle de acesso por perfis
- **Password Hashing** - Senhas criptografadas com BCrypt
- **CORS Configuration** - Configuração segura de CORS
- **SQL Injection Protection** - Proteção via JPA/Hibernate

## 🌐 API Endpoints

### Autenticação
- `POST /api/auth/login` - Login do usuário
- `POST /api/auth/register` - Registro de novo usuário

### Clientes
- `GET /api/clientes` - Listar clientes
- `POST /api/clientes` - Criar cliente
- `PUT /api/clientes/{id}` - Atualizar cliente
- `DELETE /api/clientes/{id}` - Excluir cliente

### Vendas
- `GET /api/vendas` - Listar vendas
- `POST /api/vendas` - Criar venda
- `PUT /api/vendas/{id}` - Atualizar venda
- `DELETE /api/vendas/{id}` - Excluir venda

### Cupons
- `GET /api/cupons` - Listar cupons
- `POST /api/cupons` - Criar cupom
- `PUT /api/cupons/{id}` - Atualizar cupom
- `DELETE /api/cupons/{id}` - Excluir cupom

## 📧 Sistema de Email

O sistema possui templates de email profissionais para:

- **Boas-vindas** - Email automático ao cadastrar cliente
- **Notificações de venda** - Confirmação de vendas realizadas
- **Marketing** - Comunicação promocional

## 🔄 Profiles de Execução

- **`dev`** - Desenvolvimento (H2 Database)
- **`prod`** - Produção (PostgreSQL)
- **`sqlserver`** - SQL Server (empresarial)

## 📝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📞 Suporte

- **Email:** contato@maiconsoft.com.br
- **Telefone:** (11) 9999-9999
- **Endereço:** São Paulo, SP - Brasil

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para detalhes.

## 🏆 Autores

- **Desenvolvedor Principal** - [@nisze](https://github.com/nisze)

---

**Maiconsoft** - *Construindo o futuro com tecnologia e inovação* 🚀