/**
 * Maiconsoft - Módulo do Dashboard
 * Gerencia métricas, gráficos e dados para administradores
 */

class DashboardManager {
    constructor() {
        this.apiClient = new ApiClient();
        this.authService = new AuthService();
        
        this.currentPeriod = '30d';
        this.refreshInterval = null;
        
        this.init();
    }

    init() {
        this.checkAuthentication();
        this.setupEventListeners();
        this.loadDashboardData();
        this.addQuickNavigationButtons();
        this.startAutoRefresh();
    }

    checkAuthentication() {
        if (!this.authService.isAuthenticated()) {
            window.location.href = 'login.html';
            return;
        }

        const user = this.authService.getUser();
        if (!user || !['admin', 'diretor'].includes(user.perfil?.toLowerCase())) {
            window.location.href = '#cliente'; // Redirect to appropriate page
            return;
        }

        this.updateUserInfo(user);
    }

    setupEventListeners() {
        // Tab navigation
        const navTabs = document.querySelectorAll('.nav-tab');
        navTabs.forEach(tab => {
            tab.addEventListener('click', (e) => this.handleTabChange(e));
        });

        // Period selection
        const periodButtons = document.querySelectorAll('.period-btn');
        periodButtons.forEach(btn => {
            btn.addEventListener('click', (e) => this.handlePeriodChange(e));
        });

        // Refresh button
        const refreshBtn = document.getElementById('refreshDashboard');
        if (refreshBtn) {
            refreshBtn.addEventListener('click', () => this.refreshDashboard());
        }

        // Logout
        const logoutBtn = document.getElementById('logoutBtn');
        if (logoutBtn) {
            logoutBtn.addEventListener('click', () => this.handleLogout());
        }

        // Quick actions
        const addClientBtn = document.getElementById('addClientBtn');
        if (addClientBtn) {
            addClientBtn.addEventListener('click', () => {
                window.location.href = '#cliente';
            });
        }

        const exportBtn = document.getElementById('exportBtn');
        if (exportBtn) {
            exportBtn.addEventListener('click', () => this.exportData());
        }

        // Navigation links
        this.setupNavigationLinks();
    }

    setupNavigationLinks() {
        // Dashboard navigation links
        const dashboardLink = document.querySelector('[data-route="dashboard"]');
        if (dashboardLink) {
            dashboardLink.addEventListener('click', (e) => {
                e.preventDefault();
                this.navigateToPage('dashboard');
            });
        }

        // Clientes navigation links
        const clientesLinks = document.querySelectorAll('[data-route="clientes"]');
        clientesLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                this.navigateToPage('clientes');
            });
        });

        // Vendas navigation links
        const vendasLinks = document.querySelectorAll('[data-route="vendas"]');
        vendasLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                this.navigateToPage('vendas');
            });
        });

        // Usuários navigation links
        const usuariosLinks = document.querySelectorAll('[data-route="usuarios"]');
        usuariosLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                this.navigateToPage('usuarios');
            });
        });

        // Relatórios navigation links
        const relatoriosLinks = document.querySelectorAll('[data-route="relatorios"]');
        relatoriosLinks.forEach(link => {
            link.addEventListener('click', (e) => {
                e.preventDefault();
                this.navigateToPage('relatorios');
            });
        });

        // Generic navigation handler for any data-route elements
        const allNavigationElements = document.querySelectorAll('[data-route]');
        allNavigationElements.forEach(element => {
            if (!element.hasAttribute('data-navigation-handled')) {
                element.addEventListener('click', (e) => {
                    e.preventDefault();
                    const route = element.getAttribute('data-route');
                    if (route && route !== 'dashboard') {
                        this.navigateToPage(route);
                    }
                });
                element.setAttribute('data-navigation-handled', 'true');
            }
        });
    }

    updateUserInfo(user) {
        const userNameElement = document.querySelector('.user-info span');
        const userAvatarElement = document.querySelector('.user-avatar');
        
        if (userNameElement) {
            userNameElement.textContent = user.nome || user.email;
        }
        
        if (userAvatarElement) {
            const initials = this.getInitials(user.nome || user.email);
            userAvatarElement.textContent = initials;
        }
    }

    getInitials(name) {
        return name
            .split(' ')
            .map(part => part[0])
            .join('')
            .substring(0, 2)
            .toUpperCase();
    }

    async loadDashboardData() {
        this.showLoadingState(true);
        
        try {
            // Load all dashboard data concurrently
            const [metrics, charts, recentClients, recentSales] = await Promise.all([
                this.loadMetrics(),
                this.loadChartData(),
                this.loadRecentClients(),
                this.loadRecentSales()
            ]);

            this.renderMetrics(metrics);
            this.renderCharts(charts);
            this.renderRecentClients(recentClients);
            this.renderRecentSales(recentSales);
            
        } catch (error) {
            console.error('Error loading dashboard:', error);
            this.showError('Erro ao carregar dados do dashboard');
        } finally {
            this.showLoadingState(false);
        }
    }

    async loadMetrics() {
        try {
            // Converter período do formato frontend para dias
            const daysMap = {
                '7d': 7,
                '30d': 30,
                '90d': 90,
                '365d': 365
            };
            const days = daysMap[this.currentPeriod] || 30;
            
            const response = await this.apiClient.get(`/dashboard/metrics?days=${days}`);
            
            if (response.success && response.data) {
                return this.formatMetricsForDisplay(response.data);
            }
            throw new Error('Failed to load metrics');
        } catch (error) {
            console.error('Error loading metrics:', error);
            console.warn('Using mock metrics data');
            return this.getMockMetrics();
        }
    }

    formatMetricsForDisplay(data) {
        // Formatar dados reais para o formato esperado pelo frontend
        return {
            totalClients: { 
                value: data.totalClients, 
                change: data.clientsGrowth ? `${data.clientsGrowth > 0 ? '+' : ''}${data.clientsGrowth.toFixed(1)}%` : '0%',
                positive: data.clientsGrowth > 0 
            },
            totalSales: { 
                value: data.totalSales, 
                change: data.salesGrowth ? `${data.salesGrowth > 0 ? '+' : ''}${data.salesGrowth.toFixed(1)}%` : '0%',
                positive: data.salesGrowth > 0 
            },
            avgTicket: { 
                value: parseFloat(data.averageTicket || 0), 
                change: '0%', // Crescimento do ticket médio pode ser calculado no backend se necessário
                positive: true 
            },
            conversionRate: { 
                value: parseFloat(data.conversionRate || 0), 
                change: '0%', // Crescimento da conversão pode ser calculado no backend se necessário
                positive: true 
            },
            totalRevenue: {
                value: parseFloat(data.totalRevenue || 0),
                change: data.revenueGrowth ? `${data.revenueGrowth > 0 ? '+' : ''}${data.revenueGrowth.toFixed(1)}%` : '0%',
                positive: data.revenueGrowth > 0
            },
            activeUsers: data.activeUsers || 0,
            newClientsThisPeriod: data.newClientsThisPeriod || 0,
            salesThisPeriod: data.salesThisPeriod || 0,
            revenueThisPeriod: parseFloat(data.revenueThisPeriod || 0)
        };
    }

    getMockMetrics() {
        return {
            totalClients: { value: 1247, change: '+12%', positive: true },
            totalSales: { value: 89543, change: '+8%', positive: true },
            avgTicket: { value: 2340, change: '-3%', positive: false },
            conversionRate: { value: 18.5, change: '+5%', positive: true }
        };
    }

    renderMetrics(metrics) {
        const metricsData = [
            {
                id: 'totalClients',
                title: 'Total de Clientes',
                value: metrics.totalClients.value.toLocaleString('pt-BR'),
                change: metrics.totalClients.change,
                positive: metrics.totalClients.positive,
                icon: 'people',
                class: 'primary'
            },
            {
                id: 'totalRevenue',
                title: 'Receita Total',
                value: `R$ ${parseFloat(metrics.totalRevenue?.value || 0).toLocaleString('pt-BR', {minimumFractionDigits: 2})}`,
                change: metrics.totalRevenue?.change || '0%',
                positive: metrics.totalRevenue?.positive || true,
                icon: 'attach_money',
                class: 'success'
            },
            {
                id: 'totalSales',
                title: 'Total de Vendas',
                value: metrics.totalSales.value.toLocaleString('pt-BR'),
                change: metrics.totalSales.change,
                positive: metrics.totalSales.positive,
                icon: 'trending_up',
                class: 'info'
            },
            {
                id: 'avgTicket',
                title: 'Ticket Médio',
                value: `R$ ${parseFloat(metrics.avgTicket.value).toLocaleString('pt-BR', {minimumFractionDigits: 2})}`,
                change: metrics.avgTicket.change,
                positive: metrics.avgTicket.positive,
                icon: 'receipt',
                class: 'secondary'
            },
            {
                id: 'conversionRate',
                title: 'Taxa de Conversão',
                value: `${parseFloat(metrics.conversionRate.value).toFixed(1)}%`,
                change: metrics.conversionRate.change,
                positive: metrics.conversionRate.positive,
                icon: 'conversion_path',
                class: 'warning'
            }
        ];

        const metricsGrid = document.querySelector('.metrics-grid');
        if (metricsGrid) {
            metricsGrid.innerHTML = metricsData.map(metric => `
                <div class="metric-card ${metric.class}">
                    <div class="metric-header">
                        <span class="metric-title">${metric.title}</span>
                        <div class="metric-icon">
                            <i class="material-icons">${metric.icon}</i>
                        </div>
                    </div>
                    <div class="metric-value">${metric.value}</div>
                    <div class="metric-change ${metric.positive ? 'positive' : 'negative'}">
                        <i class="material-icons">
                            ${metric.positive ? 'trending_up' : 'trending_down'}
                        </i>
                        <span>${metric.change}</span>
                    </div>
                </div>
            `).join('');
        }
    }

    async loadChartData() {
        try {
            // Converter período do formato frontend para dias
            const daysMap = {
                '7d': 7,
                '30d': 30,
                '90d': 90,
                '365d': 365
            };
            const days = daysMap[this.currentPeriod] || 30;
            
            // Carregar dados dos gráficos em paralelo
            const [revenueChart, salesChart, clientsGrowth] = await Promise.all([
                this.apiClient.get(`/dashboard/revenue-chart?days=${days}`),
                this.apiClient.get(`/dashboard/sales-chart?days=${days}`),
                this.apiClient.get(`/dashboard/clients-growth?days=${days}`)
            ]);
            
            return {
                salesChart: {
                    labels: revenueChart.data?.labels || [],
                    data: revenueChart.data?.data || []
                },
                salesCountChart: {
                    labels: salesChart.data?.labels || [],
                    data: salesChart.data?.data || []
                },
                clientsChart: {
                    labels: clientsGrowth.data?.labels || [],
                    data: clientsGrowth.data?.data || []
                }
            };
        } catch (error) {
            console.error('Error loading chart data:', error);
            console.warn('Using mock chart data');
            return this.getMockChartData();
        }
    }

    getMockChartData() {
        return {
            salesChart: {
                labels: ['Jan', 'Feb', 'Mar', 'Apr', 'Mai', 'Jun'],
                data: [12000, 19000, 15000, 25000, 22000, 30000]
            },
            topProducts: [
                { name: 'Construção Residencial', value: 45 },
                { name: 'Reforma Comercial', value: 32 },
                { name: 'Paisagismo', value: 23 }
            ]
        };
    }

    renderCharts(chartData) {
        // Sales chart placeholder
        const salesChart = document.getElementById('salesChart');
        if (salesChart) {
            salesChart.innerHTML = `
                <div class="chart-placeholder">
                    <i class="material-icons" style="font-size: 48px; margin-bottom: 16px;">bar_chart</i>
                    <p>Gráfico de Vendas - ${this.currentPeriod}</p>
                    <small>Dados: ${chartData.salesChart.data.join(', ')}</small>
                </div>
            `;
        }

        // Top products chart
        const productsChart = document.getElementById('productsChart');
        if (productsChart) {
            productsChart.innerHTML = `
                <div class="chart-placeholder">
                    <i class="material-icons" style="font-size: 48px; margin-bottom: 16px;">pie_chart</i>
                    <p>Top Serviços</p>
                    ${chartData.topProducts.map(product => `
                        <div style="margin: 8px 0;">
                            <strong>${product.name}:</strong> ${product.value}%
                        </div>
                    `).join('')}
                </div>
            `;
        }
    }

    async loadRecentClients() {
        try {
            const response = await this.apiClient.get('/dashboard/recent-clients?limit=10');
            if (response.success && response.data && response.data.clients) {
                return response.data.clients;
            }
            throw new Error('Failed to load recent clients');
        } catch (error) {
            console.error('Error loading recent clients:', error);
            console.warn('Using mock clients data');
            return this.getMockClients();
        }
    }

    getMockClients() {
        return [
            { id: 1, nome: 'João Silva', email: 'joao@email.com', telefone: '(11) 99999-9999', created_at: '2024-01-15' },
            { id: 2, nome: 'Maria Santos', email: 'maria@email.com', telefone: '(11) 88888-8888', created_at: '2024-01-14' },
            { id: 3, nome: 'Pedro Costa', email: 'pedro@email.com', telefone: '(11) 77777-7777', created_at: '2024-01-13' },
        ];
    }

    renderRecentClients(clients) {
        const tbody = document.querySelector('#recentClientsTable tbody');
        if (tbody) {
            tbody.innerHTML = clients.map(client => `
                <tr>
                    <td data-label="Nome">${client.nome || 'N/A'}</td>
                    <td data-label="Email">${client.email || 'N/A'}</td>
                    <td data-label="Telefone">${client.telefone || 'N/A'}</td>
                    <td data-label="Cidade">${client.cidade || 'N/A'}</td>
                    <td data-label="Data">${this.formatDate(client.dataCadastro || client.created_at)}</td>
                </tr>
            `).join('');
        }
    }

    async loadRecentSales() {
        try {
            const response = await this.apiClient.get('/dashboard/recent-sales?limit=10');
            if (response.success && response.data && response.data.sales) {
                return response.data.sales;
            }
            throw new Error('Failed to load recent sales');
        } catch (error) {
            console.error('Error loading recent sales:', error);
            console.warn('Using mock sales data');
            return this.getMockSales();
        }
    }

    getMockSales() {
        return [
            { id: 1, cliente: 'João Silva', valor: 15000, status: 'completed', created_at: '2024-01-15' },
            { id: 2, cliente: 'Maria Santos', valor: 8500, status: 'pending', created_at: '2024-01-14' },
            { id: 3, cliente: 'Pedro Costa', valor: 23000, status: 'completed', created_at: '2024-01-13' },
        ];
    }

    renderRecentSales(sales) {
        const tbody = document.querySelector('#recentSalesTable tbody');
        if (tbody) {
            tbody.innerHTML = sales.map(sale => `
                <tr>
                    <td data-label="Cliente">${sale.cliente || 'N/A'}</td>
                    <td data-label="Valor">R$ ${parseFloat(sale.valorTotal || 0).toLocaleString('pt-BR', {minimumFractionDigits: 2})}</td>
                    <td data-label="Status">
                        <span class="status-badge ${sale.status || 'pending'}">
                            ${this.getStatusText(sale.status || 'pending')}
                        </span>
                    </td>
                    <td data-label="Data">${this.formatDate(sale.dataVenda || sale.created_at)}</td>
                    <td data-label="Orçamento">${sale.numeroOrcamento || 'N/A'}</td>
                </tr>
            `).join('');
        }
    }

    handleTabChange(event) {
        const clickedTab = event.target;
        const tabName = clickedTab.dataset.tab;
        
        // Update active tab
        document.querySelectorAll('.nav-tab').forEach(tab => {
            tab.classList.remove('active');
        });
        clickedTab.classList.add('active');
        
        // Show/hide content sections
        document.querySelectorAll('.tab-content').forEach(content => {
            content.style.display = 'none';
        });
        
        const targetContent = document.getElementById(tabName + 'Content');
        if (targetContent) {
            targetContent.style.display = 'block';
        }
    }

    handlePeriodChange(event) {
        const clickedBtn = event.target;
        const period = clickedBtn.dataset.period;
        
        // Update active period
        document.querySelectorAll('.period-btn').forEach(btn => {
            btn.classList.remove('active');
        });
        clickedBtn.classList.add('active');
        
        this.currentPeriod = period;
        this.loadDashboardData();
    }

    async refreshDashboard() {
        const refreshBtn = document.getElementById('refreshDashboard');
        if (refreshBtn) {
            refreshBtn.disabled = true;
            refreshBtn.innerHTML = '<i class="material-icons spinning">refresh</i>';
        }

        await this.loadDashboardData();

        if (refreshBtn) {
            setTimeout(() => {
                refreshBtn.disabled = false;
                refreshBtn.innerHTML = '<i class="material-icons">refresh</i>';
            }, 1000);
        }
    }

    handleLogout() {
        if (confirm('Deseja realmente sair do sistema?')) {
            AuthService.logout();
        }
    }

    navigateToPage(page) {
        // Check if user has permission to access the page
        const user = this.authService.getUser();
        if (!this.canUserAccessPage(user, page)) {
            this.showError('Você não tem permissão para acessar esta página');
            return;
        }

        // Use the global navigation system if available
        if (window.navigateTo) {
            window.navigateTo(page);
        } else {
            // Fallback to hash navigation
            window.location.hash = page;
        }
    }

    canUserAccessPage(user, page) {
        if (!user || !user.perfil) return false;

        const userRole = user.perfil.toLowerCase();
        const permissions = {
            'funcionario': ['clientes'],
            'admin': ['dashboard', 'clientes', 'vendas', 'relatorios'],
            'diretor': ['dashboard', 'clientes', 'vendas', 'usuarios', 'relatorios']
        };

        return permissions[userRole]?.includes(page) || false;
    }

    async exportData() {
        try {
            const response = await this.apiClient.get(`/dashboard/export?period=${this.currentPeriod}`);
            
            // Create and download file
            const blob = new Blob([response.data], { type: 'text/csv' });
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `dashboard_${this.currentPeriod}_${new Date().toISOString().split('T')[0]}.csv`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
            
            this.showSuccess('Dados exportados com sucesso!');
        } catch (error) {
            console.error('Export error:', error);
            this.showError('Erro ao exportar dados');
        }
    }

    startAutoRefresh() {
        // Refresh dashboard every 5 minutes
        this.refreshInterval = setInterval(() => {
            this.loadDashboardData();
        }, 5 * 60 * 1000);
    }

    stopAutoRefresh() {
        if (this.refreshInterval) {
            clearInterval(this.refreshInterval);
            this.refreshInterval = null;
        }
    }

    showLoadingState(loading) {
        const loadingOverlay = document.querySelector('.loading-overlay');
        if (loading) {
            if (!loadingOverlay) {
                const overlay = document.createElement('div');
                overlay.className = 'loading-overlay';
                overlay.innerHTML = `
                    <div class="loading-spinner">
                        <i class="material-icons spinning">refresh</i>
                        <p>Carregando dados...</p>
                    </div>
                `;
                document.querySelector('.dashboard-content').appendChild(overlay);
            }
        } else {
            if (loadingOverlay) {
                loadingOverlay.remove();
            }
        }
    }

    showError(message) {
        this.showNotification(message, 'error');
    }

    showSuccess(message) {
        this.showNotification(message, 'success');
    }

    showNotification(message, type = 'info') {
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.innerHTML = `
            <i class="material-icons">${this.getNotificationIcon(type)}</i>
            <span>${message}</span>
            <button class="notification-close">
                <i class="material-icons">close</i>
            </button>
        `;

        document.body.appendChild(notification);

        // Auto-remove after 5 seconds
        setTimeout(() => {
            notification.remove();
        }, 5000);

        // Close button
        notification.querySelector('.notification-close').addEventListener('click', () => {
            notification.remove();
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

    getStatusText(status) {
        const statusMap = {
            'completed': 'Concluída',
            'pending': 'Pendente',
            'cancelled': 'Cancelada'
        };
        return statusMap[status] || status;
    }

    formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('pt-BR');
    }

    // Utility methods for creating navigation links
    createNavigationLink(page, text, className = '') {
        return `<a href="#${page}" data-route="${page}" class="nav-link ${className}">${text}</a>`;
    }

    createQuickActionButton(page, text, icon) {
        const user = this.authService.getUser();
        if (!this.canUserAccessPage(user, page)) {
            return '';
        }

        return `
            <button class="quick-action-btn" data-route="${page}">
                <i class="material-icons">${icon}</i>
                <span>${text}</span>
            </button>
        `;
    }

    // Method to add quick navigation buttons to dashboard
    addQuickNavigationButtons() {
        const user = this.authService.getUser();
        const quickActionsContainer = document.querySelector('.quick-actions');
        
        if (quickActionsContainer) {
            const quickActions = [
                { page: 'clientes', text: 'Gerenciar Clientes', icon: 'people' },
                { page: 'vendas', text: 'Ver Vendas', icon: 'shopping_cart' },
                { page: 'usuarios', text: 'Usuários', icon: 'admin_panel_settings' },
                { page: 'relatorios', text: 'Relatórios', icon: 'assessment' }
            ];

            const buttonsHtml = quickActions
                .filter(action => this.canUserAccessPage(user, action.page))
                .map(action => this.createQuickActionButton(action.page, action.text, action.icon))
                .join('');

            if (buttonsHtml) {
                quickActionsContainer.innerHTML += buttonsHtml;
                // Re-setup navigation links for new buttons
                this.setupNavigationLinks();
            }
        }
    }

    // Cleanup when leaving dashboard
    destroy() {
        this.stopAutoRefresh();
    }
}

// Initialize dashboard when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    if (document.querySelector('.dashboard-content')) {
        window.dashboardManager = new DashboardManager();
    }
});

// Cleanup when navigating away
window.addEventListener('beforeunload', function() {
    if (window.dashboardManager) {
        window.dashboardManager.destroy();
    }
});