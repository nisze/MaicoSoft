# 🏗️ Sistema Maiconsoft - Navegação por Páginas Separadas

## 📁 **Estrutura de Páginas**

O sistema agora está organizado em páginas HTML separadas para melhor navegação e organização:

```
frontend/
├── index.html              # Página de login
├── pages/
│   ├── dashboard.html      # Dashboard executivo (Admin/Diretor)
│   ├── clientes.html       # Gestão de clientes (Todos)
│   ├── vendas.html         # Gestão de vendas (Admin/Diretor)
│   ├── usuarios.html       # Gestão de usuários (Diretor)
│   └── relatorios.html     # Relatórios e análises (Admin/Diretor)
├── css/                    # Estilos
├── js/                     # Scripts JavaScript
└── assets/                 # Recursos
```

## 🚀 **Como Navegar**

### 1. **Página de Login (`index.html`)**
- Página inicial do sistema
- Autentica usuários e redireciona baseado no perfil

### 2. **Dashboard (`pages/dashboard.html`)**
- **Acesso:** Admin e Diretor
- **Funcionalidades:**
  - KPIs e métricas do negócio
  - Ações rápidas
  - Atividades recentes
  - Navegação para outras páginas

### 3. **Clientes (`pages/clientes.html`)**
- **Acesso:** Todos os usuários
- **Funcionalidades:**
  - Cadastro de novos clientes
  - Lista e busca de clientes
  - Edição e exclusão
  - Validação de formulários

### 4. **Vendas (`pages/vendas.html`)**
- **Acesso:** Admin e Diretor
- **Funcionalidades:**
  - Registro de novas vendas
  - Lista de vendas recentes
  - Gestão de orçamentos
  - Relatórios de vendas

### 5. **Usuários (`pages/usuarios.html`)**
- **Acesso:** Apenas Diretor
- **Funcionalidades:**
  - Cadastro de novos usuários
  - Gestão de perfis e permissões
  - Ativação/desativação de contas
  - Lista de usuários ativos

### 6. **Relatórios (`pages/relatorios.html`)**
- **Acesso:** Admin e Diretor
- **Funcionalidades:**
  - Relatórios de vendas por período
  - Estatísticas de clientes
  - Análise de performance
  - Gráficos e visualizações

## 🔐 **Sistema de Autenticação**

### **Credenciais de Teste:**
| Usuário | Login | Senha | Páginas Acessíveis |
|---------|-------|-------|-------------------|
| **Admin** | `admin` | `123` | Dashboard, Clientes, Vendas, Relatórios |
| **Diretor** | `diretor` | `123` | Todas as páginas |
| **Funcionário** | `funcionario` | `123` | Apenas Clientes |

### **Controle de Acesso:**
- Cada página verifica automaticamente se o usuário tem permissão
- Redirecionamento automático em caso de acesso negado
- Sessão mantida entre as páginas

## 🌐 **Links Diretos para Teste**

### **Desenvolvimento Local:**
- **Login:** `http://localhost:8080/index.html`
- **Dashboard:** `http://localhost:8080/pages/dashboard.html`
- **Clientes:** `http://localhost:8080/pages/clientes.html`
- **Vendas:** `http://localhost:8080/pages/vendas.html`
- **Usuários:** `http://localhost:8080/pages/usuarios.html`
- **Relatórios:** `http://localhost:8080/pages/relatorios.html`

### **File System (Desenvolvimento):**
- **Login:** `file:///C:/caminho/frontend/index.html`
- **Dashboard:** `file:///C:/caminho/frontend/pages/dashboard.html`
- **Clientes:** `file:///C:/caminho/frontend/pages/clientes.html`
- **Vendas:** `file:///C:/caminho/frontend/pages/vendas.html`
- **Usuários:** `file:///C:/caminho/frontend/pages/usuarios.html`
- **Relatórios:** `file:///C:/caminho/frontend/pages/relatorios.html`

## 🔧 **Integração com Backend**

### **Configuração:**
- Backend Spring Boot rodando em `http://localhost:8080/api`
- Detecção automática da disponibilidade do backend
- Modo simulação quando backend não disponível

### **Endpoints Principais:**
```javascript
/api/auth/login          // Autenticação
/api/clientes           // CRUD de clientes
/api/vendas             // CRUD de vendas  
/api/user               // Dados do usuário
/api/relatorios         // Dados para relatórios
```

## 🎨 **Características do Design**

- **Tema:** Construção civil
- **Cores:** #FF6B35 (laranja), #004E89 (azul)
- **Responsivo:** Funciona em desktop, tablet e mobile
- **Ícones:** Material Design Icons
- **Navegação:** Intuitiva com breadcrumbs

## 🚀 **Como Iniciar**

1. **Iniciar Backend:**
   ```bash
   cd maiconsoft_api
   mvn spring-boot:run
   ```

2. **Abrir Frontend:**
   - Abra `index.html` no navegador
   - Ou sirva via servidor HTTP local

3. **Fazer Login:**
   - Use as credenciais de teste
   - Sistema redirecionará automaticamente

4. **Navegar:**
   - Use o menu de navegação
   - Ou acesse diretamente as URLs das páginas

## ✅ **Vantagens das Páginas Separadas**

- ✅ **Performance:** Carregamento mais rápido
- ✅ **Organização:** Código mais limpo e manutenível
- ✅ **SEO:** URLs específicas para cada funcionalidade
- ✅ **Histórico:** Navegação com botão voltar do browser
- ✅ **Bookmarks:** Possibilidade de favoritar páginas específicas
- ✅ **Desenvolvimento:** Facilita o trabalho em equipe
- ✅ **Deploy:** Possibilidade de versionar páginas independentemente

Agora você pode navegar livremente entre as páginas! 🎉