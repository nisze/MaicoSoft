/**
 * Maiconsoft - Extensões Responsivas para Cliente Manager
 * Adiciona funcionalidades AJAX modernas e responsividade
 */

// Extensões para o ClienteManager existente
if (typeof ClienteManager !== 'undefined') {
    
    // Adicionar métodos responsivos ao prototype
    ClienteManager.prototype.initResponsive = function() {
        this.isMobile = window.innerWidth < 768;
        this.setupResponsiveListeners();
        this.setupAjaxLoading();
        this.adaptToScreenSize();
    };

    ClienteManager.prototype.setupResponsiveListeners = function() {
        // Listener para mudanças de orientação/tamanho
        window.addEventListener('resize', this.debounce(() => {
            var wasMobile = this.isMobile;
            this.isMobile = window.innerWidth < 768;
            
            if (wasMobile !== this.isMobile) {
                this.adaptToScreenSize();
            }
        }, 250));
    };

    ClienteManager.prototype.setupAjaxLoading = function() {
        // Interceptar métodos existentes para adicionar loading
        var originalLoadClients = this.loadClients;
        this.loadClients = function() {
            this.showLoading();
            return originalLoadClients.call(this).finally(() => {
                this.hideLoading();
            });
        };
    };

    ClienteManager.prototype.adaptToScreenSize = function() {
        var table = document.querySelector('.cliente-table');
        if (table) {
            if (this.isMobile) {
                this.convertTableToCards();
            } else {
                // Recarregar em formato tabela se necessário
                this.loadClients();
            }
        }
    };

    ClienteManager.prototype.convertTableToCards = function() {
        var table = document.querySelector('.cliente-table');
        if (!table) return;

        var rows = table.querySelectorAll('tbody tr');
        var cardsHtml = '<div class="clientes-cards">';
        
        rows.forEach(function(row) {
            var cells = row.cells;
            var clienteId = row.dataset.id;
            
            cardsHtml += `
                <div class="cliente-card" data-id="${clienteId}">
                    <div class="card-header">
                        <h3 class="card-title">${cells[1].textContent}</h3>
                        <span class="client-type">${cells[2].textContent}</span>
                    </div>
                    <div class="card-body">
                        <div class="client-info">
                            <div class="info-item">
                                <strong>Código:</strong> ${cells[0].textContent}
                            </div>
                            <div class="info-item">
                                <strong>CPF/CNPJ:</strong> ${cells[3].textContent}
                            </div>
                            <div class="info-item">
                                <strong>Email:</strong> ${cells[4].textContent}
                            </div>
                            <div class="info-item">
                                <strong>Telefone:</strong> ${cells[5].textContent}
                            </div>
                            <div class="info-item">
                                <strong>Cidade:</strong> ${cells[6].textContent}
                            </div>
                        </div>
                    </div>
                    <div class="card-actions">
                        <button class="btn btn-primary btn-edit" data-id="${clienteId}">
                            <span class="material-icons">edit</span>
                            Editar
                        </button>
                        <button class="btn btn-outline btn-delete" data-id="${clienteId}">
                            <span class="material-icons">delete</span>
                            Excluir
                        </button>
                    </div>
                </div>
            `;
        });
        
        cardsHtml += '</div>';
        
        var container = table.parentElement;
        container.innerHTML = cardsHtml;
    };

    ClienteManager.prototype.showLoading = function() {
        if (window.ajaxUtils) {
            window.ajaxUtils.showLoading();
        } else {
            // Fallback simples
            var loader = document.getElementById('simple-loader');
            if (!loader) {
                loader = document.createElement('div');
                loader.id = 'simple-loader';
                loader.innerHTML = '<div style="text-align: center; padding: 2rem;">Carregando...</div>';
                document.body.appendChild(loader);
            }
            loader.style.display = 'block';
        }
    };

    ClienteManager.prototype.hideLoading = function() {
        if (window.ajaxUtils) {
            window.ajaxUtils.hideLoading();
        } else {
            var loader = document.getElementById('simple-loader');
            if (loader) {
                loader.style.display = 'none';
            }
        }
    };

    ClienteManager.prototype.debounce = function(func, wait) {
        var timeout;
        return function executedFunction() {
            var args = arguments;
            var later = function() {
                clearTimeout(timeout);
                func.apply(this, args);
            }.bind(this);
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    };

    ClienteManager.prototype.modernAjaxRequest = function(url, options) {
        options = options || {};
        
        return new Promise((resolve, reject) => {
            var xhr = new XMLHttpRequest();
            xhr.open(options.method || 'GET', url);
            
            // Headers
            xhr.setRequestHeader('Content-Type', 'application/json');
            xhr.setRequestHeader('Accept', 'application/json');
            
            // Token se disponível
            var token = localStorage.getItem('authToken');
            if (token) {
                xhr.setRequestHeader('Authorization', 'Bearer ' + token);
            }

            xhr.onload = function() {
                if (xhr.status >= 200 && xhr.status < 300) {
                    try {
                        var response = JSON.parse(xhr.responseText);
                        resolve(response);
                    } catch (e) {
                        resolve(xhr.responseText);
                    }
                } else {
                    reject(new Error('HTTP ' + xhr.status + ': ' + xhr.statusText));
                }
            };

            xhr.onerror = function() {
                reject(new Error('Network error'));
            };

            xhr.ontimeout = function() {
                reject(new Error('Request timeout'));
            };

            xhr.timeout = options.timeout || 10000;

            if (options.body) {
                xhr.send(JSON.stringify(options.body));
            } else {
                xhr.send();
            }
        });
    };

    ClienteManager.prototype.notify = function(message, type, duration) {
        if (window.ajax && window.ajax.notify) {
            window.ajax.notify(message, type, duration);
        } else {
            // Fallback simples
            console.log(`[${type}] ${message}`);
            alert(message);
        }
    };

    // Método para carregar clientes com AJAX moderno
    ClienteManager.prototype.loadClientesModern = function(page) {
        page = page || 1;
        this.currentPage = page;
        
        var params = new URLSearchParams({
            page: page - 1,
            size: this.pageSize
        });
        
        if (this.currentSearch) {
            params.append('search', this.currentSearch);
        }

        var url = API_CONFIG.baseURL + '/clientes?' + params.toString();
        
        this.showLoading();
        
        this.modernAjaxRequest(url)
            .then((response) => {
                this.renderClientesModern(response);
                this.notify('Clientes carregados com sucesso!', 'success', 2000);
            })
            .catch((error) => {
                console.error('Erro ao carregar clientes:', error);
                this.notify('Erro ao carregar clientes', 'error');
                this.renderClientesMock(); // Fallback para dados mock
            })
            .finally(() => {
                this.hideLoading();
            });
    };

    ClienteManager.prototype.renderClientesModern = function(data) {
        var container = document.querySelector('#clientes-list');
        if (!container) return;

        var clientes = data.content || data.clientes || [];
        
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
    };

    ClienteManager.prototype.renderClientesCards = function(clientes) {
        var cardsHtml = '<div class="clientes-cards">';
        
        clientes.forEach(function(cliente) {
            cardsHtml += `
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
                                ${cliente.cpfCnpj}
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
            `;
        });
        
        cardsHtml += '</div>';
        return cardsHtml;
    };

    ClienteManager.prototype.renderClientesTable = function(clientes) {
        var tableHtml = `
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
        `;
        
        clientes.forEach(function(cliente) {
            tableHtml += `
                <tr data-id="${cliente.idCliente}">
                    <td>${cliente.codigo}</td>
                    <td>${cliente.razaoSocial}</td>
                    <td>
                        <span class="client-type ${cliente.tipo === 'F' ? 'pf' : 'pj'}">
                            ${cliente.tipo === 'F' ? 'Pessoa Física' : 'Pessoa Jurídica'}
                        </span>
                    </td>
                    <td>${cliente.cpfCnpj}</td>
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
            `;
        });
        
        tableHtml += `
                    </tbody>
                </table>
            </div>
        `;
        
        return tableHtml;
    };

    ClienteManager.prototype.renderEmptyState = function() {
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
    };

    // Inicializar responsividade quando o ClienteManager for criado
    var originalInit = ClienteManager.prototype.init;
    ClienteManager.prototype.init = function() {
        originalInit.call(this);
        this.initResponsive();
    };

    console.log('✅ Extensões responsivas para ClienteManager carregadas');
}

// CSS adicional para os cards responsivos
var responsiveStyles = `
<style>
.clientes-cards {
    display: grid;
    gap: 1rem;
    grid-template-columns: 1fr;
}

@media (min-width: 768px) {
    .clientes-cards {
        grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    }
}

.cliente-card {
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    padding: 1rem;
    border: 1px solid #eee;
    transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.cliente-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.cliente-card .card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 1rem;
    padding-bottom: 0.5rem;
    border-bottom: 1px solid #eee;
}

.cliente-card .card-title {
    font-size: 1.1rem;
    font-weight: 600;
    margin: 0;
    color: #333;
    flex: 1;
    margin-right: 1rem;
}

.client-type {
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
    font-size: 0.8rem;
    font-weight: 500;
    white-space: nowrap;
}

.client-type.pf {
    background: #e3f2fd;
    color: #1976d2;
}

.client-type.pj {
    background: #f3e5f5;
    color: #7b1fa2;
}

.client-info {
    margin-bottom: 1rem;
}

.info-item {
    margin-bottom: 0.5rem;
    font-size: 0.9rem;
}

.info-item strong {
    display: inline-block;
    min-width: 80px;
    color: #555;
}

.card-actions {
    display: flex;
    gap: 0.5rem;
    justify-content: flex-end;
}

.empty-state {
    text-align: center;
    padding: 3rem 1rem;
    color: #666;
}

.empty-state .empty-icon {
    font-size: 4rem;
    color: #ccc;
    margin-bottom: 1rem;
}

.empty-state .material-icons {
    font-size: inherit;
}

.empty-state h3 {
    margin: 0 0 0.5rem 0;
    color: #333;
}

.empty-state p {
    margin: 0 0 1.5rem 0;
    max-width: 400px;
    margin-left: auto;
    margin-right: auto;
}
</style>
`;

// Adicionar estilos ao head
if (!document.querySelector('#responsive-styles')) {
    var styleElement = document.createElement('div');
    styleElement.id = 'responsive-styles';
    styleElement.innerHTML = responsiveStyles;
    document.head.appendChild(styleElement.firstElementChild);
}