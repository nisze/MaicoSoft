# 🏗️ Maiconsoft Frontend - Sistema Navegável

Frontend em HTML, CSS e JavaScript vanilla para o sistema Maiconsoft, com tema de construção civil.

## 🚀 **DEMONSTRAÇÃO PRONTA - TESTE AGORA!**

### **⚡ Como Testar (3 opções):**

1. **📁 Arquivo Direto:** Abra `index.html` no navegador
2. **🌐 Live Server:** Use a extensão Live Server do VS Code
3. **💻 Servidor Local:** Execute `python -m http.server 3000`

### **🔑 Credenciais de Teste:**

| **Usuário** | **Login** | **Senha** | **Acesso** |
|-------------|-----------|-----------|------------|
| **Admin** | `admin` | `123` | Dashboard + Clientes + Vendas + Relatórios |
| **Diretor** | `diretor` | `123` | **TODAS** as páginas |
| **Funcionário** | `funcionario` | `123` | Apenas Clientes |

### **🎯 Roteiro de Demonstração:**

#### **1️⃣ Login (`index.html`):**
- ✅ Interface profissional com tema construção civil
- ✅ Validação de credenciais
- ✅ Redirecionamento automático por perfil

#### **2️⃣ Dashboard (`pages/dashboard.html`):**
- ✅ KPIs e métricas executivas
- ✅ Ações rápidas navegáveis
- ✅ Atividades recentes
- ✅ Menu de navegação completo

#### **3️⃣ Clientes (`pages/clientes.html`):**
- ✅ Formulário de cadastro com validação
- ✅ Busca e listagem
- ✅ Interface responsiva

#### **4️⃣ Vendas (`pages/vendas.html`):**
- ✅ Gestão de vendas e orçamentos
- ✅ Formulários profissionais
- ✅ Tabelas organizadas

#### **5️⃣ Usuários (`pages/usuarios.html`):**
- ✅ Administração de usuários (Diretor only)
- ✅ Controle de permissões
- ✅ Status de usuários

#### **6️⃣ Relatórios (`pages/relatorios.html`):**
- ✅ Filtros por período
- ✅ Gráficos e analytics
- ✅ Exportação de dados

### **🔧 Integração Backend:**
- **✅ API Base:** `localhost:8080/api` 
- **✅ Endpoints:** auth, clientes, vendas, usuarios
- **✅ Fallback:** Modo offline para desenvolvimento

---

## 🎉 **SISTEMA PRONTO PARA DEMONSTRAÇÃO!**

### **🚀 Iniciar Demonstração:**

1. **Abrir arquivo:** Clique duas vezes em `index.html`
2. **Login rápido:** Use os botões coloridos na tela de login
3. **Navegação:** Use o menu superior para navegar entre páginas
4. **Teste funcionalidades:** Preencha formulários, clique em botões

### **✨ Destaques da Demonstração:**

- ✅ **Interface Profissional:** Tema construção civil
- ✅ **Navegação Fluida:** Páginas separadas com menu
- ✅ **Controle de Acesso:** Diferentes perfis e permissões
- ✅ **Formulários Funcionais:** Validação e feedback
- ✅ **Design Responsivo:** Funciona em desktop e mobile
- ✅ **Integração Pronta:** Backend Spring Boot localhost:8080

### **📱 Compatibilidade:**
- ✅ Chrome, Firefox, Safari, Edge
- ✅ Desktop e Mobile
- ✅ Com ou sem servidor local

**🎯 PRONTO PARA APRESENTAR AO CLIENTE!**

## 📁 Estrutura do Projeto

```
frontend/
├── index.html              # Página principal com todas as telas
├── css/
│   ├── style.css          # Estilos principais e framework
│   ├── login.css          # Estilos específicos do login
│   ├── dashboard.css      # Estilos do dashboard administrativo
│   ├── cliente.css        # Estilos da gestão de clientes
│   └── notifications.css  # Sistema de notificações
├── js/
│   ├── app.js            # Aplicação principal e roteamento
│   ├── login.js          # Módulo de autenticação
│   ├── dashboard.js      # Módulo do dashboard
│   └── cliente.js        # Módulo de gestão de clientes
└── README.md             # Esta documentação
```

## 🎨 Design System

### Cores Principais
- **Primary**: `#FF6B35` (Laranja construção)
- **Secondary**: `#004E89` (Azul estrutural)
- **Accent**: `#F77F00` (Laranja vibrante)
- **Success**: `#22C55E` (Verde)
- **Warning**: `#FDE047` (Amarelo)
- **Error**: `#EF4444` (Vermelho)

### Tipografia
- **Fonte**: Inter (Google Fonts)
- **Pesos**: 300, 400, 500, 600, 700

### Ícones
- **Material Icons** do Google

## 🚀 Como Usar

### 1. Configuração Inicial

1. Coloque todos os arquivos na pasta `frontend/` do seu projeto
2. Certifique-se de que sua API Spring Boot está rodando
3. Abra o arquivo `index.html` em um navegador ou configure um servidor local

### 2. Configuração da API

No arquivo `js/app.js`, configure a URL da sua API:

```javascript
// Linha ~15
this.baseURL = 'http://localhost:8080/api'; // Ajuste conforme necessário
```

### 3. Estrutura de Usuários

O sistema espera usuários com os seguintes perfis:
- **admin**: Acesso completo ao dashboard
- **diretor**: Acesso completo ao dashboard
- **funcionario**: Acesso apenas ao cadastro de clientes

### 4. Endpoints Esperados

O frontend espera os seguintes endpoints da API:

#### Autenticação
- `POST /auth/login` - Login do usuário
- `POST /auth/logout` - Logout do usuário

#### Clientes
- `GET /clientes` - Lista clientes (com paginação)
- `POST /clientes` - Cria novo cliente
- `GET /clientes/{id}` - Busca cliente por ID
- `PUT /clientes/{id}` - Atualiza cliente
- `DELETE /clientes/{id}` - Remove cliente

#### Dashboard (Admin/Diretor)
- `GET /dashboard/metrics` - Métricas principais
- `GET /dashboard/charts` - Dados para gráficos
- `GET /vendas` - Lista de vendas recentes

## 📱 Funcionalidades

### Tela de Login
- Layout split-screen com imagem de construção
- Validação em tempo real dos campos
- Redirecionamento baseado no perfil do usuário
- Toggle para mostrar/ocultar senha
- Recuperação de senha

### Dashboard (Admin/Diretor)
- Métricas principais (KPIs)
- Gráficos de vendas e produtos
- Tabelas de clientes e vendas recentes
- Navegação por abas
- Atualização automática dos dados
- Exportação de relatórios

### Gestão de Clientes
- Formulário completo de cadastro
- Busca automática de endereço por CEP
- Validação de CPF, email e telefone
- Lista paginada de clientes
- Busca e filtros
- Edição e exclusão de clientes
- Layout responsivo

## 🎯 Navegação Baseada em Perfil

### Funcionário
- Login → Página de Cadastro de Clientes
- Acesso apenas a:
  - Cadastro de clientes
  - Lista de clientes
  - Busca de clientes

### Admin/Diretor
- Login → Dashboard
- Acesso completo a:
  - Dashboard com métricas
  - Gestão de clientes
  - Relatórios e gráficos

## 📱 Responsividade

O sistema é totalmente responsivo com breakpoints:
- **Desktop**: > 1024px
- **Tablet**: 768px - 1024px
- **Mobile**: < 768px

### Adaptações Mobile
- Menu colapsável
- Tabelas adaptativas
- Formulários otimizados
- Navegação por tabs touch-friendly

## 🛠️ Validações Implementadas

### Campos de Cliente
- **Nome**: Obrigatório, mínimo 2 caracteres
- **Email**: Obrigatório, formato válido
- **Telefone**: Obrigatório, formato brasileiro
- **CPF**: Formato e dígitos verificadores
- **CEP**: Formato brasileiro (00000-000)

### Integração CEP
- Busca automática via API ViaCEP
- Preenchimento automático do endereço
- Tratamento de erros

## 🔧 Customização

### Cores
Edite as variáveis CSS no arquivo `css/style.css`:

```css
:root {
    --primary: #FF6B35;
    --secondary: #004E89;
    /* ... outras variáveis */
}
```

### Logotipo
Substitua o ícone Material no header por sua logo:

```html
<!-- Substitua esta linha no index.html -->
<i class="material-icons">engineering</i>
```

### Textos e Labels
Todos os textos estão em português e podem ser facilmente alterados nos arquivos HTML e JS.

## 🚨 Tratamento de Erros

O sistema inclui tratamento completo de erros:
- Conexão com a API
- Validação de formulários
- Estados de carregamento
- Mensagens de feedback para o usuário
- Fallbacks para dados mockados em desenvolvimento

## 📈 Performance

### Otimizações Implementadas
- CSS com variáveis customizadas
- JavaScript modular
- Lazy loading de dados
- Debounce em buscas
- Cache de autenticação

### Melhorias Sugeridas
- Minificação dos arquivos CSS/JS
- Compressão de imagens
- Service Worker para cache offline
- Lazy loading de módulos JS

## 🔐 Segurança

### Medidas Implementadas
- Validação client-side e server-side
- Token JWT para autenticação
- Sanitização de inputs
- Prevenção de XSS básica

### Recomendações
- Implementar CSP (Content Security Policy)
- Validar tokens no servidor
- Rate limiting para login
- HTTPS obrigatório em produção

## 🤝 Contribuição

Para contribuir com o projeto:
1. Mantenha o padrão de código estabelecido
2. Documente novas funcionalidades
3. Teste em diferentes dispositivos
4. Siga as convenções de nomenclatura

## 📞 Suporte

Para dúvidas sobre implementação:
1. Verifique os logs do console do navegador
2. Confirme se a API está respondendo corretamente
3. Teste com dados mockados primeiro
4. Verifique a configuração de CORS na API

---

**Desenvolvido com ❤️ para o setor de construção civil**