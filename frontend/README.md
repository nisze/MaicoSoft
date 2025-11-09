#  Maiconsoft Frontend - Sistema Navegvel

Frontend em HTML, CSS e JavaScript vanilla para o sistema Maiconsoft, com tema de construo civil.

##  **DEMONSTRAO PRONTA - TESTE AGORA!**

### ** Como Testar (3 opes):**

1. ** Docker (Recomendado):** Execute `cd scripts && start.bat` (sistema completo na porta 3000)
2. ** Live Server:** Use a extenso Live Server do VS Code
3. ** Servidor Local:** Execute `python -m http.server 8000` (ou outra porta livre)

### ** Credenciais de Teste:**

| **Usurio** | **Login** | **Senha** | **Acesso** |
|-------------|-----------|-----------|------------|
| **Admin** | `admin` | `123` | Dashboard + Clientes + Vendas + Relatrios |
| **Diretor** | `diretor` | `123` | **TODAS** as pginas |
| **Funcionrio** | `funcionario` | `123` | Apenas Clientes |

### ** Roteiro de Demonstrao:**

#### **1 Login (`index.html`):**
-  Interface profissional com tema construo civil
-  Validao de credenciais
-  Redirecionamento automtico por perfil

#### **2 Dashboard (`pages/dashboard.html`):**
-  KPIs e mtricas executivas
-  Aes rpidas navegveis
-  Atividades recentes
-  Menu de navegao completo

#### **3 Clientes (`pages/clientes.html`):**
-  Formulrio de cadastro com validao
-  Busca e listagem
-  Interface responsiva

#### **4 Vendas (`pages/vendas.html`):**
-  Gesto de vendas e oramentos
-  Formulrios profissionais
-  Tabelas organizadas

#### **5 Usurios (`pages/usuarios.html`):**
-  Administrao de usurios (Diretor only)
-  Controle de permisses
-  Status de usurios

#### **6 Relatrios (`pages/relatorios.html`):**
-  Filtros por perodo
-  Grficos e analytics
-  Exportao de dados

### ** Integrao Backend:**
- ** API Base:** `localhost:8090/api` (Docker: `http://backend:8090/api`)
- ** Endpoints:** auth, clientes, vendas, usuarios
- ** Fallback:** Modo offline para desenvolvimento

---

##  **SISTEMA PRONTO PARA DEMONSTRAO!**

### ** Iniciar Demonstrao:**

1. **Abrir arquivo:** Clique duas vezes em `index.html`
2. **Login rpido:** Use os botes coloridos na tela de login
3. **Navegao:** Use o menu superior para navegar entre pginas
4. **Teste funcionalidades:** Preencha formulrios, clique em botes

### ** Destaques da Demonstrao:**

-  **Interface Profissional:** Tema construo civil
-  **Navegao Fluida:** Pginas separadas com menu
-  **Controle de Acesso:** Diferentes perfis e permisses
-  **Formulrios Funcionais:** Validao e feedback
-  **Design Responsivo:** Funciona em desktop e mobile
-  **Integrao Pronta:** Backend Spring Boot localhost:8090
-  **Docker Ready:** Stack completo com um comando (Frontend: 3000, Backend: 8090, DB: 5432)

### ** Compatibilidade:**
-  Chrome, Firefox, Safari, Edge
-  Desktop e Mobile
-  Com ou sem servidor local

** PRONTO PARA APRESENTAR AO CLIENTE!**

##  Estrutura do Projeto

```
frontend/
 Dockerfile              # Container Nginx para frontend
 nginx.conf             # Configurao Nginx com proxy para backend
 pages/                 # Pginas HTML separadas
    base.html         # Template base
    login.html        # Pgina de login
    dashboard.html    # Dashboard administrativo
    clientes.html     # Gesto de clientes
    vendas.html       # Gesto de vendas
    usuarios.html     # Gesto de usurios
    relatorios.html   # Relatrios
 css/
    style.css         # Estilos principais
    login.css         # Estilos do login
    dashboard.css     # Estilos do dashboard
    cliente.css       # Estilos da gesto de clientes
    navigation.css    # Estilos de navegao
 js/
    app.js           # Aplicao principal
    config.js        # Configurao API
    login.js         # Mdulo de autenticao
    dashboard.js     # Mdulo do dashboard
    cliente.js       # Mdulo de gesto de clientes
 images/              # Imagens do sistema
 assets/              # Recursos adicionais
```

##  Design System

### Cores Principais
- **Primary**: `#FF6B35` (Laranja construo)
- **Secondary**: `#004E89` (Azul estrutural)
- **Accent**: `#F77F00` (Laranja vibrante)
- **Success**: `#22C55E` (Verde)
- **Warning**: `#FDE047` (Amarelo)
- **Error**: `#EF4444` (Vermelho)

### Tipografia
- **Fonte**: Inter (Google Fonts)
- **Pesos**: 300, 400, 500, 600, 700

### cones
- **Material Icons** do Google

##  Como Usar

### 1. Configurao Inicial

1. Coloque todos os arquivos na pasta `frontend/` do seu projeto
2. Certifique-se de que sua API Spring Boot est rodando
3. Abra o arquivo `index.html` em um navegador ou configure um servidor local

### 2. Configurao da API

No arquivo `js/config.js`, configure a URL da sua API:

```javascript
const API_CONFIG = {
    BASE_URL: 'http://localhost:8090/api', // Desenvolvimento local
    // No Docker, nginx proxy encaminha /api para backend:8090
};
```

### 3. Estrutura de Usurios

O sistema espera usurios com os seguintes perfis:
- **admin**: Acesso completo ao dashboard
- **diretor**: Acesso completo ao dashboard
- **funcionario**: Acesso apenas ao cadastro de clientes

### 4. Endpoints Esperados

O frontend espera os seguintes endpoints da API:

#### Autenticao
- `POST /auth/login` - Login do usurio
- `POST /auth/logout` - Logout do usurio

#### Clientes
- `GET /clientes` - Lista clientes (com paginao)
- `POST /clientes` - Cria novo cliente
- `GET /clientes/{id}` - Busca cliente por ID
- `PUT /clientes/{id}` - Atualiza cliente
- `DELETE /clientes/{id}` - Remove cliente

#### Dashboard (Admin/Diretor)
- `GET /dashboard/metrics` - Mtricas principais
- `GET /dashboard/charts` - Dados para grficos
- `GET /vendas` - Lista de vendas recentes

##  Funcionalidades

### Tela de Login
- Layout split-screen com imagem de construo
- Validao em tempo real dos campos
- Redirecionamento baseado no perfil do usurio
- Toggle para mostrar/ocultar senha
- Recuperao de senha

### Dashboard (Admin/Diretor)
- Mtricas principais (KPIs)
- Grficos de vendas e produtos
- Tabelas de clientes e vendas recentes
- Navegao por abas
- Atualizao automtica dos dados
- Exportao de relatrios

### Gesto de Clientes
- Formulrio completo de cadastro
- Busca automtica de endereo por CEP
- Validao de CPF, email e telefone
- Lista paginada de clientes
- Busca e filtros
- Edio e excluso de clientes
- Layout responsivo

##  Navegao Baseada em Perfil

### Funcionrio
- Login  Pgina de Cadastro de Clientes
- Acesso apenas a:
  - Cadastro de clientes
  - Lista de clientes
  - Busca de clientes

### Admin/Diretor
- Login  Dashboard
- Acesso completo a:
  - Dashboard com mtricas
  - Gesto de clientes
  - Relatrios e grficos

##  Responsividade

O sistema  totalmente responsivo com breakpoints:
- **Desktop**: > 1024px
- **Tablet**: 768px - 1024px
- **Mobile**: < 768px

### Adaptaes Mobile
- Menu colapsvel
- Tabelas adaptativas
- Formulrios otimizados
- Navegao por tabs touch-friendly

##  Validaes Implementadas

### Campos de Cliente
- **Nome**: Obrigatrio, mnimo 2 caracteres
- **Email**: Obrigatrio, formato vlido
- **Telefone**: Obrigatrio, formato brasileiro
- **CPF**: Formato e dgitos verificadores
- **CEP**: Formato brasileiro (00000-000)

### Integrao CEP
- Busca automtica via API ViaCEP
- Preenchimento automtico do endereo
- Tratamento de erros

##  Customizao

### Cores
Edite as variveis CSS no arquivo `css/style.css`:

```css
:root {
    --primary: #FF6B35;
    --secondary: #004E89;
    /* ... outras variveis */
}
```

### Logotipo
Substitua o cone Material no header por sua logo:

```html
<!-- Substitua esta linha no index.html -->
<i class="material-icons">engineering</i>
```

### Textos e Labels
Todos os textos esto em portugus e podem ser facilmente alterados nos arquivos HTML e JS.

##  Tratamento de Erros

O sistema inclui tratamento completo de erros:
- Conexo com a API
- Validao de formulrios
- Estados de carregamento
- Mensagens de feedback para o usurio
- Fallbacks para dados mockados em desenvolvimento

##  Performance

### Otimizaes Implementadas
- CSS com variveis customizadas
- JavaScript modular
- Lazy loading de dados
- Debounce em buscas
- Cache de autenticao

### Melhorias Sugeridas
- Minificao dos arquivos CSS/JS
- Compresso de imagens
- Service Worker para cache offline
- Lazy loading de mdulos JS

##  Segurana

### Medidas Implementadas
- Validao client-side e server-side
- Token JWT para autenticao
- Sanitizao de inputs
- Preveno de XSS bsica

### Recomendaes
- Implementar CSP (Content Security Policy)
- Validar tokens no servidor
- Rate limiting para login
- HTTPS obrigatrio em produo

##  Contribuio

Para contribuir com o projeto:
1. Mantenha o padro de cdigo estabelecido
2. Documente novas funcionalidades
3. Teste em diferentes dispositivos
4. Siga as convenes de nomenclatura

##  Suporte

Para dvidas sobre implementao:
1. Verifique os logs do console do navegador
2. Confirme se a API est respondendo corretamente
3. Teste com dados mockados primeiro
4. Verifique a configurao de CORS na API

---

**Desenvolvido com  para o setor de construo civil**