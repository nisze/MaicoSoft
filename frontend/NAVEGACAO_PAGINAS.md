#  Sistema Maiconsoft - Navegao por Pginas Separadas

##  **Estrutura de Pginas**

O sistema agora est organizado em pginas HTML separadas para melhor navegao e organizao:

```
frontend/
 index.html              # Pgina de login
 pages/
    dashboard.html      # Dashboard executivo (Admin/Diretor)
    clientes.html       # Gesto de clientes (Todos)
    vendas.html         # Gesto de vendas (Admin/Diretor)
    usuarios.html       # Gesto de usurios (Diretor)
    relatorios.html     # Relatrios e anlises (Admin/Diretor)
 css/                    # Estilos
 js/                     # Scripts JavaScript
 assets/                 # Recursos
```

##  **Como Navegar**

### 1. **Pgina de Login (`index.html`)**
- Pgina inicial do sistema
- Autentica usurios e redireciona baseado no perfil

### 2. **Dashboard (`pages/dashboard.html`)**
- **Acesso:** Admin e Diretor
- **Funcionalidades:**
  - KPIs e mtricas do negcio
  - Aes rpidas
  - Atividades recentes
  - Navegao para outras pginas

### 3. **Clientes (`pages/clientes.html`)**
- **Acesso:** Todos os usurios
- **Funcionalidades:**
  - Cadastro de novos clientes
  - Lista e busca de clientes
  - Edio e excluso
  - Validao de formulrios

### 4. **Vendas (`pages/vendas.html`)**
- **Acesso:** Admin e Diretor
- **Funcionalidades:**
  - Registro de novas vendas
  - Lista de vendas recentes
  - Gesto de oramentos
  - Relatrios de vendas

### 5. **Usurios (`pages/usuarios.html`)**
- **Acesso:** Apenas Diretor
- **Funcionalidades:**
  - Cadastro de novos usurios
  - Gesto de perfis e permisses
  - Ativao/desativao de contas
  - Lista de usurios ativos

### 6. **Relatrios (`pages/relatorios.html`)**
- **Acesso:** Admin e Diretor
- **Funcionalidades:**
  - Relatrios de vendas por perodo
  - Estatsticas de clientes
  - Anlise de performance
  - Grficos e visualizaes

##  **Sistema de Autenticao**

### **Credenciais de Teste:**
| Usurio | Login | Senha | Perfil | Pginas Acessveis |
|---------|-------|-------|--------|-------------------|
| **Admin** | `ADM001` | `123456` | ADMIN | Dashboard, Clientes, Vendas, Relatrios, Usurios |
| **Vendedor** | `W36K0D` | `123456` | VENDEDOR | Clientes, Vendas |

### **Controle de Acesso:**
- Cada pgina verifica automaticamente se o usurio tem permisso
- Redirecionamento automtico em caso de acesso negado
- Sesso mantida entre as pginas

##  **Acesso ao Sistema**

### **Docker (Recomendado):**
```bash
# Iniciar sistema completo
cd scripts
start.bat
```

**URLs de Acesso:**
- **Login:** `http://localhost:3000/pages/login.html`
- **Dashboard:** `http://localhost:3000/pages/dashboard.html`
- **Clientes:** `http://localhost:3000/pages/clientes.html`
- **Vendas:** `http://localhost:3000/pages/vendas.html`
- **Usurios:** `http://localhost:3000/pages/usuarios.html`
- **Relatrios:** `http://localhost:3000/pages/relatorios.html`
- **Perfil:** `http://localhost:3000/pages/perfil.html`

### **Desenvolvimento Local (Backend separado):**
```bash
# Terminal 1: Backend
cd maiconsoft_api
mvn spring-boot:run

# Terminal 2: Frontend
cd frontend
python -m http.server 8000
```

**URLs de Acesso:**
- Frontend: `http://localhost:8000/pages/login.html`
- Backend API: `http://localhost:8090/api`

### **Servidores Alternativos:**
```bash
# Live Server (VS Code)
# Clique direito em login.html > Open with Live Server

# Node.js http-server
npx http-server frontend -p 8000
```

##  **Integrao com Backend**

### **Configurao Docker:**
```yaml
Frontend: Nginx (porta 3000)
   /api/* proxy_pass
Backend: Spring Boot (porta 8090)
   JDBC
Database: PostgreSQL (porta 5432)
```

### **Configurao Local:**
- Backend: `http://localhost:8090/api`
- Frontend: Qualquer servidor HTTP (ex: porta 8000)
- Configurar em `js/config.js`:
  ```javascript
  const API_CONFIG = {
      BASE_URL: 'http://localhost:8090/api'
  };
  ```

### **Endpoints Principais:**
```javascript
/api/auth/login          // Autenticao
/api/usuarios            // CRUD de usurios
/api/clientes            // CRUD de clientes
/api/vendas              // CRUD de vendas
/api/cupons              // CRUD de cupons
/api/relatorios/**       // Relatrios diversos
/api/usuarios/{id}/foto  // Upload foto perfil
```

##  **Caractersticas do Design**

- **Tema:** Construo civil
- **Cores:** #FF6B35 (laranja), #004E89 (azul)
- **Responsivo:** Funciona em desktop, tablet e mobile
- **cones:** Material Design Icons
- **Navegao:** Intuitiva com breadcrumbs

##  **Como Iniciar**

### **Opo 1: Docker (Recomendado)**
```bash
cd scripts
start.bat          # Inicia tudo
logs.bat           # Ver logs
status.bat         # Ver status
stop.bat           # Parar tudo
```

Acesse: `http://localhost:3000/pages/login.html`

### **Opo 2: Desenvolvimento Local**
```bash
# Terminal 1: Backend
cd maiconsoft_api
mvn spring-boot:run

# Terminal 2: Frontend
cd frontend
python -m http.server 8000
```

Acesse: `http://localhost:8000/pages/login.html`

### **Login:**
- Use as credenciais: `ADM001` / `123456` ou `W36K0D` / `123456`
- Sistema redirecionar baseado no perfil

### **Navegao:**
- Menu superior para mudar de pgina
- Boto voltar do navegador funciona
- URLs podem ser compartilhadas/bookmarked

##  **Vantagens das Pginas Separadas**

-  **Performance:** Carregamento mais rpido
-  **Organizao:** Cdigo mais limpo e manutenvel
-  **SEO:** URLs especficas para cada funcionalidade
-  **Histrico:** Navegao com boto voltar do browser
-  **Bookmarks:** Possibilidade de favoritar pginas especficas
-  **Desenvolvimento:** Facilita o trabalho em equipe
-  **Deploy:** Possibilidade de versionar pginas independentemente
-  **Docker:** Nginx serve pginas estticas de forma eficiente

##  **Infraestrutura Docker**

### **Stack Completa:**
```yaml
Services:
  - postgres: Banco de dados PostgreSQL 15
  - backend: Spring Boot API (Java 17)
  - frontend: Nginx web server

Portas:
  - Frontend: 3000 (HTTP)
  - Backend: 8090 (API)
  - Database: 5432 (PostgreSQL)

Volumes:
  - postgres_data: Dados persistentes do banco
  - backend_uploads: Fotos de perfil
  - backend_logs: Logs da aplicao
```

### **Comandos teis:**
```bash
# Iniciar
cd scripts && start.bat

# Ver logs em tempo real
cd scripts && logs.bat

# Ver status e recursos
cd scripts && status.bat

# Reconstruir aps mudanas
cd scripts && rebuild.bat

# Reset completo (apaga dados)
cd scripts && reset.bat

# Parar tudo
cd scripts && stop.bat
```

Agora voc pode navegar livremente entre as pginas! 