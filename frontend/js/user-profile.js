/**
 * Maiconsoft - Sistema de Perfil de Usuário
 * Gerencia configurações, dados pessoais e preferências do usuário
 * @fileOverview User profile management for Maiconsoft
 * @author Maiconsoft
 * @version 1.0.0
 */

class UserProfileManager {
    constructor() {
        this.currentUser = null;
        this.profileData = {};
        this.init();
    }

    init() {
        console.log('🔧 UserProfileManager inicializando...');
        this.loadCurrentUser();
        this.setupEventListeners();
        this.setupModalEvents();
        console.log('✅ UserProfileManager inicializado');
    }

    loadCurrentUser() {
        try {
            const userData = localStorage.getItem('usuarioLogado') || localStorage.getItem('authData');
            if (userData) {
                this.currentUser = JSON.parse(userData);
                console.log('👤 Usuário carregado:', this.currentUser);
            }
        } catch (error) {
            console.error('❌ Erro ao carregar dados do usuário:', error);
        }
    }

    setupEventListeners() {
        // Modal de perfil
        const profileSetupBtn = document.getElementById('profileSetupBtn');
        if (profileSetupBtn) {
            profileSetupBtn.addEventListener('click', () => this.openProfileModal());
        }

        // Modal de esqueceu senha
        const forgotPasswordBtn = document.getElementById('forgotPasswordBtn');
        if (forgotPasswordBtn) {
            forgotPasswordBtn.addEventListener('click', () => this.openForgotPasswordModal());
        }

        // Upload de imagem
        const profileImageInput = document.getElementById('profileImage');
        if (profileImageInput) {
            profileImageInput.addEventListener('change', (e) => this.handleImageUpload(e));
        }

        // Validação de senhas
        const newPasswordInput = document.getElementById('profileNewPassword');
        const confirmPasswordInput = document.getElementById('profileConfirmPassword');
        
        if (newPasswordInput && confirmPasswordInput) {
            newPasswordInput.addEventListener('input', () => this.validatePasswords());
            confirmPasswordInput.addEventListener('input', () => this.validatePasswords());
        }
    }

    setupModalEvents() {
        // Eventos de fechar modais
        const closeButtons = [
            { id: 'closeProfileModal', modal: 'profileModal' },
            { id: 'closeForgotModal', modal: 'forgotPasswordModal' },
            { id: 'cancelProfileBtn', modal: 'profileModal' },
            { id: 'cancelForgotBtn', modal: 'forgotPasswordModal' }
        ];

        closeButtons.forEach(({ id, modal }) => {
            const button = document.getElementById(id);
            if (button) {
                button.addEventListener('click', () => this.closeModal(modal));
            }
        });

        // Formulários
        const profileForm = document.getElementById('profileForm');
        if (profileForm) {
            profileForm.addEventListener('submit', (e) => this.handleProfileSubmit(e));
        }

        const forgotPasswordForm = document.getElementById('forgotPasswordForm');
        if (forgotPasswordForm) {
            forgotPasswordForm.addEventListener('submit', (e) => this.handleForgotPasswordSubmit(e));
        }

        // Fechar modal clicando no fundo
        const modals = ['profileModal', 'forgotPasswordModal'];
        modals.forEach(modalId => {
            const modal = document.getElementById(modalId);
            if (modal) {
                modal.addEventListener('click', (e) => {
                    if (e.target === modal) {
                        this.closeModal(modalId);
                    }
                });
            }
        });
    }

    openProfileModal() {
        const modal = document.getElementById('profileModal');
        if (modal) {
            this.loadProfileData();
            modal.style.display = 'flex';
            
            // Focar no primeiro campo
            setTimeout(() => {
                const firstInput = modal.querySelector('input[type="text"]');
                if (firstInput) {
                    firstInput.focus();
                }
            }, 100);
        }
    }

    openForgotPasswordModal() {
        const modal = document.getElementById('forgotPasswordModal');
        if (modal) {
            modal.style.display = 'flex';
            
            // Focar no campo de entrada
            setTimeout(() => {
                const input = document.getElementById('forgotIdentifier');
                if (input) {
                    input.focus();
                }
            }, 100);
        }
    }

    closeModal(modalId) {
        const modal = document.getElementById(modalId);
        if (modal) {
            modal.style.display = 'none';
        }
    }

    loadProfileData() {
        if (!this.currentUser) return;

        // Preencher campos com dados existentes
        const fields = {
            'profileName': this.currentUser.nome || '',
            'profileEmail': this.currentUser.email || '',
            'profilePhone': this.currentUser.telefone || '',
            'profileRole': this.currentUser.tipoUsuario || this.currentUser.role || 'FUNCIONARIO'
        };

        Object.entries(fields).forEach(([fieldId, value]) => {
            const field = document.getElementById(fieldId);
            if (field) {
                field.value = value;
            }
        });

        // Carregar foto de perfil se existir
        this.loadProfileImage();
    }

    loadProfileImage() {
        const savedImage = localStorage.getItem(`profile_image_${this.currentUser.codigo || this.currentUser.id}`);
        const preview = document.getElementById('profileImagePreview');
        
        if (savedImage && preview) {
            preview.innerHTML = `<img src="${savedImage}" style="width: 100%; height: 100%; object-fit: cover; border-radius: 50%;">`;
        }
    }

    handleImageUpload(event) {
        const file = event.target.files[0];
        if (!file) return;

        // Validar tipo de arquivo
        if (!file.type.startsWith('image/')) {
            this.showNotification('Por favor, selecione apenas arquivos de imagem.', 'error');
            return;
        }

        // Validar tamanho (máximo 5MB)
        if (file.size > 5 * 1024 * 1024) {
            this.showNotification('A imagem deve ter no máximo 5MB.', 'error');
            return;
        }

        const reader = new FileReader();
        reader.onload = (e) => {
            const preview = document.getElementById('profileImagePreview');
            if (preview) {
                preview.innerHTML = `<img src="${e.target.result}" style="width: 100%; height: 100%; object-fit: cover; border-radius: 50%;">`;
            }
            
            // Salvar imagem no localStorage (temporariamente)
            this.profileData.profileImage = e.target.result;
        };
        reader.readAsDataURL(file);
    }

    validatePasswords() {
        const newPassword = document.getElementById('profileNewPassword').value;
        const confirmPassword = document.getElementById('profileConfirmPassword').value;
        const confirmField = document.getElementById('profileConfirmPassword');

        // Limpar erros anteriores
        confirmField.style.borderColor = '#444444';
        const existingError = confirmField.parentElement.querySelector('.password-error');
        if (existingError) {
            existingError.remove();
        }

        // Validar apenas se ambos os campos têm valor
        if (newPassword && confirmPassword) {
            if (newPassword !== confirmPassword) {
                confirmField.style.borderColor = '#ef4444';
                
                const errorDiv = document.createElement('div');
                errorDiv.className = 'password-error';
                errorDiv.style.cssText = 'color: #ef4444; font-size: 0.8rem; margin-top: 0.25rem;';
                errorDiv.textContent = 'As senhas não coincidem';
                confirmField.parentElement.appendChild(errorDiv);
                
                return false;
            } else {
                confirmField.style.borderColor = '#22c55e';
                return true;
            }
        }

        return true;
    }

    async handleProfileSubmit(event) {
        event.preventDefault();

        // Validar senhas se foram preenchidas
        if (!this.validatePasswords()) {
            return;
        }

        const formData = this.getProfileFormData();
        const submitButton = event.target.querySelector('button[type="submit"]');
        
        this.setLoadingState(submitButton, true);

        try {
            await this.saveProfile(formData);
            this.showNotification('Perfil atualizado com sucesso!', 'success');
            this.closeModal('profileModal');
        } catch (error) {
            console.error('Erro ao salvar perfil:', error);
            this.showNotification('Erro ao salvar perfil. Tente novamente.', 'error');
        } finally {
            this.setLoadingState(submitButton, false);
        }
    }

    getProfileFormData() {
        return {
            nome: document.getElementById('profileName')?.value?.trim(),
            email: document.getElementById('profileEmail')?.value?.trim(),
            telefone: document.getElementById('profilePhone')?.value?.trim(),
            tipoUsuario: document.getElementById('profileRole')?.value,
            senha: document.getElementById('profileNewPassword')?.value?.trim() || null,
            profileImage: this.profileData.profileImage || null
        };
    }

    async saveProfile(profileData) {
        // Simular chamada para API
        await new Promise(resolve => setTimeout(resolve, 1000));

        // Atualizar dados locais
        if (this.currentUser) {
            Object.assign(this.currentUser, profileData);
            
            // Remover senha dos dados salvos
            const { senha, ...userDataToSave } = this.currentUser;
            localStorage.setItem('usuarioLogado', JSON.stringify(userDataToSave));

            // Salvar imagem separadamente se existir
            if (profileData.profileImage) {
                localStorage.setItem(`profile_image_${this.currentUser.codigo || this.currentUser.id}`, profileData.profileImage);
            }
        }

        console.log('✅ Perfil salvo:', profileData);
    }

    async handleForgotPasswordSubmit(event) {
        event.preventDefault();

        const identifier = document.getElementById('forgotIdentifier')?.value?.trim();
        if (!identifier) {
            this.showNotification('Por favor, digite seu código de acesso ou email.', 'error');
            return;
        }

        const submitButton = event.target.querySelector('button[type="submit"]');
        this.setLoadingState(submitButton, true);

        try {
            await this.requestPasswordReset(identifier);
            this.showNotification('Instruções de recuperação enviadas!', 'success');
            this.closeModal('forgotPasswordModal');
        } catch (error) {
            console.error('Erro ao solicitar recuperação:', error);
            this.showNotification('Erro ao enviar solicitação. Tente novamente.', 'error');
        } finally {
            this.setLoadingState(submitButton, false);
        }
    }

    async requestPasswordReset(identifier) {
        // Simular chamada para API
        await new Promise(resolve => setTimeout(resolve, 1500));
        
        console.log('📧 Solicitação de recuperação enviada para:', identifier);
        
        // Em um sistema real, aqui seria feita a chamada para o backend
        // return await fetch('/api/auth/forgot-password', {
        //     method: 'POST',
        //     headers: { 'Content-Type': 'application/json' },
        //     body: JSON.stringify({ identifier })
        // });
    }

    setLoadingState(button, loading) {
        if (!button) return;

        if (loading) {
            button.disabled = true;
            button.dataset.originalText = button.textContent;
            button.innerHTML = '<span style="display: inline-block; width: 16px; height: 16px; border: 2px solid #fff; border-top: 2px solid transparent; border-radius: 50%; animation: spin 1s linear infinite; margin-right: 0.5rem;"></span>Salvando...';
        } else {
            button.disabled = false;
            button.textContent = button.dataset.originalText || 'Salvar';
        }
    }

    showNotification(message, type = 'info') {
        // Remover notificações existentes
        document.querySelectorAll('.profile-notification').forEach(n => n.remove());

        const notification = document.createElement('div');
        notification.className = `profile-notification ${type}`;
        
        const bgColors = {
            success: '#10b981',
            error: '#ef4444',
            warning: '#f59e0b',
            info: '#3b82f6'
        };

        const icons = {
            success: '✓',
            error: '✕',
            warning: '⚠',
            info: 'ℹ'
        };

        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: ${bgColors[type]};
            color: white;
            padding: 1rem 1.5rem;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            z-index: 10001;
            font-weight: 500;
            display: flex;
            align-items: center;
            gap: 0.5rem;
            animation: slideInRight 0.3s ease-out;
            max-width: 400px;
        `;

        notification.innerHTML = `
            <span style="font-size: 1.1rem;">${icons[type]}</span>
            <span>${message}</span>
        `;

        document.body.appendChild(notification);

        // Auto-remover após 4 segundos
        setTimeout(() => {
            notification.style.animation = 'slideOutRight 0.3s ease-in';
            setTimeout(() => notification.remove(), 300);
        }, 4000);

        // Remover ao clicar
        notification.addEventListener('click', () => {
            notification.style.animation = 'slideOutRight 0.3s ease-in';
            setTimeout(() => notification.remove(), 300);
        });
    }

    // Método para atualizar informações do usuário no cabeçalho
    updateUserHeader() {
        if (!this.currentUser) return;

        const userNameElement = document.querySelector('.user-name');
        const userRoleElement = document.querySelector('.user-role');
        const userAvatarElement = document.querySelector('.user-avatar');

        if (userNameElement) {
            userNameElement.textContent = this.currentUser.nome || 'Usuário';
        }

        if (userRoleElement) {
            userRoleElement.textContent = this.getRoleDisplayName(this.currentUser.tipoUsuario || this.currentUser.roleName || this.currentUser.role);
        }

        if (userAvatarElement) {
            const savedImage = localStorage.getItem(`profile_image_${this.currentUser.codigo || this.currentUser.id}`);
            if (savedImage) {
                userAvatarElement.style.backgroundImage = `url(${savedImage})`;
                userAvatarElement.style.backgroundSize = 'cover';
                userAvatarElement.style.backgroundPosition = 'center';
                userAvatarElement.textContent = '';
            } else {
                userAvatarElement.textContent = (this.currentUser.nome || 'U').charAt(0).toUpperCase();
            }
        }
    }

    getRoleDisplayName(role) {
        const roleNames = {
            'ADMIN': 'Administrador',
            'DIRETOR': 'Diretor',
            'GERENTE': 'Gerente',
            'SUPERVISOR': 'Supervisor',
            'VENDEDOR': 'Vendedor',
            'FUNCIONARIO': 'Funcionário'
        };

        return roleNames[role?.toUpperCase()] || 'Funcionário';
    }
}

// Adicionar estilos CSS necessários
const profileStyles = document.createElement('style');
profileStyles.textContent = `
    @keyframes slideInRight {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    
    @keyframes slideOutRight {
        from { transform: translateX(0); opacity: 1; }
        to { transform: translateX(100%); opacity: 0; }
    }
    
    @keyframes spin {
        from { transform: rotate(0deg); }
        to { transform: rotate(360deg); }
    }
    
    .profile-notification {
        font-family: 'Inter', sans-serif;
    }
`;
document.head.appendChild(profileStyles);

// Inicializar o sistema de perfil quando o DOM estiver pronto
document.addEventListener('DOMContentLoaded', () => {
    window.userProfileManager = new UserProfileManager();
});

// Tornar disponível globalmente
window.UserProfileManager = UserProfileManager;