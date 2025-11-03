// Verificar se já foi carregado para evitar redeclaração
if (typeof ENV_CONFIG !== 'undefined') {
    console.warn('⚠️ config.js já foi carregado! Evitando redeclaração.');
} else {

// Configuração do ambiente
const ENV_CONFIG = {
    // Configuração para desenvolvimento local
    development: {
        API_BASE_URL: 'http://localhost:8090/api',
        ENABLE_MOCK: false, // Desativado - usar API real
        DEBUG: true
    },
    
    // Configuração para produção
    production: {
        API_BASE_URL: '/api', // URL relativa em produção
        ENABLE_MOCK: false,
        DEBUG: false
    }
};

// FORÇAR DESENVOLVIMENTO - NUNCA USAR PRODUÇÃO
window.FORCE_DEVELOPMENT = true;
const CURRENT_ENV = 'development';
const CONFIG = {
    API_BASE_URL: 'http://localhost:8090/api',
    ENABLE_MOCK: false,
    DEBUG: true
};

// Sistema de Permissões Detalhado
const PERMISSIONS = {
    // Permissões por módulo
    CLIENTES: {
        VIEW: 'clientes:view',
        CREATE: 'clientes:create',
        EDIT: 'clientes:edit',
        DELETE: 'clientes:delete',
        EXPORT: 'clientes:export'
    },
    VENDAS: {
        VIEW: 'vendas:view',
        CREATE: 'vendas:create',
        EDIT: 'vendas:edit',
        DELETE: 'vendas:delete',
        APPROVE: 'vendas:approve',
        EXPORT: 'vendas:export'
    },
    USUARIOS: {
        VIEW: 'usuarios:view',
        CREATE: 'usuarios:create',
        EDIT: 'usuarios:edit',
        DELETE: 'usuarios:delete',
        CHANGE_PERMISSIONS: 'usuarios:permissions'
    },
    RELATORIOS: {
        VIEW: 'relatorios:view',
        EXPORT: 'relatorios:export',
        ADVANCED: 'relatorios:advanced'
    },
    DASHBOARD: {
        VIEW: 'dashboard:view',
        ANALYTICS: 'dashboard:analytics'
    },
    SISTEMA: {
        ADMIN: 'sistema:admin',
        CONFIG: 'sistema:config',
        LOGS: 'sistema:logs'
    }
};

// Mapeamento de Tipos de Usuário para Permissões
const USER_PERMISSIONS = {
    ADMIN: [
        // Acesso ao Dashboard, Clientes, Vendas e Relatórios
        PERMISSIONS.DASHBOARD.VIEW,
        PERMISSIONS.DASHBOARD.ANALYTICS,
        
        PERMISSIONS.CLIENTES.VIEW,
        PERMISSIONS.CLIENTES.CREATE,
        PERMISSIONS.CLIENTES.EDIT,
        PERMISSIONS.CLIENTES.DELETE,
        PERMISSIONS.CLIENTES.EXPORT,
        
        PERMISSIONS.VENDAS.VIEW,
        PERMISSIONS.VENDAS.CREATE,
        PERMISSIONS.VENDAS.EDIT,
        PERMISSIONS.VENDAS.DELETE,
        PERMISSIONS.VENDAS.APPROVE,
        PERMISSIONS.VENDAS.EXPORT,
        
        PERMISSIONS.RELATORIOS.VIEW,
        PERMISSIONS.RELATORIOS.EXPORT,
        PERMISSIONS.RELATORIOS.ADVANCED
        
        // NÃO tem acesso a gestão de usuários ou configurações do sistema
    ],
    
    DIRETOR: [
        // Acesso completo ao sistema, incluindo gestão de usuários e todas as funcionalidades administrativas
        PERMISSIONS.DASHBOARD.VIEW,
        PERMISSIONS.DASHBOARD.ANALYTICS,
        
        PERMISSIONS.CLIENTES.VIEW,
        PERMISSIONS.CLIENTES.CREATE,
        PERMISSIONS.CLIENTES.EDIT,
        PERMISSIONS.CLIENTES.DELETE,
        PERMISSIONS.CLIENTES.EXPORT,
        
        PERMISSIONS.VENDAS.VIEW,
        PERMISSIONS.VENDAS.CREATE,
        PERMISSIONS.VENDAS.EDIT,
        PERMISSIONS.VENDAS.DELETE,
        PERMISSIONS.VENDAS.APPROVE,
        PERMISSIONS.VENDAS.EXPORT,
        
        PERMISSIONS.USUARIOS.VIEW,
        PERMISSIONS.USUARIOS.CREATE,
        PERMISSIONS.USUARIOS.EDIT,
        PERMISSIONS.USUARIOS.DELETE,
        PERMISSIONS.USUARIOS.CHANGE_PERMISSIONS,
        
        PERMISSIONS.RELATORIOS.VIEW,
        PERMISSIONS.RELATORIOS.EXPORT,
        PERMISSIONS.RELATORIOS.ADVANCED,
        
        PERMISSIONS.SISTEMA.ADMIN,
        PERMISSIONS.SISTEMA.CONFIG,
        PERMISSIONS.SISTEMA.LOGS
    ],
    
    FUNCIONARIO: [
        // Acesso restrito apenas ao módulo de Clientes
        PERMISSIONS.CLIENTES.VIEW,
        PERMISSIONS.CLIENTES.CREATE
        // NÃO tem acesso a vendas, relatórios ou outras funcionalidades
    ],
    
    VENDEDOR: [
        // Foco em vendas e clientes
        PERMISSIONS.CLIENTES.VIEW,
        PERMISSIONS.CLIENTES.CREATE,
        PERMISSIONS.CLIENTES.EDIT,
        PERMISSIONS.CLIENTES.EXPORT,
        
        PERMISSIONS.VENDAS.VIEW,
        PERMISSIONS.VENDAS.CREATE,
        PERMISSIONS.VENDAS.EDIT,
        PERMISSIONS.VENDAS.EXPORT,
        
        PERMISSIONS.DASHBOARD.VIEW,
        PERMISSIONS.DASHBOARD.ANALYTICS
    ]
};

// Sistema de verificação de permissões
class PermissionManager {
    static getCurrentUser() {
        try {
            const userData = localStorage.getItem('maiconsoft_user') || localStorage.getItem('usuarioLogado');
            return userData ? JSON.parse(userData) : null;
        } catch (error) {
            console.error('Erro ao obter usuário atual:', error);
            return null;
        }
    }
    
    static getUserPermissions(userType = null) {
        const user = userType ? { roleName: userType } : this.getCurrentUser();
        if (!user) {
            return [];
        }
        
        // Suporte para ambos roleName (novo) e tipoUsuario (compatibilidade)
        const roleType = user.roleName || user.tipoUsuario;
        if (!roleType) {
            return [];
        }
        
        return USER_PERMISSIONS[roleType.toUpperCase()] || [];
    }
    
    static hasPermission(permission, userType = null) {
        const permissions = this.getUserPermissions(userType);
        return permissions.includes(permission);
    }
    
    static canAccessModule(module, userType = null) {
        const modulePermissions = PERMISSIONS[module.toUpperCase()];
        if (!modulePermissions) return false;
        
        const userPermissions = this.getUserPermissions(userType);
        
        // Verificar se tem pelo menos uma permissão do módulo
        return Object.values(modulePermissions).some(permission => 
            userPermissions.includes(permission)
        );
    }
    
    static canPerformAction(module, action, userType = null) {
        const permission = PERMISSIONS[module.toUpperCase()]?.[action.toUpperCase()];
        if (!permission) return false;
        
        return this.hasPermission(permission, userType);
    }
    
    static getAccessiblePages(userType = null) {
        const user = userType ? { roleName: userType } : this.getCurrentUser();
        if (!user) return [];
        
        const accessiblePages = [];
        
        // Dashboard
        if (this.canAccessModule('DASHBOARD', user.roleName)) {
            accessiblePages.push('dashboard');
        }
        
        // Clientes
        if (this.canAccessModule('CLIENTES', user.roleName)) {
            accessiblePages.push('clientes');
        }
        
        // Vendas
        if (this.canAccessModule('VENDAS', user.roleName)) {
            accessiblePages.push('vendas');
        }
        
        // Usuários
        if (this.canAccessModule('USUARIOS', user.tipoUsuario)) {
            accessiblePages.push('usuarios');
        }
        
        // Relatórios
        if (this.canAccessModule('RELATORIOS', user.tipoUsuario)) {
            accessiblePages.push('relatorios');
        }
        
        return accessiblePages;
    }
    
    static showPermissionError(action = 'realizar esta ação') {
        const user = this.getCurrentUser();
        const userName = user?.nome || 'Usuário';
        const userType = user?.tipoUsuario || 'Desconhecido';
        
        alert(`❌ Acesso Negado!\n\n${userName} (${userType}) não tem permissão para ${action}.\n\nContate o administrador do sistema se necessário.`);
    }
    
    static logPermissionAttempt(action, allowed = false) {
        const user = this.getCurrentUser();
        console.log(`🔐 Tentativa de acesso: ${action}`, {
            user: user?.nome,
            type: user?.roleName,
            allowed: allowed,
            timestamp: new Date().toISOString()
        });
    }
}

// Decorador para funções que precisam de permissão
function requirePermission(permission) {
    return function(target, propertyKey, descriptor) {
        const originalMethod = descriptor.value;
        
        descriptor.value = function(...args) {
            if (PermissionManager.hasPermission(permission)) {
                PermissionManager.logPermissionAttempt(permission, true);
                return originalMethod.apply(this, args);
            } else {
                PermissionManager.logPermissionAttempt(permission, false);
                PermissionManager.showPermissionError(`executar: ${permission}`);
                return false;
            }
        };
        
        return descriptor;
    };
}

// Função para integrar verificações de permissão nas páginas
function setupPagePermissions() {
    // Adicionar verificações de permissão em botões e elementos da interface
    document.addEventListener('DOMContentLoaded', function() {
        const user = PermissionManager.getCurrentUser();
        if (!user) return;
        
        // Verificar botões de ação baseados em permissões
        const permissionElements = document.querySelectorAll('[data-permission]');
        permissionElements.forEach(element => {
            const requiredPermission = element.getAttribute('data-permission');
            if (!PermissionManager.hasPermission(requiredPermission)) {
                // Desabilitar ou ocultar elemento
                if (element.getAttribute('data-hide-if-no-permission') === 'true') {
                    element.style.display = 'none';
                } else {
                    element.disabled = true;
                    element.style.opacity = '0.5';
                    element.title = 'Você não tem permissão para esta ação';
                }
            }
        });
        
        // Verificar seções baseadas em módulos
        const moduleElements = document.querySelectorAll('[data-module]');
        moduleElements.forEach(element => {
            const requiredModule = element.getAttribute('data-module');
            if (!PermissionManager.canAccessModule(requiredModule)) {
                element.style.display = 'none';
            }
        });
    });
}

// Função para verificar permissão antes de executar ações
function checkPermissionAndExecute(permission, callback, errorMessage = null) {
    if (PermissionManager.hasPermission(permission)) {
        PermissionManager.logPermissionAttempt(permission, true);
        return callback();
    } else {
        PermissionManager.logPermissionAttempt(permission, false);
        PermissionManager.showPermissionError(errorMessage || `executar esta ação (${permission})`);
        return false;
    }
}

// URLs das páginas do sistema
const PAGE_ROUTES = {
    login: '#login',
    dashboard: '#dashboard', 
    cliente: '#cliente',
    vendas: '#vendas',
    usuarios: '#usuarios',
    relatorios: '#relatorios'
};

// Dados de teste para desenvolvimento
const MOCK_USERS = {
    'admin': {
        codigoAcesso: 'admin',
        senha: '123',
        perfil: 'admin',
        nome: 'Administrador Sistema',
        email: 'admin@maiconsoft.com'
    },
    'diretor': {
        codigoAcesso: 'diretor', 
        senha: '123',
        perfil: 'diretor',
        nome: 'Diretor Geral',
        email: 'diretor@maiconsoft.com'
    },
    'funcionario': {
        codigoAcesso: 'funcionario',
        senha: '123', 
        perfil: 'funcionario',
        nome: 'Funcionário Cadastro',
        email: 'funcionario@maiconsoft.com'
    }
};

// Status de conectividade
let BACKEND_AVAILABLE = false;

// Função para testar conectividade com backend
async function checkBackendConnection() {
    if (!CONFIG.ENABLE_MOCK) return true;
    
    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/health`, {
            method: 'GET',
            timeout: 5000
        });
        BACKEND_AVAILABLE = response.ok;
        
        if (CONFIG.DEBUG) {
            console.log(`Backend ${BACKEND_AVAILABLE ? 'disponível' : 'indisponível'}`);
        }
    } catch (error) {
        BACKEND_AVAILABLE = false;
        if (CONFIG.DEBUG) {
            console.warn('Backend não disponível, usando modo simulação:', error.message);
        }
    }
    
    return BACKEND_AVAILABLE;
}

// API Service para comunicação com backend
const APIService = {
    // Base URL da API
    get baseURL() {
        return CONFIG.API_BASE_URL;
    },

    // Headers padrão
    getHeaders() {
        return {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        };
    },

    // Método para fazer requisições HTTP
    async request(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        const config = {
            headers: this.getHeaders(),
            ...options
        };

        if (CONFIG.DEBUG) {
            console.log('API Request:', { url, config });
        }

        try {
            const response = await fetch(url, config);
            const data = await response.json();

            if (CONFIG.DEBUG) {
                console.log('API Response:', { status: response.status, data });
            }

            if (!response.ok) {
                throw new Error(data.message || `HTTP ${response.status}: ${response.statusText}`);
            }

            return data;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    },

    // Métodos específicos para usuários
    users: {
        async login(codigoAcesso, senha) {
            return APIService.request('/users/login', {
                method: 'POST',
                body: JSON.stringify({ codigoAcesso, senha })
            });
        },

        async create(userData) {
            return APIService.request('/users', {
                method: 'POST',
                body: JSON.stringify(userData)
            });
        },

        async getAll(page = 0, size = 20) {
            return APIService.request(`/users?page=${page}&size=${size}`);
        },

        async getById(id) {
            return APIService.request(`/users/${id}`);
        },

        async update(id, userData) {
            return APIService.request(`/users/${id}`, {
                method: 'PATCH',
                body: JSON.stringify(userData)
            });
        },

        async delete(id) {
            return APIService.request(`/users/${id}`, {
                method: 'DELETE'
            });
        },

        async uploadProfilePhoto(userId, file) {
            const formData = new FormData();
            formData.append('file', file);
            formData.append('userId', userId);

            return fetch(`${APIService.baseURL}/uploads/profile-photo`, {
                method: 'POST',
                body: formData
            }).then(response => response.json());
        },

        async deleteProfilePhoto(filePath) {
            return APIService.request(`/uploads/profile-photo?filePath=${encodeURIComponent(filePath)}`, {
                method: 'DELETE'
            });
        }
    },

    // Métodos específicos para user roles
    userRoles: {
        async getAll() {
            return APIService.request('/users/roles');
        },

        async getById(id) {
            return APIService.request(`/users/roles/${id}`);
        },

        async create(roleData) {
            return APIService.request('/users/roles', {
                method: 'POST',
                body: JSON.stringify(roleData)
            });
        },

        async update(id, roleData) {
            return APIService.request(`/users/roles/${id}`, {
                method: 'PUT',
                body: JSON.stringify(roleData)
            });
        },

        async delete(id) {
            return APIService.request(`/users/roles/${id}`, {
                method: 'DELETE'
            });
        }
    },

    // Métodos específicos para clientes
    clientes: {
        async create(clienteData) {
            return APIService.request('/clientes', {
                method: 'POST',
                body: JSON.stringify(clienteData)
            });
        },

        async getAll(page = 0, size = 20, search = '') {
            let url = `/clientes?page=${page}&size=${size}`;
            if (search) {
                url += `&search=${encodeURIComponent(search)}`;
            }
            return APIService.request(url);
        },

        async getById(id) {
            return APIService.request(`/clientes/${id}`);
        },

        async update(id, clienteData) {
            return APIService.request(`/clientes/${id}`, {
                method: 'PUT',
                body: JSON.stringify(clienteData)
            });
        },

        async delete(id) {
            return APIService.request(`/clientes/${id}`, {
                method: 'DELETE'
            });
        },

        async search(searchTerm, page = 0, size = 10) {
            const url = `/clientes/search?searchText=${encodeURIComponent(searchTerm)}&page=${page}&size=${size}`;
            return APIService.request(url);
        }
    },

    // Métodos específicos para vendas
    vendas: {
        async create(vendaData) {
            return APIService.request('/vendas', {
                method: 'POST',
                body: JSON.stringify(vendaData)
            });
        },

        async getAll(page = 0, size = 20, clienteNome = '', status = '', dataInicio = '', dataFim = '') {
            let url = `/vendas?page=${page}&size=${size}`;
            if (clienteNome) {
                url += `&clienteNome=${encodeURIComponent(clienteNome)}`;
            }
            if (status) {
                url += `&status=${encodeURIComponent(status)}`;
            }
            if (dataInicio) {
                url += `&dataInicio=${dataInicio}`;
            }
            if (dataFim) {
                url += `&dataFim=${dataFim}`;
            }
            return APIService.request(url);
        },

        async getById(id) {
            return APIService.request(`/vendas/${id}`);
        },

        async getByOrcamento(numeroOrcamento) {
            return APIService.request(`/vendas/orcamento/${numeroOrcamento}`);
        },

        async getByStatus(status, page = 0, size = 20) {
            return APIService.request(`/vendas/status/${status}?page=${page}&size=${size}`);
        },

        async getByPeriodo(dataInicio, dataFim, page = 0, size = 20) {
            return APIService.request(`/vendas/periodo?dataInicio=${dataInicio}&dataFim=${dataFim}&page=${page}&size=${size}`);
        },

        async getByUsuario(usuarioId, dataInicio = '', dataFim = '', page = 0, size = 20) {
            let url = `/vendas/relatorio/usuario/${usuarioId}?page=${page}&size=${size}`;
            if (dataInicio) {
                url += `&dataInicio=${dataInicio}`;
            }
            if (dataFim) {
                url += `&dataFim=${dataFim}`;
            }
            return APIService.request(url);
        },

        async filterAdvanced(filterData) {
            return APIService.request('/vendas/filter', {
                method: 'POST',
                body: JSON.stringify(filterData)
            });
        },

        async update(id, vendaData) {
            return APIService.request(`/vendas/${id}`, {
                method: 'PUT',
                body: JSON.stringify(vendaData)
            });
        },

        async delete(id) {
            return APIService.request(`/vendas/${id}`, {
                method: 'DELETE'
            });
        },

        // Métodos auxiliares para métricas
        async getMetricas(periodo = 'mes') {
            return APIService.request(`/vendas/metricas?periodo=${periodo}`);
        },

        async getResumo() {
            return APIService.request('/vendas/resumo');
        }
    }
};

// Expor configurações globalmente
window.CONFIG = CONFIG;
window.PAGE_ROUTES = PAGE_ROUTES;
window.MOCK_USERS = MOCK_USERS;
window.checkBackendConnection = checkBackendConnection;
window.APIService = APIService;

// Service para gerenciar roles do usuário
class UserRoleService {
    static async getAllRoles() {
        try {
            console.log('🔄 UserRoleService: Buscando roles do backend...');
            const response = await APIService.userRoles.getAll();
            console.log('✅ UserRoleService: Roles recebidas do backend:', response);
            return response.data || response;
        } catch (error) {
            console.error('❌ UserRoleService: Erro ao buscar roles:', error);
            console.log('🔄 UserRoleService: Aplicando fallback para roles padrão...');
            // Fallback para roles padrão
            const fallbackRoles = [
                { idRole: 1, roleName: 'ADMIN' },
                { idRole: 2, roleName: 'DIRETOR' },
                { idRole: 3, roleName: 'FUNCIONARIO' },
                { idRole: 4, roleName: 'VENDEDOR' }
            ];
            console.log('✅ UserRoleService: Usando fallback:', fallbackRoles);
            return fallbackRoles;
        }
    }
    
    static async getUserWithRole(userId) {
        try {
            const response = await APIService.users.getById(userId);
            return response.data || response;
        } catch (error) {
            console.error('Erro ao buscar usuário com role:', error);
            return null;
        }
    }
    
    static getRoleDisplayName(roleName) {
        const roleNames = {
            'ADMIN': 'Administrador',
            'DIRETOR': 'Diretor',
            'FUNCIONARIO': 'Funcionário',
            'VENDEDOR': 'Vendedor',
            'SUPERVISOR': 'Supervisor',
            'GERENTE': 'Gerente'
        };
        return roleNames[roleName?.toUpperCase()] || roleName || 'Funcionário';
    }
    
    static getRolePermissions(roleName) {
        return USER_PERMISSIONS[roleName?.toUpperCase()] || [];
    }
    
    static canRolePerformAction(roleName, permission) {
        const permissions = this.getRolePermissions(roleName);
        return permissions.includes(permission);
    }
}

// Expor UserRoleService globalmente
window.UserRoleService = UserRoleService;

// Função utilitária para atualizar dados do usuário
async function updateUserDataFromBackend() {
    const userData = localStorage.getItem('maiconsoft_user') || localStorage.getItem('userData');
    if (!userData) return null;
    
    try {
        const user = JSON.parse(userData);
        
        // Tentar buscar dados atualizados do backend
        if (user.id || user.idUser) {
            try {
                console.log('🔄 Buscando dados atualizados do usuário...');
                const response = await APIService.users.getById(user.id || user.idUser);
                const updatedUser = response.data || response;
                
                // Atualizar localStorage com dados atualizados
                const mergedUser = {
                    ...user,
                    tipoUsuario: updatedUser.roleName || user.tipoUsuario,
                    roleName: updatedUser.roleName || user.roleName
                };
                
                localStorage.setItem('maiconsoft_user', JSON.stringify(mergedUser));
                console.log('✅ Dados do usuário atualizados:', mergedUser);
                
                return mergedUser;
            } catch (apiError) {
                console.warn('⚠️ Erro ao buscar dados atualizados, usando dados locais:', apiError);
                return user;
            }
        }
        
        return user;
    } catch (error) {
        console.error('Erro ao processar dados do usuário:', error);
        return null;
    }
}

// Expor função globalmente
window.updateUserDataFromBackend = updateUserDataFromBackend;

// Definir API_BASE_URL como variável global para compatibilidade
window.API_BASE_URL = CONFIG.API_BASE_URL;

console.log(`🔧 Maiconsoft Config carregado - Ambiente: ${CURRENT_ENV}`);
console.log(`🌐 API Base URL: ${CONFIG.API_BASE_URL}`);
console.log(`🔍 Debug Mode: ${CONFIG.DEBUG}`);

} // Fechar bloco if