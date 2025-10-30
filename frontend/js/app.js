/**
 * Maiconsoft Frontend - JavaScript Principal
 * Sistema de gestão para construção civil
 * Integração com API Spring Boot
 */

// Definições de permissões
const PERMISSIONS = {
    USUARIOS: {
        VIEW: 'usuarios.view',
        CREATE: 'usuarios.create',
        EDIT: 'usuarios.edit',
        DELETE: 'usuarios.delete'
    },
    RELATORIOS: {
        VIEW: 'relatorios.view',
        EXPORT: 'relatorios.export'
    },
    VENDAS: {
        VIEW: 'vendas.view',
        CREATE: 'vendas.create',
        EDIT: 'vendas.edit',
        DELETE: 'vendas.delete'
    },
    CLIENTES: {
        VIEW: 'clientes.view',
        CREATE: 'clientes.create',
        EDIT: 'clientes.edit',
        DELETE: 'clientes.delete'
    }
};

// Configuração da API
const API_CONFIG = {
    baseURL: 'http://localhost:8090/api',
    endpoints: {
        auth: {
            login: '/auth/login',
            register: '/auth/register',
            test: '/auth/test'
        },
        clientes: {
            base: '/clientes',
            search: '/clientes/search'
        },
        vendas: {
            base: '/vendas',
            byOrcamento: '/vendas/orcamento'
        },
        user: '/user'
    }
};

// Cliente da API
class ApiClient {
    constructor() {
        this.baseURL = API_CONFIG.baseURL;
        this.token = localStorage.getItem('authToken') || 'demo-token'; // Demo token for testing
        console.log('🔑 Token sendo usado:', this.token ? 'Token encontrado' : 'Sem token');
    }

    async request(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        
        const config = {
            method: options.method || 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                ...(this.token && { 'Authorization': `Bearer ${this.token}` }),
                ...options.headers
            },
            ...options
        };

        if (options.body && typeof options.body === 'object') {
            config.body = JSON.stringify(options.body);
        }

        try {
            console.log(`🚀 API Request: ${config.method} ${url}`, config.body ? JSON.parse(config.body) : '');
            
            const response = await fetch(url, config);
            
            if (!response.ok) {
                const errorData = await response.text();
                console.error(`❌ API Error: ${response.status}`, errorData);
                throw new Error(`HTTP ${response.status}: ${errorData}`);
            }

            const data = await response.json();
            console.log('✅ API Response:', data);
            return data;
            
        } catch (error) {
            console.error('🔥 API Request Failed:', error);
            throw error;
        }
    }

    async login(codigoAcesso, senha) {
        const data = await this.request('/auth/login', {
            method: 'POST',
            body: { codigoAcesso, senha }
        });
        
        if (data.token) {
            this.token = data.token;
            localStorage.setItem('authToken', data.token);
        }
        
        return data;
    }

    async testConnection() {
        try {
            await this.request('/auth/test', { method: 'POST' });
            return true;
        } catch (error) {
            return false;
        }
    }

    async getClientes(page = 0, size = 10) {
        return await this.request(`/clientes?page=${page}&size=${size}`);
    }

    async createCliente(clienteData) {
        return await this.request('/clientes', {
            method: 'POST',
            body: clienteData
        });
    }

    // ============================================
    // VENDAS API METHODS
    // ============================================

    async getVendas() {
        return await this.request('/vendas', {
            method: 'GET'
        });
    }

    async getVenda(id) {
        return await this.request(`/vendas/${id}`, {
            method: 'GET'
        });
    }

    async createVenda(vendaData) {
        return await this.request('/vendas', {
            method: 'POST',
            body: vendaData
        });
    }

    async updateVenda(id, vendaData) {
        return await this.request(`/vendas/${id}`, {
            method: 'PUT',
            body: vendaData
        });
    }

    async deleteVenda(id) {
        return await this.request(`/vendas/${id}`, {
            method: 'DELETE'
        });
    }
}

// Instância global da API
const apiClient = new ApiClient();

// Função para testar conexão com backend
async function testBackendConnection() {
    try {
        const response = await fetch(`${API_CONFIG.baseURL}/auth/test`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });
        return response.ok;
    } catch (error) {
        console.warn('Backend não disponível, usando modo offline:', error);
        return false;
    }
}

// Estado da aplicação
const AppState = {
    user: null,
    isAuthenticated: false,
    currentPage: 'login',
    loading: false
};

// A primeira classe ApiClient (linha 29) já contém toda a funcionalidade necessária

// Autenticação
class AuthService {
    static async login(codigoAcesso, senha) {
        try {
            // Testa conexão com backend
            const backendAvailable = await testBackendConnection();
            
            let response;
            if (backendAvailable) {
                // Login real com backend
                response = await ApiClient.post(API_CONFIG.endpoints.auth.login, {
                    codigoAcesso,
                    senha
                });
            } else {
                // Simulação de login para desenvolvimento
                response = this.simulateLogin(codigoAcesso, senha);
            }

            // Salva dados do usuário - use both keys for compatibility
            localStorage.setItem('authToken', response.token || 'temp-token');
            localStorage.setItem('userData', JSON.stringify(response));
            localStorage.setItem('usuarioLogado', JSON.stringify(response)); // For compatibility
            
            AppState.user = response;
            AppState.isAuthenticated = true;

            // Redireciona baseado no perfil
            this.redirectByRole(response);
            
            showAlert('Login realizado com sucesso!', 'success');
            return response;
        } catch (error) {
            showAlert('Credenciais inválidas', 'error');
            throw error;
        }
    }

    static simulateLogin(codigoAcesso, senha) {
        // Simulação para desenvolvimento - remover em produção
        const users = {
            'admin': { perfil: 'admin', nome: 'Administrador', token: 'admin-token' },
            'diretor': { perfil: 'diretor', nome: 'Diretor', token: 'diretor-token' },
            'funcionario': { perfil: 'funcionario', nome: 'Funcionário', token: 'func-token' }
        };

        const user = users[codigoAcesso.toLowerCase()];
        if (user && senha === '123') {
            return user;
        }
        throw new Error('Credenciais inválidas');
    }

    static redirectByRole(user) {
        // Baseado no perfil do usuário - redirecionar para páginas corretas
        const perfil = user.perfil?.toLowerCase();
        const role = user.role?.toLowerCase();
        
        // Verificar se já estamos na pasta pages
        const isInPagesFolder = window.location.pathname.includes('/pages/');
        
        if (perfil === 'admin' || perfil === 'diretor' || role === 'admin') {
            if (isInPagesFolder) {
                window.location.href = 'dashboard.html';
            } else {
                window.location.href = 'pages/dashboard.html';
            }
        } else if (role === 'gerente') {
            if (isInPagesFolder) {
                window.location.href = 'dashboard.html';
            } else {
                window.location.href = 'pages/dashboard.html';
            }
        } else {
            if (isInPagesFolder) {
                window.location.href = 'clientes.html';
            } else {
                window.location.href = 'pages/clientes.html';
            }
        }
    }

    static logout() {
        // Limpar dados de autenticação
        localStorage.removeItem('authToken');
        localStorage.removeItem('userData');
        localStorage.removeItem('usuarioLogado');
        AppState.user = null;
        AppState.isAuthenticated = false;
        
        // Mostrar mensagem de logout
        showAlert('Logout realizado com sucesso', 'success');
        
        // Redirecionar para página de login após 1 segundo
        setTimeout(() => {
            if (window.location.pathname.includes('/pages/')) {
                // Se estiver em uma página interna, ir para login.html
                window.location.href = 'login.html';
            } else {
                // Se estiver na raiz, navegar para páginas/login.html
                window.location.href = 'pages/login.html';
            }
        }, 1000);
    }

    static checkAuth() {
        const token = localStorage.getItem('authToken');
        const userData = localStorage.getItem('userData');
        
        if (token && userData) {
            AppState.user = JSON.parse(userData);
            AppState.isAuthenticated = true;
            return true;
        }
        return false;
    }
}

// Navegação SPA com Sistema de Rotas Atualizado
const ROUTES = {
    login: { 
        title: 'Login',
        allowAnonymous: true,
        roles: []
    },
    dashboard: { 
        title: 'Dashboard',
        allowAnonymous: false,
        roles: ['admin', 'diretor']
    },
    cliente: { 
        title: 'Clientes',
        allowAnonymous: false,
        roles: ['admin', 'diretor', 'funcionario', 'vendedor']
    },
    vendas: { 
        title: 'Vendas',
        allowAnonymous: false,
        roles: ['admin', 'diretor', 'vendedor']
    },
    usuarios: { 
        title: 'Usuários',
        allowAnonymous: false,
        roles: ['diretor'],
        requiredPermission: PERMISSIONS.USUARIOS.VIEW
    },
    relatorios: { 
        title: 'Relatórios',
        allowAnonymous: false,
        roles: ['admin', 'diretor'],
        requiredPermission: PERMISSIONS.RELATORIOS.VIEW
    }
};

// Inicializar navegação
function initNavigation() {
    // Event listeners para links de navegação
    document.addEventListener('click', function(e) {
        // Links com data-route
        if (e.target.matches('[data-route]') || e.target.closest('[data-route]')) {
            e.preventDefault();
            const element = e.target.matches('[data-route]') ? e.target : e.target.closest('[data-route]');
            const route = element.getAttribute('data-route');
            navigateTo(route);
        }
        
        // Links com href="#route"
        if (e.target.matches('a[href^="#"]')) {
            e.preventDefault();
            const route = e.target.getAttribute('href').substring(1);
            navigateTo(route);
        }
    });

    // Navegação por histórico do browser
    window.addEventListener('popstate', function(e) {
        const route = e.state?.page || getRouteFromHash();
        navigateTo(route, false);
    });

    // Navegação inicial
    const initialRoute = getRouteFromHash() || getDefaultRoute();
    navigateTo(initialRoute, false);
}

function getRouteFromHash() {
    return window.location.hash.substring(1) || null;
}

function getDefaultRoute() {
    if (AppState.isAuthenticated && AppState.user) {
        const userRole = AppState.user.perfil?.toLowerCase();
        switch (userRole) {
            case 'funcionario':
                return 'cliente';
            case 'admin':
            case 'diretor':
                return 'dashboard';
            default:
                return 'cliente';
        }
    }
    return 'login';
}

function canUserAccessRoute(route, user = null) {
    const routeConfig = ROUTES[route];
    if (!routeConfig) return false;
    
    // Rotas que permitem acesso anônimo
    if (routeConfig.allowAnonymous) return true;
    
    // Verificar autenticação
    const currentUser = user || PermissionManager.getCurrentUser();
    if (!AppState.isAuthenticated || !currentUser) return false;
    
    // Verificar permissão específica se definida
    if (routeConfig.requiredPermission) {
        return PermissionManager.hasPermission(routeConfig.requiredPermission, currentUser.tipoUsuario);
    }
    
    // Fallback para verificação por role (compatibilidade)
    const userRole = currentUser.tipoUsuario?.toLowerCase();
    return routeConfig.roles.length === 0 || routeConfig.roles.includes(userRole);
}

function navigateTo(page, updateHistory = true) {
    // Verificar se a rota existe
    if (!ROUTES[page]) {
        console.warn(`Rota '${page}' não encontrada`);
        page = getDefaultRoute();
    }

    // Verificar permissões
    if (!canUserAccessRoute(page, AppState.user)) {
        showAlert('Acesso negado para esta página', 'error');
        
        // Redirecionar para página padrão
        const defaultRoute = getDefaultRoute();
        if (defaultRoute !== page) {
            navigateTo(defaultRoute, updateHistory);
        }
        return;
    }

    AppState.currentPage = page;
    
    // Esconde todas as páginas
    document.querySelectorAll('.page, .page-section').forEach(p => {
        p.style.display = 'none';
        p.classList.remove('active');
    });
    
    // Mostra página atual - tentar múltiplos seletores
    const possibleSelectors = [
        `#page-${page}`,
        `#${page}Page`,
        `#${page}-page`,
        `.page-${page}`,
        `[data-page="${page}"]`
    ];
    
    let currentPageElement = null;
    for (const selector of possibleSelectors) {
        currentPageElement = document.querySelector(selector);
        if (currentPageElement) break;
    }
    
    if (currentPageElement) {
        currentPageElement.style.display = 'block';
        currentPageElement.classList.add('active', 'fade-in');
        
        // Executar lógica específica da página
        executePageLogic(page);
    } else {
        console.warn(`Elemento da página '${page}' não encontrado`);
    }

    // Atualizar URL
    if (updateHistory) {
        window.location.hash = page;
    }
    
    // Atualizar navegação ativa
    updateNavigation();
    
    // Atualizar título da página
    document.title = `Maiconsoft - ${ROUTES[page]?.title || page}`;
}

function executePageLogic(page) {
    switch (page) {
        case 'dashboard':
            if (window.dashboardManager) {
                window.dashboardManager.loadDashboardData();
            }
            break;
        case 'cliente':
            if (window.clienteManager) {
                window.clienteManager.loadClients();
            }
            break;
        case 'login':
            // Limpar dados se necessário
            break;
    }
}

function updateNavigation() {
    // Remover classe active de todos os links
    document.querySelectorAll('.nav-link, .nav-tab, .sidebar-link, [data-route]').forEach(link => {
        link.classList.remove('active');
    });
    
    // Adicionar classe active para links da página atual
    const currentPage = AppState.currentPage;
    document.querySelectorAll(`[data-route="${currentPage}"], a[href="#${currentPage}"], [data-page="${currentPage}"]`).forEach(link => {
        link.classList.add('active');
    });
    
    // Atualizar breadcrumbs se existir
    updateBreadcrumbs();
}

function updateBreadcrumbs() {
    const breadcrumbs = document.querySelector('.breadcrumbs');
    if (!breadcrumbs) return;
    
    const currentRoute = ROUTES[AppState.currentPage];
    if (!currentRoute) return;
    
    let breadcrumbHTML = '<a href="#dashboard">Dashboard</a>';
    
    if (AppState.currentPage !== 'dashboard') {
        breadcrumbHTML += ` <span class="separator">></span> <span class="current">${currentRoute.title}</span>`;
    }
    
    breadcrumbs.innerHTML = breadcrumbHTML;
}

// Funções de utilidade para navegação
function goToLogin() {
    navigateTo('login');
}

function goToDashboard() {
    navigateTo('dashboard');
}

function goToClientes() {
    navigateTo('cliente');
}

function goToVendas() {
    navigateTo('vendas');
}

function goToUsuarios() {
    navigateTo('usuarios');
}

function goToRelatorios() {
    navigateTo('relatorios');
}

// Sistema de Alertas
function showAlert(message, type = 'success') {
    const alertContainer = document.getElementById('alert-container') || createAlertContainer();
    
    const alert = document.createElement('div');
    alert.className = `alert alert-${type} fade-in`;
    alert.textContent = message;
    
    alertContainer.appendChild(alert);
    
    // Remove após 5 segundos
    setTimeout(() => {
        alert.remove();
    }, 5000);
}

function createAlertContainer() {
    const container = document.createElement('div');
    container.id = 'alert-container';
    container.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 10000;
        max-width: 400px;
    `;
    document.body.appendChild(container);
    return container;
}

// Loading State
function updateLoadingState() {
    const loadingElements = document.querySelectorAll('.loading-indicator');
    loadingElements.forEach(el => {
        el.style.display = AppState.loading ? 'block' : 'none';
    });
}

// Validação de Formulários
class FormValidator {
    static validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }

    static validateCPF(cpf) {
        cpf = cpf.replace(/\D/g, '');
        return cpf.length === 11;
    }

    static validateCNPJ(cnpj) {
        cnpj = cnpj.replace(/\D/g, '');
        return cnpj.length === 14;
    }

    static validateRequired(value) {
        return value && value.trim().length > 0;
    }

    static showFieldError(fieldId, message) {
        const field = document.getElementById(fieldId);
        const errorElement = document.getElementById(`${fieldId}-error`);
        
        if (field) {
            field.classList.add('error');
            field.classList.add('shake');
            setTimeout(() => field.classList.remove('shake'), 300);
        }
        
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
        }
    }

    static clearFieldError(fieldId) {
        const field = document.getElementById(fieldId);
        const errorElement = document.getElementById(`${fieldId}-error`);
        
        if (field) {
            field.classList.remove('error');
            field.classList.add('success');
        }
        
        if (errorElement) {
            errorElement.style.display = 'none';
        }
    }
}

// Formatação de campos
class FieldFormatter {
    static formatCPF(value) {
        return value
            .replace(/\D/g, '')
            .replace(/(\d{3})(\d)/, '$1.$2')
            .replace(/(\d{3})(\d)/, '$1.$2')
            .replace(/(\d{3})(\d{1,2})/, '$1-$2')
            .replace(/(-\d{2})\d+?$/, '$1');
    }

    static formatCNPJ(value) {
        return value
            .replace(/\D/g, '')
            .replace(/(\d{2})(\d)/, '$1.$2')
            .replace(/(\d{3})(\d)/, '$1.$2')
            .replace(/(\d{3})(\d)/, '$1/$2')
            .replace(/(\d{4})(\d{1,2})/, '$1-$2')
            .replace(/(-\d{2})\d+?$/, '$1');
    }

    static formatPhone(value) {
        return value
            .replace(/\D/g, '')
            .replace(/(\d{2})(\d)/, '($1) $2')
            .replace(/(\d{4})(\d)/, '$1-$2')
            .replace(/(\d{4})-(\d)(\d{4})/, '$1$2-$3')
            .replace(/(-\d{4})\d+?$/, '$1');
    }
}

// Inicialização da aplicação
document.addEventListener('DOMContentLoaded', function() {
    // Inicializar sistema de navegação
    initNavigation();
    
    // Verifica autenticação
    if (AuthService.checkAuth()) {
        // Navegar para página padrão baseada no perfil
        const defaultRoute = getDefaultRoute();
        navigateTo(defaultRoute, false);
    } else {
        navigateTo('login', false);
    }

    // Auto-formatação de campos
    document.addEventListener('input', function(e) {
        if (e.target.dataset.format) {
            const formatter = FieldFormatter[`format${e.target.dataset.format}`];
            if (formatter) {
                e.target.value = formatter(e.target.value);
            }
        }
    });

    console.log('Maiconsoft Frontend inicializado com sucesso!');
    console.log('Rotas disponíveis:', Object.keys(ROUTES));
});

// Expor funções globais necessárias
window.ApiClient = ApiClient;
window.AuthService = AuthService;
window.navigateTo = navigateTo;
window.goToLogin = goToLogin;
window.goToDashboard = goToDashboard;
window.goToClientes = goToClientes;
window.goToVendas = goToVendas;
window.goToUsuarios = goToUsuarios;
window.goToRelatorios = goToRelatorios;
window.showAlert = showAlert;
window.FormValidator = FormValidator;
window.ROUTES = ROUTES;