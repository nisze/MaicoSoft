/**
 * Maiconsoft - Módulo de Login
 * Gerencia autenticação             senhaInput.addEventListener('input', () => this.clearFieldError('senha'));
        }redirecionamento baseado em perfil
 */

// Função para login rápido de demonstração
function demoLogin(usuario, senha) {
    const codigoInput = document.getElementById('codigoAcesso');
    const senhaInput = document.getElementById('senha');
    
    if (codigoInput && senhaInput) {
        codigoInput.value = usuario;
        senhaInput.value = senha;
        
        // Destacar campos preenchidos
        codigoInput.style.borderColor = '#f59e0b';
        senhaInput.style.borderColor = '#f59e0b';
        
        // Simula o envio do formulário após delay
        setTimeout(() => {
            const form = document.getElementById('loginForm');
            if (form) {
                form.dispatchEvent(new Event('submit', { cancelable: true }));
            }
        }, 500);
    }
}

class LoginManager {
    constructor() {
        this.isLoggingIn = false; // Flag para prevenir múltiplos submits
        this.init();
    }

    init() {
        // Limpar dados antigos que podem causar loop
        localStorage.removeItem('maiconsoft_user');
        localStorage.removeItem('maiconsoft_logged_in');
        localStorage.removeItem('usuarioLogado'); // Formato antigo
        console.log('🧹 localStorage limpo');
        
        this.setupEventListeners();
        this.setupPasswordToggle();
        // DESABILITADO: this.checkExistingAuth(); - Causava loop infinito
        console.log('🔧 Login Manager inicializado SEM verificação automática');
    }

    setupEventListeners() {
        const loginForm = document.getElementById('loginForm');
        const loginBtn = document.getElementById('loginBtn');
        
        if (loginForm) {
            loginForm.addEventListener('submit', (e) => this.handleLogin(e));
            console.log('✅ Event listener de submit adicionado ao formulário');
        } else {
            console.error('❌ Formulário loginForm não encontrado!');
        }

        // Enter key handling - só se o formulário existir
        if (loginForm) {
            const inputs = loginForm.querySelectorAll('input');
            inputs.forEach(input => {
                input.addEventListener('keypress', (e) => {
                    if (e.key === 'Enter' && !this.isLoggingIn) {
                        e.preventDefault();
                        this.handleLogin(e);
                    }
                });
            });
        }

        // Real-time validation
        const codigoInput = document.getElementById('codigoAcesso');
        const senhaInput = document.getElementById('senha');
        
        if (codigoInput) {
            codigoInput.addEventListener('blur', () => this.validateField('codigoAcesso'));
            codigoInput.addEventListener('input', () => this.clearFieldError('codigoAcesso'));
            console.log('✅ Event listeners adicionados ao campo codigoAcesso');
        } else {
            console.error('❌ Campo codigoAcesso não encontrado!');
        }
        
        if (senhaInput) {
            senhaInput.addEventListener('blur', () => this.validateField('senha'));
            senhaInput.addEventListener('input', () => this.clearFieldError('senha'));
            console.log('✅ Event listeners adicionados ao campo senha');
        } else {
            console.error('❌ Campo senha não encontrado!');
        }
    }

    setupPasswordToggle() {
        const toggleBtn = document.querySelector('.password-toggle');
        const passwordInput = document.getElementById('password');
        
        if (toggleBtn && passwordInput) {
            toggleBtn.addEventListener('click', () => {
                const isPassword = passwordInput.type === 'password';
                passwordInput.type = isPassword ? 'text' : 'password';
                
                const icon = toggleBtn.querySelector('.material-icons');
                if (icon) {
                    icon.textContent = isPassword ? 'visibility_off' : 'visibility';
                }
            });
        }
    }

    async handleLogin(event) {
        console.log('🚀 handleLogin chamado!', event);
        event.preventDefault();
        
        // Prevenir múltiplos submits
        if (this.isLoggingIn) {
            console.log('⚠️ Login já em andamento, ignorando...');
            return;
        }
        
        this.isLoggingIn = true;
        
        const codigoAcesso = document.getElementById('codigoAcesso')?.value?.trim();
        const senha = document.getElementById('senha')?.value;
        
        console.log('📋 Dados do formulário:', { codigoAcesso, senha: senha ? '***' : 'vazio' });
        
        // Validate form
        if (!this.validateForm(codigoAcesso, senha)) {
            console.log('❌ Formulário inválido');
            this.isLoggingIn = false;
            return;
        }

        console.log('✅ Formulário válido, iniciando login...');
        const loginBtn = document.getElementById('loginBtn');
        this.setLoadingState(loginBtn, true);

        try {
            console.log('🔌 Fazendo requisição para API...');
            // Usar APIService para login
            const response = await APIService.users.login(codigoAcesso, senha);
            console.log('✅ Resposta da API:', response);
            
            if (response.ativo) {
                // Salvar dados do usuário no localStorage no formato correto
                const userData = {
                    id: response.id,
                    nome: response.nome,
                    email: response.email,
                    codigoAcesso: response.codigoAcesso,
                    tipoUsuario: response.tipoUsuario, // Adicionar tipo de usuário
                    ativo: response.ativo,
                    loginTime: new Date().toISOString()
                };
                
                // Salvar nos dois formatos para compatibilidade
                localStorage.setItem('maiconsoft_user', JSON.stringify(userData));
                localStorage.setItem('maiconsoft_logged_in', 'true');
                localStorage.setItem('usuarioLogado', JSON.stringify(userData)); // Formato que o dashboard espera
                
                console.log('💾 Dados salvos no localStorage:', userData);
                
                this.showMessage('Login realizado com sucesso!', 'success');
                
                // Desabilitar formulário para prevenir resubmissão
                const form = document.getElementById('loginForm');
                if (form) {
                    const inputs = form.querySelectorAll('input, button');
                    inputs.forEach(input => input.disabled = true);
                }
                
                // Redirecionar para dashboard suavemente
                console.log('🔄 Redirecionando para dashboard...');
                
                // URL do dashboard
                const dashboardUrl = 'http://127.0.0.1:3001/pages/dashboard.html';
                console.log('🌐 URL destino:', dashboardUrl);
                
                // Redirecionamento direto e suave
                console.log('🚀 Executando redirecionamento...');
                setTimeout(() => {
                    window.location.href = dashboardUrl;
                }, 800); // Pequeno delay para mostrar mensagem de sucesso
                
            } else {
                this.showMessage('Usuário inativo. Contate o administrador.', 'error');
            }
            
        } catch (error) {
            console.error('Login error:', error);
            
            // Mostrar mensagem de erro específica
            let errorMessage = 'Erro ao fazer login. Verifique suas credenciais.';
            
            if (error.message.includes('Código de acesso não encontrado')) {
                errorMessage = 'Código de acesso não encontrado.';
            } else if (error.message.includes('Senha incorreta')) {
                errorMessage = 'Senha incorreta.';
            } else if (error.message.includes('usuário inativo')) {
                errorMessage = 'Usuário inativo. Contate o administrador.';
            }
            
            this.showMessage(errorMessage, 'error');
        } finally {
            this.setLoadingState(loginBtn, false);
            this.isLoggingIn = false; // Resetar flag
        }
    }

    validateForm(codigoAcesso, senha) {
        let isValid = true;

        // Validate codigo de acesso
        if (!codigoAcesso) {
            this.showFieldError('codigoAcesso', 'Código de acesso é obrigatório');
            isValid = false;
        }

        // Validate senha
        if (!senha) {
            this.showFieldError('senha', 'Senha é obrigatória');
            isValid = false;
        }

        return isValid;
    }

    validateField(fieldName) {
        const input = document.getElementById(fieldName);
        const value = input?.value?.trim();

        this.clearFieldError(fieldName);

        switch (fieldName) {
            case 'codigoAcesso':
                if (!value) {
                    this.showFieldError(fieldName, 'Código de acesso é obrigatório');
                    return false;
                }
                break;
                
            case 'senha':
                if (!value) {
                    this.showFieldError(fieldName, 'Senha é obrigatória');
                    return false;
                }
                break;
        }

        this.showFieldSuccess(fieldName);
        return true;
    }

    showFieldError(fieldName, message) {
        const field = document.getElementById(fieldName);
        const fieldContainer = field?.closest('.form-field');
        
        if (fieldContainer) {
            fieldContainer.classList.add('invalid');
            fieldContainer.classList.remove('valid');
            
            // Remove existing error message
            const existingError = fieldContainer.querySelector('.field-error');
            if (existingError) {
                existingError.remove();
            }
            
            // Add error message
            const errorDiv = document.createElement('div');
            errorDiv.className = 'field-error';
            errorDiv.innerHTML = `<i class="material-icons">error</i> ${message}`;
            fieldContainer.appendChild(errorDiv);
        }
    }

    showFieldSuccess(fieldName) {
        const field = document.getElementById(fieldName);
        const fieldContainer = field?.closest('.form-field');
        
        if (fieldContainer) {
            fieldContainer.classList.add('valid');
            fieldContainer.classList.remove('invalid');
            
            // Remove error message
            const existingError = fieldContainer.querySelector('.field-error');
            if (existingError) {
                existingError.remove();
            }
        }
    }

    clearFieldError(fieldName) {
        const field = document.getElementById(fieldName);
        const fieldContainer = field?.closest('.form-field');
        
        if (fieldContainer) {
            fieldContainer.classList.remove('invalid', 'valid');
            
            const existingError = fieldContainer.querySelector('.field-error');
            if (existingError) {
                existingError.remove();
            }
        }
    }

    setLoadingState(button, loading) {
        if (!button) return;
        
        if (loading) {
            button.classList.add('loading');
            button.disabled = true;
        } else {
            button.classList.remove('loading');
            button.disabled = false;
        }
    }

    showMessage(message, type = 'info') {
        // Criar notificação simples
        console.log(`${type.toUpperCase()}: ${message}`);
        
        // Criar elemento de notificação
        const notification = document.createElement('div');
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            border-radius: 8px;
            color: white;
            font-weight: 500;
            z-index: 10000;
            max-width: 300px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.3);
            background: ${type === 'success' ? '#10b981' : type === 'error' ? '#ef4444' : '#3b82f6'};
        `;
        notification.textContent = message;
        
        document.body.appendChild(notification);
        
        // Remover após 3 segundos
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 3000);
    }

    getMessageIcon(type) {
        const icons = {
            success: 'check_circle',
            error: 'error',
            warning: 'warning',
            info: 'info'
        };
        return icons[type] || 'info';
    }

    redirectBasedOnRole(perfil) {
        switch (perfil?.toLowerCase()) {
            case 'diretor':
                // Diretor tem acesso completo - vai para dashboard
                window.location.href = '#dashboard';
                break;
            case 'admin':
                // Admin tem acesso a dashboard, clientes, vendas e relatórios
                window.location.href = '#dashboard';
                break;
            case 'funcionario':
                // Funcionário tem acesso restrito apenas ao módulo de clientes
                window.location.href = '#cliente';
                break;
            case 'vendedor':
                // Vendedor tem acesso a clientes e vendas
                window.location.href = '#cliente';
                break;
            default:
                // Default para clientes para perfis desconhecidos
                window.location.href = '#cliente';
        }
    }

    checkExistingAuth() {
        // Verificar se já existe sessão ativa
        const userData = localStorage.getItem('maiconsoft_user');
        const isLoggedIn = localStorage.getItem('maiconsoft_logged_in');
        
        if (isLoggedIn === 'true' && userData) {
            try {
                const user = JSON.parse(userData);
                if (user && user.nome) {
                    console.log('✅ Usuário já autenticado:', user.nome);
                    // Redirecionar para dashboard
                    setTimeout(() => {
                        window.location.href = '../pages/dashboard.html';
                    }, 1000);
                }
            } catch (error) {
                console.error('❌ Erro ao verificar autenticação:', error);
                // Limpar dados corrompidos
                localStorage.removeItem('maiconsoft_user');
                localStorage.removeItem('maiconsoft_logged_in');
            }
        }
    }

    // Forgot password functionality
    handleForgotPassword() {
        const email = document.getElementById('email')?.value?.trim();
        
        if (!email) {
            this.showMessage('Digite seu email para recuperar a senha', 'warning');
            document.getElementById('email')?.focus();
            return;
        }

        if (!this.formValidator.isValidEmail(email)) {
            this.showMessage('Digite um email válido', 'error');
            return;
        }

        // Simulate forgot password request
        this.showMessage('Se este email estiver cadastrado, você receberá instruções para redefinir sua senha.', 'info');
    }
}

// Initialize login manager when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    // Only initialize if we're on the login page
    if (document.getElementById('loginForm')) {
        new LoginManager();
    }
});

// Handle forgot password link
document.addEventListener('click', function(e) {
    if (e.target.classList.contains('forgot-password')) {
        e.preventDefault();
        const loginManager = new LoginManager();
        loginManager.handleForgotPassword();
    }
});

// Additional CSS for login messages
const loginStyles = `
    .login-message {
        margin-bottom: var(--spacing-md);
        padding: var(--spacing-md);
        border-radius: var(--radius-sm);
        display: flex;
        align-items: center;
        gap: var(--spacing-sm);
        font-weight: 500;
        animation: slideDown 0.3s ease-out;
    }

    .login-message.alert-success {
        background: rgba(34, 197, 94, 0.1);
        color: var(--success);
        border: 1px solid rgba(34, 197, 94, 0.3);
    }

    .login-message.alert-error {
        background: rgba(239, 68, 68, 0.1);
        color: var(--error);
        border: 1px solid rgba(239, 68, 68, 0.3);
    }

    .login-message.alert-warning {
        background: rgba(251, 191, 36, 0.1);
        color: var(--warning);
        border: 1px solid rgba(251, 191, 36, 0.3);
    }

    .login-message.alert-info {
        background: rgba(59, 130, 246, 0.1);
        color: #3b82f6;
        border: 1px solid rgba(59, 130, 246, 0.3);
    }

    .field-error {
        display: flex;
        align-items: center;
        gap: var(--spacing-xs);
        margin-top: var(--spacing-xs);
        color: var(--error);
        font-size: 12px;
        font-weight: 500;
    }

    .field-error .material-icons {
        font-size: 16px;
    }

    @keyframes slideDown {
        from { opacity: 0; transform: translateY(-10px); }
        to { opacity: 1; transform: translateY(0); }
    }
`;

// Inject login styles
const styleSheet = document.createElement('style');
styleSheet.textContent = loginStyles;
document.head.appendChild(styleSheet);

// Inicializar LoginManager quando o documento estiver carregado
document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('loginForm')) {
        new LoginManager();
    }
});