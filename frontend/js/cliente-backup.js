/**
 * Maiconsoft - Módulo de Gestão de Clientes RESPONSIVO
 * Gerencia cadastro, busca e edição de clientes com AJAX moderno
 * @fileOverview Cliente management for Maiconsoft
 * @author Maiconsoft
 * @version 1.0.0
 */

// @ts-nocheck

class ClienteManager {
    constructor() {
        // Usar APIService do config.js em vez de ApiClient
        this.apiService = window.APIService;
        this.currentPage = 1;
        this.pageSize = 10;
        this.currentSearch = '';
        this.editingClientId = null;
        this.debounceTimer = null;
        
        this.init();
    }

    init() {
        console.log('🔧 ClienteManager inicializando...');
        if (!this.apiService) {
            console.error('❌ APIService não encontrado! Verifique se config.js foi carregado.');
            return;
        }
        console.log('✅ APIService disponível:', this.apiService.baseURL);
        
        this.setupEventListeners();
        this.setupTabs();
        this.setupResponsiveFeatures();
        this.loadClientes();
    }

    /**
     * Configurar recursos responsivos
     */
    setupResponsiveFeatures() {
        // Detectar se é mobile
        this.isMobile = window.innerWidth < 768;
        
        // Listener para mudanças de orientação/tamanho
        window.addEventListener('resize', this.debounce(() => {
            const wasMobile = this.isMobile;
            this.isMobile = window.innerWidth < 768;
            
            if (wasMobile !== this.isMobile) {
                this.adaptToScreenSize();
            }
        }, 250));

        // Configurar swipe em abas para mobile
        if (this.isMobile) {
            this.setupMobileSwipe();
        }

        // Configurar lazy loading para tabelas grandes
        this.setupLazyLoading();
    }

    /**
     * Adaptar interface ao tamanho da tela
     */
    adaptToScreenSize() {
        const table = document.querySelector('.cliente-table');
        if (table) {
            if (this.isMobile) {
                this.convertTableToCards();
            } else {
                this.convertCardsToTable();
            }
        }
    }

    /**
     * Configurar swipe para navegação em mobile
     */
    setupMobileSwipe() {
        const tabContent = document.querySelector('.tab-content');
        if (!tabContent) return;

        let startX = 0;
        let currentX = 0;
        let startY = 0;

        tabContent.addEventListener('touchstart', (e) => {
            startX = e.touches[0].clientX;
            startY = e.touches[0].clientY;
        });

        tabContent.addEventListener('touchmove', (e) => {
            currentX = e.touches[0].clientX;
            const deltaY = Math.abs(e.touches[0].clientY - startY);
            
            // Prevenir scroll horizontal se movimento é principalmente vertical
            if (deltaY > 30) return;
            
            e.preventDefault();
        });

        tabContent.addEventListener('touchend', () => {
            const deltaX = startX - currentX;
            
            if (Math.abs(deltaX) > 50) { // Mínimo 50px para swipe
                if (deltaX > 0) {
                    this.goToNextTab();
                } else {
                    this.goToPreviousTab();
                }
            }
        });
    }

    /**
     * Configurar lazy loading
     */
    setupLazyLoading() {
        // Observer para detectar quando elementos entram no viewport
        this.observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    const element = entry.target;
                    if (element.dataset.src) {
                        element.src = element.dataset.src;
                        element.removeAttribute('data-src');
                        this.observer.unobserve(element);
                    }
                }
            });
        });
    }

    /**
     * Navegar entre abas
     */
    goToNextTab() {
        const tabs = document.querySelectorAll('.tab-nav button');
        const currentTab = document.querySelector('.tab-nav button.active');
        const currentIndex = Array.from(tabs).indexOf(currentTab);
        
        if (currentIndex < tabs.length - 1) {
            tabs[currentIndex + 1].click();
        }
    }

    goToPreviousTab() {
        const tabs = document.querySelectorAll('.tab-nav button');
        const currentTab = document.querySelector('.tab-nav button.active');
        const currentIndex = Array.from(tabs).indexOf(currentTab);
        
        if (currentIndex > 0) {
            tabs[currentIndex - 1].click();
        }
    }

    setupEventListeners() {
        // Formulário de cadastro com validação em tempo real
        const form = document.querySelector('#cliente-form');
        if (form) {
            form.addEventListener('submit', (e) => this.handleFormSubmit(e));
            
            // Validação em tempo real
            const inputs = form.querySelectorAll('input, select, textarea');
            inputs.forEach(input => {
                input.addEventListener('blur', () => this.validateField(input));
                input.addEventListener('input', () => this.clearFieldError(input));
            });
        }

        // Busca com debounce
        const searchInput = document.querySelector('#search-input');
        if (searchInput) {
            searchInput.addEventListener('input', (e) => {
                this.debounceSearch(e.target.value);
            });
        }

        // Paginação
        document.addEventListener('click', (e) => {
            if (e.target.matches('.pagination-btn')) {
                const page = parseInt(e.target.dataset.page);
                this.loadClientes(page);
            }
        });

        // Botões de ação
        document.addEventListener('click', (e) => {
            if (e.target.matches('.btn-edit')) {
                const clienteId = e.target.dataset.id;
                this.editCliente(clienteId);
            }
            
            if (e.target.matches('.btn-delete')) {
                const clienteId = e.target.dataset.id;
                this.confirmDelete(clienteId);
            }
        });

        // Botão de novo cliente
        const newClientBtn = document.querySelector('#new-client-btn');
        if (newClientBtn) {
            newClientBtn.addEventListener('click', () => this.showNewClientForm());
        }

        // CEP lookup
        const cepInput = document.querySelector('#cep');
        if (cepInput) {
            cepInput.addEventListener('blur', () => this.lookupCEP(cepInput.value));
        }
    }

    /**
     * Debounce para busca
     */
    debounceSearch(searchTerm) {
        clearTimeout(this.debounceTimer);
        this.debounceTimer = setTimeout(() => {
            this.currentSearch = searchTerm;
            this.currentPage = 1;
            this.loadClientes();
        }, 300);
    }

    /**
     * Debounce genérico
     */
    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }

    /**
     * Carregar clientes com AJAX moderno
     */
    async loadClientes(page = 1) {
        try {
            this.currentPage = page;
            
            // Construir URL com parâmetros
            const params = new URLSearchParams({
                page: page - 1, // Backend usa zero-based
                size: this.pageSize
            });
            
            if (this.currentSearch) {
                params.append('search', this.currentSearch);
            }

            const url = `${API_CONFIG.baseURL}/clientes?${params}`;
            
            // Usar novo sistema AJAX
            const response = await ajax.get(url, {
                cache: true,
                showLoading: true
            });

            this.renderClientes(response);
            ajax.notify('Clientes carregados com sucesso!', 'success', 2000);
            
        } catch (error) {
            console.error('Erro ao carregar clientes:', error);
            
            // Fallback para dados mock em caso de erro
            if (error.message.includes('Failed to fetch') || error.message.includes('HTTP 500')) {
                ajax.notify('Modo demonstração - usando dados locais', 'warning', 3000);
                this.renderClientesMock();
            } else {
                ajax.notify('Erro ao carregar clientes', 'error');
                this.renderClientesError();
            }
        }
    }

    /**
     * Renderizar clientes responsivamente
     */
    renderClientes(data) {
        const container = document.querySelector('#clientes-list');
        if (!container) return;

        const clientes = data.content || data.clientes || [];
        
        if (clientes.length === 0) {
            container.innerHTML = this.renderEmptyState();
            return;
        }

        if (this.isMobile) {
            container.innerHTML = this.renderClientesCards(clientes);
        } else {
            container.innerHTML = this.renderClientesTable(clientes);
        }

        // Renderizar paginação se necessário
        if (data.totalPages && data.totalPages > 1) {
            this.renderPagination(data);
        }
    }

    /**
     * Renderizar clientes como cards (mobile)
     */
    renderClientesCards(clientes) {
        return `
            <div class="clientes-cards">
                ${clientes.map(cliente => `
                    <div class="cliente-card" data-id="${cliente.idCliente}">
                        <div class="card-header">
                            <h3 class="card-title">${cliente.razaoSocial}</h3>
                            <span class="client-type ${cliente.tipo === 'F' ? 'pf' : 'pj'}">
                                ${cliente.tipo === 'F' ? 'PF' : 'PJ'}
                            </span>
                        </div>
                        <div class="card-body">
                            <div class="client-info">
                                <div class="info-item">
                                    <strong>Código:</strong> ${cliente.codigo}
                                </div>
                                <div class="info-item">
                                    <strong>${cliente.tipo === 'F' ? 'CPF' : 'CNPJ'}:</strong> 
                                    ${this.formatDocument(cliente.cpfCnpj, cliente.tipo)}
                                </div>
                                ${cliente.email ? `
                                    <div class="info-item">
                                        <strong>Email:</strong> 
                                        <a href="mailto:${cliente.email}">${cliente.email}</a>
                                    </div>
                                ` : ''}
                                ${cliente.telefone ? `
                                    <div class="info-item">
                                        <strong>Telefone:</strong> 
                                        <a href="tel:${cliente.telefone}">${cliente.telefone}</a>
                                    </div>
                                ` : ''}
                                ${cliente.cidade ? `
                                    <div class="info-item">
                                        <strong>Cidade:</strong> ${cliente.cidade}/${cliente.estado}
                                    </div>
                                ` : ''}
                            </div>
                        </div>
                        <div class="card-actions">
                            <button class="btn btn-primary btn-edit" data-id="${cliente.idCliente}">
                                <span class="material-icons">edit</span>
                                Editar
                            </button>
                            <button class="btn btn-outline btn-delete" data-id="${cliente.idCliente}">
                                <span class="material-icons">delete</span>
                                Excluir
                            </button>
                        </div>
                    </div>
                `).join('')}
            </div>
        `;
    }

    /**
     * Renderizar clientes como tabela (desktop)
     */
    renderClientesTable(clientes) {
        return `
            <div class="table-responsive">
                <table class="table cliente-table">
                    <thead>
                        <tr>
                            <th>Código</th>
                            <th>Razão Social</th>
                            <th>Tipo</th>
                            <th>CPF/CNPJ</th>
                            <th>Email</th>
                            <th>Telefone</th>
                            <th>Cidade</th>
                            <th>Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${clientes.map(cliente => `
                            <tr data-id="${cliente.idCliente}">
                                <td>${cliente.codigo}</td>
                                <td>${cliente.razaoSocial}</td>
                                <td>
                                    <span class="client-type ${cliente.tipo === 'F' ? 'pf' : 'pj'}">
                                        ${cliente.tipo === 'F' ? 'Pessoa Física' : 'Pessoa Jurídica'}
                                    </span>
                                </td>
                                <td>${this.formatDocument(cliente.cpfCnpj, cliente.tipo)}</td>
                                <td>
                                    ${cliente.email ? `<a href="mailto:${cliente.email}">${cliente.email}</a>` : '-'}
                                </td>
                                <td>
                                    ${cliente.telefone ? `<a href="tel:${cliente.telefone}">${cliente.telefone}</a>` : '-'}
                                </td>
                                <td>${cliente.cidade ? `${cliente.cidade}/${cliente.estado}` : '-'}</td>
                                <td class="actions">
                                    <button class="btn btn-sm btn-primary btn-edit" data-id="${cliente.idCliente}">
                                        <span class="material-icons">edit</span>
                                    </button>
                                    <button class="btn btn-sm btn-outline btn-delete" data-id="${cliente.idCliente}">
                                        <span class="material-icons">delete</span>
                                    </button>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;
    }

    /**
     * Estado vazio
     */
    renderEmptyState() {
        return `
            <div class="empty-state">
                <div class="empty-icon">
                    <span class="material-icons">people_outline</span>
                </div>
                <h3>Nenhum cliente encontrado</h3>
                <p>Comece cadastrando seu primeiro cliente ou ajuste os filtros de busca.</p>
                <button class="btn btn-primary" onclick="clienteManager.showNewClientForm()">
                    <span class="material-icons">add</span>
                    Novo Cliente
                </button>
            </div>
        `;
    }

    /**
     * Estado de erro
     */
    renderClientesError() {
        const container = document.querySelector('#clientes-list');
        if (!container) return;

        container.innerHTML = `
            <div class="error-state">
                <div class="error-icon">
                    <span class="material-icons">error_outline</span>
                </div>
                <h3>Erro ao carregar clientes</h3>
                <p>Ocorreu um problema ao conectar com o servidor. Tente novamente.</p>
                <button class="btn btn-primary" onclick="clienteManager.loadClientes()">
                    <span class="material-icons">refresh</span>
                    Tentar Novamente
                </button>
            </div>
        `;
    }

    /**
     * Continua na próxima parte...
     */
        // Form submission
        const clienteForm = document.getElementById('clienteForm');
        if (clienteForm) {
            clienteForm.addEventListener('submit', (e) => this.handleFormSubmit(e));
        }

        // Tab buttons
        const tabCadastro = document.getElementById('tab-cadastro-btn');
        const tabLista = document.getElementById('tab-lista-btn');
        
        if (tabCadastro) {
            tabCadastro.addEventListener('click', (e) => {
                e.preventDefault();
                this.switchTab('cadastro');
            });
        }
        
        if (tabLista) {
            tabLista.addEventListener('click', (e) => {
                e.preventDefault();
                this.switchTab('lista');
            });
        }

        // Search functionality
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            let searchTimeout;
            searchInput.addEventListener('input', (e) => {
                clearTimeout(searchTimeout);
                searchTimeout = setTimeout(() => {
                    this.handleSearch(e.target.value);
                }, 300);
            });
        }

        // CEP lookup
        const cepInput = document.getElementById('cep');
        const cepButton = document.querySelector('.cep-button');
        if (cepInput && cepButton) {
            cepButton.addEventListener('click', () => this.buscarCEP());
            cepInput.addEventListener('blur', () => this.buscarCEP());
        }

        // Clear form button
        const clearFormBtn = document.getElementById('clearForm');
        if (clearFormBtn) {
            clearFormBtn.addEventListener('click', () => this.clearForm());
        }

        // Logout
        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', () => this.handleLogout());
        }

        // Form validation on field change
        this.setupFormValidation();
    }

    setupTabs() {
        const tabButtons = document.querySelectorAll('.tab-button');
        tabButtons.forEach(button => {
            button.addEventListener('click', (e) => this.handleTabChange(e));
        });
    }

    switchTab(tabName) {
        console.log('🔄 Mudando para aba:', tabName);
        
        try {
            // Update tab buttons
            const allTabs = document.querySelectorAll('.tab-button');
            console.log('📋 Tabs encontradas:', allTabs.length);
            allTabs.forEach(tab => {
                tab.classList.remove('active');
                tab.style.background = 'transparent';
                tab.style.color = '#6b7280';
            });
            
            // Activate current tab
            const activeTab = document.getElementById(`tab-${tabName}-btn`);
            console.log('🎯 Tab ativa encontrada:', activeTab ? 'sim' : 'não');
            if (activeTab) {
                activeTab.classList.add('active');
                activeTab.style.background = '#3b82f6';
                activeTab.style.color = 'white';
            }
            
            // Show/hide content
            const allContent = document.querySelectorAll('.tab-content');
            console.log('📄 Conteúdos encontrados:', allContent.length);
            allContent.forEach(content => {
                content.style.display = 'none';
            });
            
            const activeContent = document.getElementById(`tab-${tabName}`);
            console.log('📖 Conteúdo ativo encontrado:', activeContent ? 'sim' : 'não');
            if (activeContent) {
                activeContent.style.display = 'block';
            }
            
            // Load data for specific tabs
            if (tabName === 'lista') {
                console.log('📊 Carregando dados da lista...');
                // Initialize tab state
                const clientsTable = document.querySelector('#clientsTable');
                const emptyState = document.querySelector('#emptyState');
                
                console.log('🔍 Elementos encontrados:', {
                    clientsTable: clientsTable ? 'sim' : 'não',
                    emptyState: emptyState ? 'sim' : 'não'
                });
                
                if (clientsTable) clientsTable.style.display = 'none';
                if (emptyState) emptyState.style.display = 'block';
                
                this.loadClients();
            }
        } catch (error) {
            console.error('❌ Erro ao trocar de aba:', error);
        }
    }

    setupFormValidation() {
        const fields = ['nome', 'email', 'telefone', 'cpf', 'cep'];
        
        fields.forEach(fieldName => {
            const field = document.getElementById(fieldName);
            if (field) {
                field.addEventListener('blur', () => this.validateField(fieldName));
                field.addEventListener('input', () => this.clearFieldError(fieldName));
            }
        });
    }

    handleTabChange(event) {
        event.preventDefault();
        const clickedTab = event.target.closest('.tab-button');
        const tabName = clickedTab.dataset.tab;
        
        console.log('🔄 Mudando para aba:', tabName);
        
        // Update active tab styles
        document.querySelectorAll('.tab-button').forEach(tab => {
            tab.classList.remove('active');
            tab.style.background = 'transparent';
            tab.style.color = '#6b7280';
        });
        
        clickedTab.classList.add('active');
        clickedTab.style.background = '#3b82f6';
        clickedTab.style.color = 'white';
        
        // Show/hide content sections
        document.querySelectorAll('.tab-content').forEach(content => {
            content.style.display = 'none';
        });
        
        const targetContent = document.getElementById(tabName);
        if (targetContent) {
            targetContent.style.display = 'block';
            console.log('✅ Aba ativada:', tabName);
        } else {
            console.error('❌ Conteúdo da aba não encontrado:', tabName);
        }

        // Load data for specific tabs
        if (tabName === 'tab-lista') {
            console.log('📊 Carregando lista de clientes...');
            this.loadClients();
        }
    }

    async handleFormSubmit(event) {
        event.preventDefault();
        
        const formData = this.getFormData();
        
        if (!this.validateForm(formData)) {
            return;
        }

        const submitBtn = document.getElementById('submitBtn');
        this.setLoadingState(submitBtn, true);

        try {
            let response;
            
            if (this.editingClientId) {
                console.log('� Atualizando cliente ID:', this.editingClientId);
                response = await APIService.clientes.update(this.editingClientId, formData);
                this.showSuccess('Cliente atualizado com sucesso!');
            } else {
                console.log('📝 Criando novo cliente:', formData);
                response = await APIService.clientes.create(formData);
                this.showSuccess('Cliente cadastrado com sucesso!');
            }
            
            console.log('✅ Operação concluída:', response);
            
            this.clearForm();
            this.editingClientId = null;
            this.loadClients(); // Recarrega a lista
            
        } catch (error) {
            console.error('❌ Erro ao salvar cliente:', error);
            this.showError('Erro ao salvar cliente. Tente novamente.');
        } finally {
            this.setLoadingState(submitBtn, false);
        }
    }

    getFormData() {
        return {
            // Dados básicos
            tipoCliente: document.getElementById('tipoCliente')?.value?.trim(),
            nome: document.getElementById('nome')?.value?.trim(),
            nomeFantasia: document.getElementById('nomeFantasia')?.value?.trim() || null,
            cpfCnpj: document.getElementById('cpfCnpj')?.value?.trim(),
            status: document.getElementById('status')?.value?.trim() || 'ativo',
            
            // Contatos
            telefone1: document.getElementById('telefone1')?.value?.trim(),
            telefone2: document.getElementById('telefone2')?.value?.trim() || null,
            whatsapp: document.getElementById('whatsapp')?.value?.trim() || null,
            email: document.getElementById('email')?.value?.trim(),
            contatoPrincipal: document.getElementById('contatoPrincipal')?.value?.trim() || null,
            
            // Endereço
            cep: document.getElementById('cep')?.value?.trim() || null,
            logradouro: document.getElementById('logradouro')?.value?.trim() || null,
            numero: document.getElementById('numero')?.value?.trim() || null,
            complemento: document.getElementById('complemento')?.value?.trim() || null,
            bairro: document.getElementById('bairro')?.value?.trim() || null,
            cidade: document.getElementById('cidade')?.value?.trim() || null,
            estado: document.getElementById('estado')?.value?.trim() || null,
            
            // Informações comerciais
            segmento: document.getElementById('segmento')?.value?.trim() || null,
            porte: document.getElementById('porte')?.value?.trim() || null,
            origem: document.getElementById('origem')?.value?.trim() || null,
            observacoes: document.getElementById('observacoes')?.value?.trim() || null
        };
    }

    validateForm(data) {
        let isValid = true;
        
        // Required fields
        const requiredFields = ['tipoCliente', 'nome', 'cpfCnpj', 'telefone1', 'email'];
        requiredFields.forEach(field => {
            if (!data[field]) {
                this.showFieldError(field, 'Campo obrigatório');
                isValid = false;
            }
        });

        // Email validation
        if (data.email && !this.isValidEmail(data.email)) {
            this.showFieldError('email', 'Email inválido');
            isValid = false;
        }

        // Phone validation
        if (data.telefone1 && !this.isValidPhone(data.telefone1)) {
            this.showFieldError('telefone', 'Telefone inválido');
            isValid = false;
        }

        // CPF validation - temporarily disabled
        // if (data.cpf && !this.formValidator.isValidCPF(data.cpf)) {
        //     this.showFieldError('cpf', 'CPF inválido');
        //     isValid = false;
        // }

        // CEP validation - temporarily disabled
        // if (data.cep && !this.formValidator.isValidCEP(data.cep)) {
        //     this.showFieldError('cep', 'CEP inválido');
        //     isValid = false;
        // }

        return isValid;
    }

    validateField(fieldName) {
        const input = document.getElementById(fieldName);
        const value = input?.value?.trim();

        this.clearFieldError(fieldName);

        switch (fieldName) {
            case 'nome':
                if (!value) {
                    this.showFieldError(fieldName, 'Nome é obrigatório');
                    return false;
                } else if (value.length < 2) {
                    this.showFieldError(fieldName, 'Nome deve ter pelo menos 2 caracteres');
                    return false;
                }
                break;
                
            case 'email':
                if (!value) {
                    this.showFieldError(fieldName, 'Email é obrigatório');
                    return false;
                } // Email validation temporarily disabled
                // else if (!this.formValidator.isValidEmail(value)) {
                //     this.showFieldError(fieldName, 'Email inválido');
                //     return false;
                // }
                break;
                
            case 'telefone':
                if (!value) {
                    this.showFieldError(fieldName, 'Telefone é obrigatório');
                    return false;
                } // Phone validation temporarily disabled
                // else if (!this.formValidator.isValidPhone(value)) {
                //     this.showFieldError(fieldName, 'Telefone inválido');
                //     return false;
                // }
                break;
                
            case 'cpf':
                // CPF validation temporarily disabled
                // if (value && !this.formValidator.isValidCPF(value)) {
                //     this.showFieldError(fieldName, 'CPF inválido');
                //     return false;
                // }
                break;
                
            case 'cep':
                // CEP validation temporarily disabled
                // if (value && !this.formValidator.isValidCEP(value)) {
                //     this.showFieldError(fieldName, 'CEP inválido');
                //     return false;
                // }
                break;
        }

        this.showFieldSuccess(fieldName);
        return true;
    }

    async buscarCEP() {
        const cepInput = document.getElementById('cep');
        const cep = cepInput?.value?.replace(/\D/g, '');
        
        if (!cep || cep.length !== 8) {
            return;
        }

        const cepButton = document.querySelector('.cep-button');
        if (cepButton) {
            cepButton.disabled = true;
            cepButton.innerHTML = '<i class="material-icons spinning">refresh</i>';
        }

        try {
            const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
            const data = await response.json();
            
            if (!data.erro) {
                document.getElementById('endereco').value = data.logradouro || '';
                document.getElementById('bairro').value = data.bairro || '';
                document.getElementById('cidade').value = data.localidade || '';
                document.getElementById('estado').value = data.uf || '';
                
                // Focus on number field
                document.getElementById('numero')?.focus();
                
                this.showSuccess('Endereço preenchido automaticamente');
            } else {
                this.showError('CEP não encontrado');
            }
        } catch (error) {
            console.error('CEP lookup error:', error);
            this.showError('Erro ao buscar CEP');
        } finally {
            if (cepButton) {
                cepButton.disabled = false;
                cepButton.innerHTML = '<i class="material-icons">search</i> Buscar';
            }
        }
    }

    async loadClients() {
        console.log('📡 Carregando clientes do banco de dados');
        this.showTableLoading(true);
        
        try {
            const response = await APIService.clientes.getAll();
            const clients = response.data || response;
            console.log('📋 Clientes carregados:', clients.length, 'registros');
            
            this.renderClientsTable(clients);
            this.renderPagination(1, clients.length);
            
            this.showNotification(`${clients.length} clientes carregados com sucesso`, 'success');
            
        } catch (error) {
            console.error('❌ Erro ao carregar clientes:', error);
            this.showError('Erro ao carregar lista de clientes');
            
            // Fallback to mock data in case of error
            const mockClients = this.getMockClients();
            this.renderClientsTable(mockClients);
            this.renderPagination(1, mockClients.length);
        }
        
        this.showTableLoading(false);
        console.log('✅ Carregamento de clientes concluído');
    }

    getMockClients() {
        return [
            {
                id: 1,
                nome: 'João Silva Construções LTDA',
                cpfCnpj: '12.345.678/0001-90',
                telefone1: '(11) 99999-9999',
                whatsapp: '(11) 99999-9999',
                email: 'contato@joaosilva.com.br',
                tipoCliente: 'pj',
                status: 'ativo',
                cidade: 'São Paulo',
                created_at: '2024-01-15'
            },
            {
                id: 2,
                nome: 'Maria Santos Oliveira',
                cpfCnpj: '123.456.789-00',
                telefone1: '(11) 88888-8888',
                whatsapp: '(11) 98888-8888',
                email: 'maria.santos@email.com',
                tipoCliente: 'pf',
                status: 'prospecto',
                cidade: 'Rio de Janeiro',
                created_at: '2024-01-14'
            },
            {
                id: 3,
                nome: 'TechCorp Soluções Digitais',
                cpfCnpj: '98.765.432/0001-10',
                telefone1: '(11) 77777-7777',
                whatsapp: '(11) 97777-7777',
                email: 'contato@techcorp.com.br',
                tipoCliente: 'pj',
                status: 'ativo',
                cidade: 'Campinas',
                created_at: '2024-01-10'
            },
            {
                id: 4,
                nome: 'Ana Paula Costa',
                cpfCnpj: '987.654.321-00',
                telefone1: '(11) 66666-6666',
                whatsapp: '(11) 96666-6666',
                email: 'ana.costa@gmail.com',
                tipoCliente: 'pf',
                status: 'inativo',
                cidade: 'Santos',
                created_at: '2024-01-05'
            },
            {
                id: 5,
                nome: 'Construtora BemViver LTDA',
                cpfCnpj: '11.222.333/0001-44',
                telefone1: '(11) 55555-5555',
                whatsapp: '(11) 95555-5555',
                email: 'admin@bemviver.com.br',
                tipoCliente: 'pj',
                status: 'ativo',
                cidade: 'São Paulo',
                created_at: '2024-01-03'
            }
        ];
    }

    renderClientsTable(clients) {
        console.log('🎨 Renderizando tabela com', clients ? clients.length : 0, 'clientes');
        
        const tbody = document.querySelector('#clientsTableBody');
        const clientsTable = document.querySelector('#clientsTable');
        const emptyState = document.querySelector('#emptyState');
        
        console.log('🔍 Elementos encontrados:', {
            tbody: !!tbody,
            clientsTable: !!clientsTable,
            emptyState: !!emptyState
        });
        
        if (!tbody) {
            console.error('❌ Elemento tbody #clientsTableBody não encontrado!');
            return;
        }

        if (!clients || clients.length === 0) {
            // Ocultar tabela e mostrar estado vazio
            if (clientsTable) clientsTable.style.display = 'none';
            if (emptyState) emptyState.style.display = 'block';
            return;
        }

        // Mostrar tabela e ocultar estado vazio
        if (clientsTable) {
            clientsTable.style.display = 'block';
            console.log('✅ Tabela exibida (display: block)');
        } else {
            console.error('❌ Elemento clientsTable não encontrado!');
        }
        
        if (emptyState) {
            emptyState.style.display = 'none';
            console.log('✅ Estado vazio ocultado');
        }

        console.log('🏗️ Gerando HTML para', clients.length, 'clientes');
        const htmlContent = clients.map(client => `
            <tr>
                <td data-label="Nome">
                    <div class="cliente-info">
                        <div class="cliente-name">${client.nome || client.nomeFantasia || 'Sem nome'}</div>
                        <div class="cliente-detail">
                            ${client.tipoCliente === 'pf' ? 'CPF' : 'CNPJ'}: ${client.cpfCnpj || 'Não informado'}
                        </div>
                    </div>
                </td>
                <td data-label="CPF/CNPJ">${client.cpfCnpj || '-'}</td>
                <td data-label="Telefone">${client.telefone1 || client.whatsapp || '-'}</td>
                <td data-label="Email">${client.email || '-'}</td>
                <td data-label="Status">
                    <span class="status-badge status-${client.status || 'ativo'}">${(client.status || 'ativo').toUpperCase()}</span>
                </td>
                <td data-label="Ações">
                    <div class="action-buttons">
                        <button class="action-btn view" onclick="clienteManager.viewClient(${client.id})" title="Visualizar">
                            <i class="material-icons">visibility</i>
                        </button>
                        <button class="action-btn edit" onclick="clienteManager.editClient(${client.id})" title="Editar">
                            <i class="material-icons">edit</i>
                        </button>
                        <button class="action-btn delete" onclick="clienteManager.deleteClient(${client.id})" title="Excluir">
                            <i class="material-icons">delete</i>
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');
        
        tbody.innerHTML = htmlContent;
        console.log('✅ HTML inserido no tbody, comprimento:', htmlContent.length);
        console.log('🎯 Primeiro cliente renderizado:', clients[0]?.nome);
    }

    renderPagination(totalPages, totalItems) {
        // Update counts in the HTML
        const showingCount = document.getElementById('showingCount');
        const totalCount = document.getElementById('totalCount');
        
        if (showingCount && totalCount) {
            const startItem = (this.currentPage - 1) * this.pageSize + 1;
            const endItem = Math.min(this.currentPage * this.pageSize, totalItems);
            showingCount.textContent = endItem;
            totalCount.textContent = totalItems;
            console.log('📊 Contadores atualizados:', `${endItem} de ${totalItems} clientes`);
        }

        if (paginationControls) {
            let controls = `
                <button class="page-btn" ${this.currentPage === 1 ? 'disabled' : ''} onclick="clienteManager.goToPage(${this.currentPage - 1})">
                    <i class="material-icons">chevron_left</i>
                </button>
            `;

            // Show page numbers
            for (let i = 1; i <= totalPages; i++) {
                if (i === this.currentPage || i === 1 || i === totalPages || Math.abs(i - this.currentPage) <= 1) {
                    controls += `
                        <button class="page-btn ${i === this.currentPage ? 'active' : ''}" onclick="clienteManager.goToPage(${i})">
                            ${i}
                        </button>
                    `;
                }
            }

            controls += `
                <button class="page-btn" ${this.currentPage === totalPages ? 'disabled' : ''} onclick="clienteManager.goToPage(${this.currentPage + 1})">
                    <i class="material-icons">chevron_right</i>
                </button>
            `;

            paginationControls.innerHTML = controls;
        }
    }

    handleSearch(searchTerm) {
        this.currentSearch = searchTerm;
        this.currentPage = 1;
        this.loadClients();
    }

    goToPage(page) {
        this.currentPage = page;
        this.loadClients();
    }

    async viewClient(id) {
        try {
            const response = await APIService.clientes.getById(id);
            const client = response.data;
            
            // Show client details in a modal or navigate to detail view
            alert(`Cliente: ${client.nome}\nEmail: ${client.email}\nTelefone: ${client.telefone}`);
        } catch (error) {
            this.showError('Erro ao carregar dados do cliente');
        }
    }

    async editClient(id) {
        try {
            const response = await APIService.clientes.getById(id);
            const client = response.data;
            
            // Fill form with client data
            this.fillForm(client);
            this.editingClientId = id;
            
            // Switch to form tab
            document.querySelector('[data-tab="cadastro"]').click();
            
            // Update submit button text
            const submitBtn = document.getElementById('submitBtn');
            if (submitBtn) {
                submitBtn.innerHTML = '<i class="material-icons">save</i> Atualizar Cliente';
            }
            
        } catch (error) {
            this.showError('Erro ao carregar dados do cliente');
        }
    }

    fillForm(client) {
        const fields = ['nome', 'email', 'telefone', 'cpf', 'cep', 'endereco', 'numero', 'complemento', 'bairro', 'cidade', 'estado', 'observacoes'];
        
        fields.forEach(field => {
            const input = document.getElementById(field);
            if (input && client[field]) {
                input.value = client[field];
            }
        });
    }

    async deleteClient(id) {
        if (!confirm('Tem certeza que deseja excluir este cliente?')) {
            return;
        }

        try {
            await APIService.clientes.delete(id);
            this.showSuccess('Cliente excluído com sucesso!');
            this.loadClients();
        } catch (error) {
            this.showError('Erro ao excluir cliente');
        }
    }

    clearForm() {
        const form = document.getElementById('clienteForm');
        if (form) {
            form.reset();
        }
        
        this.editingClientId = null;
        
        // Reset submit button
        const submitBtn = document.getElementById('submitBtn');
        if (submitBtn) {
            submitBtn.innerHTML = '<i class="material-icons">save</i> Cadastrar Cliente';
        }
        
        // Clear all field errors
        document.querySelectorAll('.form-field').forEach(field => {
            field.classList.remove('invalid', 'valid');
        });
        
        document.querySelectorAll('.field-error').forEach(error => {
            error.remove();
        });
    }

    handleLogout() {
        if (confirm('Deseja realmente sair do sistema?')) {
            AuthService.logout();
        }
    }

    // Utility methods
    showFieldError(fieldName, message) {
        const field = document.getElementById(fieldName);
        const fieldContainer = field?.closest('.form-field');
        
        if (fieldContainer) {
            fieldContainer.classList.add('invalid');
            fieldContainer.classList.remove('valid');
            
            const existingError = fieldContainer.querySelector('.field-error');
            if (existingError) {
                existingError.remove();
            }
            
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
            button.disabled = true;
            button.innerHTML = '<i class="material-icons spinning">refresh</i> Salvando...';
        } else {
            button.disabled = false;
            const text = this.editingClientId ? 'Atualizar Cliente' : 'Cadastrar Cliente';
            button.innerHTML = `<i class="material-icons">save</i> ${text}`;
        }
    }

    showTableLoading(loading) {
        const tbody = document.querySelector('#clientsTableBody');
        if (!tbody) return;
        
        if (loading) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="table-loading">
                        <i class="material-icons spinning">refresh</i>
                        <p>Carregando clientes...</p>
                    </td>
                </tr>
            `;
        } else {
            // Clear loading state - the renderClientsTable will populate with data
            tbody.innerHTML = '';
        }
    }

    showSuccess(message) {
        this.showNotification(message, 'success');
    }

    showError(message) {
        this.showNotification(message, 'error');
    }

    showNotification(message, type = 'info') {
        // Remove existing notifications
        document.querySelectorAll('.notification').forEach(n => n.remove());
        
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.innerHTML = `
            <i class="material-icons">${this.getNotificationIcon(type)}</i>
            <span style="font-size: 1rem; font-weight: 600;">${message}</span>
        `;

        document.body.appendChild(notification);
        
        // Auto-hide after 4 seconds for success/info, 6 seconds for errors
        const duration = type === 'error' ? 6000 : 4000;
        setTimeout(() => {
            if (notification.parentNode) {
                notification.style.animation = 'slideOut 0.3s ease-in-out forwards';
                setTimeout(() => notification.remove(), 300);
            }
        }, duration);
        
        // Click to dismiss
        notification.addEventListener('click', () => {
            notification.style.animation = 'slideOut 0.3s ease-in-out forwards';
            setTimeout(() => notification.remove(), 300);
        });
    }

    getNotificationIcon(type) {
        const icons = {
            success: 'check_circle',
            error: 'error',
            warning: 'warning',
            info: 'info'
        };
        return icons[type] || 'info';
    }

    formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('pt-BR');
    }

    // Basic validation methods
    isValidEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    isValidPhone(phone) {
        // Remove non-numeric characters and check if it has 10 or 11 digits
        const cleanPhone = phone.replace(/\D/g, '');
        return cleanPhone.length >= 10 && cleanPhone.length <= 11;
    }

    isValidCPF(cpf) {
        // Basic CPF format validation (XXX.XXX.XXX-XX or XXXXXXXXXXX)
        const cleanCPF = cpf.replace(/\D/g, '');
        return cleanCPF.length === 11;
    }

    isValidCNPJ(cnpj) {
        // Basic CNPJ format validation (XX.XXX.XXX/XXXX-XX or XXXXXXXXXXXXXX)
        const cleanCNPJ = cnpj.replace(/\D/g, '');
        return cleanCNPJ.length === 14;
    }
    
    // Search functionality
    buscarClientes() {
        const searchTerm = document.getElementById('searchInput')?.value || '';
        const statusFilter = document.getElementById('statusFilter')?.value || '';
        
        console.log('🔍 Buscando clientes:', { searchTerm, statusFilter });
        
        this.currentSearch = searchTerm;
        this.currentPage = 1;
        this.loadClients();
    }

    // View client details
    viewClient(clientId) {
        console.log('👁️ Visualizando cliente ID:', clientId);
        
        // Find client in mock data
        const client = this.getMockClients().find(c => c.id === clientId);
        
        if (!client) {
            this.showError('Cliente não encontrado');
            return;
        }

        // Generate view content
        const viewContent = `
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 2rem;">
                <div>
                    <h3 style="margin: 0 0 1rem 0; color: #374151; font-size: 1.1rem; border-bottom: 2px solid #e5e7eb; padding-bottom: 0.5rem;">Informações Básicas</h3>
                    <div style="display: flex; flex-direction: column; gap: 0.75rem;">
                        <div><strong>Nome:</strong> ${client.nome}</div>
                        <div><strong>Tipo:</strong> ${client.tipoCliente === 'pf' ? 'Pessoa Física' : 'Pessoa Jurídica'}</div>
                        <div><strong>CPF/CNPJ:</strong> ${client.cpfCnpj}</div>
                        <div><strong>Status:</strong> <span class="status-badge status-${client.status}">${client.status.toUpperCase()}</span></div>
                        <div><strong>Cidade:</strong> ${client.cidade || 'Não informado'}</div>
                        <div><strong>Cadastrado em:</strong> ${this.formatDate(client.created_at)}</div>
                    </div>
                </div>
                <div>
                    <h3 style="margin: 0 0 1rem 0; color: #374151; font-size: 1.1rem; border-bottom: 2px solid #e5e7eb; padding-bottom: 0.5rem;">Contatos</h3>
                    <div style="display: flex; flex-direction: column; gap: 0.75rem;">
                        <div><strong>Telefone:</strong> ${client.telefone1 || 'Não informado'}</div>
                        <div><strong>WhatsApp:</strong> ${client.whatsapp || 'Não informado'}</div>
                        <div><strong>Email:</strong> ${client.email || 'Não informado'}</div>
                    </div>
                </div>
            </div>
        `;

        document.getElementById('viewModalContent').innerHTML = viewContent;
        document.getElementById('viewModal').style.display = 'flex';
    }

    // Edit client
    editClient(clientId) {
        console.log('✏️ Editando cliente ID:', clientId);
        
        // Find client in mock data
        const client = this.getMockClients().find(c => c.id === clientId);
        
        if (!client) {
            this.showError('Cliente não encontrado');
            return;
        }

        // Store editing client ID
        this.editingClientId = clientId;

        // Generate edit form
        const editContent = `
            <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 1.5rem;">
                <div>
                    <label style="display: block; font-size: 0.85rem; font-weight: 600; color: #374151; margin-bottom: 0.4rem;">Tipo de Cliente</label>
                    <select id="editTipoCliente" style="width: 100%; padding: 0.6rem; border: 1px solid #d1d5db; font-size: 0.9rem;">
                        <option value="pf" ${client.tipoCliente === 'pf' ? 'selected' : ''}>Pessoa Física</option>
                        <option value="pj" ${client.tipoCliente === 'pj' ? 'selected' : ''}>Pessoa Jurídica</option>
                    </select>
                </div>
                <div>
                    <label style="display: block; font-size: 0.85rem; font-weight: 600; color: #374151; margin-bottom: 0.4rem;">Status</label>
                    <select id="editStatus" style="width: 100%; padding: 0.6rem; border: 1px solid #d1d5db; font-size: 0.9rem;">
                        <option value="ativo" ${client.status === 'ativo' ? 'selected' : ''}>Ativo</option>
                        <option value="prospecto" ${client.status === 'prospecto' ? 'selected' : ''}>Prospecto</option>
                        <option value="inativo" ${client.status === 'inativo' ? 'selected' : ''}>Inativo</option>
                    </select>
                </div>
            </div>

            <div style="margin-top: 1rem;">
                <label style="display: block; font-size: 0.85rem; font-weight: 600; color: #374151; margin-bottom: 0.4rem;">Nome Completo / Razão Social</label>
                <input type="text" id="editNome" value="${client.nome}" style="width: 100%; padding: 0.6rem; border: 1px solid #d1d5db; font-size: 0.9rem;">
            </div>

            <div style="margin-top: 1rem;">
                <label style="display: block; font-size: 0.85rem; font-weight: 600; color: #374151; margin-bottom: 0.4rem;">CPF/CNPJ</label>
                <input type="text" id="editCpfCnpj" value="${client.cpfCnpj}" style="width: 100%; padding: 0.6rem; border: 1px solid #d1d5db; font-size: 0.9rem;">
            </div>

            <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 1rem; margin-top: 1rem;">
                <div>
                    <label style="display: block; font-size: 0.85rem; font-weight: 600; color: #374151; margin-bottom: 0.4rem;">Telefone</label>
                    <input type="text" id="editTelefone1" value="${client.telefone1 || ''}" style="width: 100%; padding: 0.6rem; border: 1px solid #d1d5db; font-size: 0.9rem;">
                </div>
                <div>
                    <label style="display: block; font-size: 0.85rem; font-weight: 600; color: #374151; margin-bottom: 0.4rem;">WhatsApp</label>
                    <input type="text" id="editWhatsapp" value="${client.whatsapp || ''}" style="width: 100%; padding: 0.6rem; border: 1px solid #d1d5db; font-size: 0.9rem;">
                </div>
                <div>
                    <label style="display: block; font-size: 0.85rem; font-weight: 600; color: #374151; margin-bottom: 0.4rem;">Email</label>
                    <input type="email" id="editEmail" value="${client.email || ''}" style="width: 100%; padding: 0.6rem; border: 1px solid #d1d5db; font-size: 0.9rem;">
                </div>
            </div>

            <div style="margin-top: 1rem;">
                <label style="display: block; font-size: 0.85rem; font-weight: 600; color: #374151; margin-bottom: 0.4rem;">Cidade</label>
                <input type="text" id="editCidade" value="${client.cidade || ''}" style="width: 100%; padding: 0.6rem; border: 1px solid #d1d5db; font-size: 0.9rem;">
            </div>
        `;

        document.getElementById('editModalContent').innerHTML = editContent;
        document.getElementById('editModal').style.display = 'flex';
        
        // Setup form submission
        document.getElementById('editForm').onsubmit = (e) => this.handleEditSubmit(e);
    }

    // Handle edit form submission
    async handleEditSubmit(event) {
        event.preventDefault();
        
        const editData = {
            tipoCliente: document.getElementById('editTipoCliente').value,
            nome: document.getElementById('editNome').value,
            cpfCnpj: document.getElementById('editCpfCnpj').value,
            status: document.getElementById('editStatus').value,
            telefone1: document.getElementById('editTelefone1').value,
            whatsapp: document.getElementById('editWhatsapp').value,
            email: document.getElementById('editEmail').value,
            cidade: document.getElementById('editCidade').value
        };

        console.log('Salvando alteracoes:', editData);

        // Update UI
        const submitBtn = document.getElementById('editSubmitBtn');
        submitBtn.innerHTML = '<span class="material-icons">hourglass_empty</span> Salvando...';
        submitBtn.disabled = true;

        try {
            const response = await APIService.clientes.update(this.editingClientId, editData);
            console.log('Cliente atualizado:', response);
            
            this.showSuccess('Cliente atualizado com sucesso!');
            this.closeEditModal();
            this.loadClients(); // Recarrega a lista
            
        } catch (error) {
            console.error('Erro ao atualizar:', error);
            this.showError('Erro ao atualizar cliente');
        } finally {
            submitBtn.innerHTML = '<span class="material-icons">save</span> Salvar Alteracoes';
            submitBtn.disabled = false;
        }
    }

    // Delete client (placeholder)
    deleteClient(clientId) {
        console.log('🗑️ Excluir cliente ID:', clientId);
        
        if (confirm('Tem certeza que deseja excluir este cliente?')) {
            console.log('💾 Simulando exclusão...');
            this.showSuccess('Cliente excluído com sucesso! (Modo demonstração)');
            this.loadClients();
        }
    }

    // Modal control methods
    closeViewModal() {
        document.getElementById('viewModal').style.display = 'none';
    }

    closeEditModal() {
        document.getElementById('editModal').style.display = 'none';
        this.editingClientId = null;
    }
}

// Make ClienteManager available globally
window.ClienteManager = ClienteManager;

// Global functions
window.buscarClientes = function() {
    if (window.clienteManager) {
        window.clienteManager.buscarClientes();
    }
};

window.closeViewModal = function() {
    if (window.clienteManager) {
        window.clienteManager.closeViewModal();
    }
};

window.closeEditModal = function() {
    if (window.clienteManager) {
        window.clienteManager.closeEditModal();
    }
};