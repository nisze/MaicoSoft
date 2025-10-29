/**
 * Maiconsoft - Utilitários AJAX Modernos
 * Sistema AJAX responsivo com loading, retry e cache
 */

class AjaxUtils {
    constructor() {
        this.cache = new Map();
        this.pendingRequests = new Map();
        this.retryConfig = {
            maxRetries: 3,
            retryDelay: 1000,
            backoffMultiplier: 2
        };
    }

    /**
     * Executa requisição AJAX com retry automático e cache
     */
    async request(url, options = {}) {
        const config = {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            cache: options.cache !== false,
            showLoading: options.showLoading !== false,
            retry: options.retry !== false,
            timeout: options.timeout || 10000,
            ...options
        };

        // Verificar cache se habilitado
        if (config.cache && config.method === 'GET') {
            const cached = this.getFromCache(url);
            if (cached) {
                console.log('📦 Cache hit:', url);
                return cached;
            }
        }

        // Verificar se já existe requisição pendente
        const requestKey = `${config.method}:${url}`;
        if (this.pendingRequests.has(requestKey)) {
            console.log('⏳ Aguardando requisição pendente:', url);
            return this.pendingRequests.get(requestKey);
        }

        // Mostrar loading se habilitado
        if (config.showLoading) {
            this.showLoading();
        }

        // Criar promise da requisição
        const requestPromise = this.executeRequest(url, config);
        this.pendingRequests.set(requestKey, requestPromise);

        try {
            const result = await requestPromise;
            
            // Salvar no cache se habilitado
            if (config.cache && config.method === 'GET') {
                this.saveToCache(url, result);
            }

            return result;
        } catch (error) {
            console.error('❌ Erro na requisição:', error);
            throw error;
        } finally {
            // Limpar requisição pendente
            this.pendingRequests.delete(requestKey);
            
            // Esconder loading se habilitado
            if (config.showLoading) {
                this.hideLoading();
            }
        }
    }

    /**
     * Executa a requisição com retry automático
     */
    async executeRequest(url, config, attempt = 1) {
        try {
            const controller = new AbortController();
            const timeoutId = setTimeout(() => controller.abort(), config.timeout);

            const response = await fetch(url, {
                ...config,
                signal: controller.signal
            });

            clearTimeout(timeoutId);

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            } else {
                return await response.text();
            }

        } catch (error) {
            // Retry se habilitado e não é o último attempt
            if (config.retry && attempt < this.retryConfig.maxRetries) {
                const delay = this.retryConfig.retryDelay * Math.pow(this.retryConfig.backoffMultiplier, attempt - 1);
                console.log(`🔄 Retry ${attempt}/${this.retryConfig.maxRetries} em ${delay}ms:`, url);
                
                await this.sleep(delay);
                return this.executeRequest(url, config, attempt + 1);
            }

            throw error;
        }
    }

    /**
     * Métodos de conveniência para diferentes tipos de requisição
     */
    async get(url, options = {}) {
        return this.request(url, { ...options, method: 'GET' });
    }

    async post(url, data, options = {}) {
        return this.request(url, {
            ...options,
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    async put(url, data, options = {}) {
        return this.request(url, {
            ...options,
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    async delete(url, options = {}) {
        return this.request(url, { ...options, method: 'DELETE' });
    }

    /**
     * Sistema de cache simples
     */
    getFromCache(key) {
        const cached = this.cache.get(key);
        if (!cached) return null;

        const { data, timestamp, ttl } = cached;
        if (Date.now() - timestamp > ttl) {
            this.cache.delete(key);
            return null;
        }

        return data;
    }

    saveToCache(key, data, ttl = 300000) { // 5 minutos por padrão
        this.cache.set(key, {
            data,
            timestamp: Date.now(),
            ttl
        });
    }

    clearCache() {
        this.cache.clear();
        console.log('🗑️ Cache limpo');
    }

    /**
     * Sistema de loading responsivo
     */
    showLoading() {
        let loader = document.getElementById('ajax-loader');
        if (!loader) {
            loader = this.createLoader();
            document.body.appendChild(loader);
        }
        loader.style.display = 'flex';
        
        // Adicionar classe ao body para indicar loading
        document.body.classList.add('ajax-loading');
    }

    hideLoading() {
        const loader = document.getElementById('ajax-loader');
        if (loader) {
            loader.style.display = 'none';
        }
        
        document.body.classList.remove('ajax-loading');
    }

    createLoader() {
        const loader = document.createElement('div');
        loader.id = 'ajax-loader';
        loader.innerHTML = `
            <div class="ajax-loader-backdrop">
                <div class="ajax-loader-content">
                    <div class="ajax-spinner"></div>
                    <div class="ajax-loader-text">Carregando...</div>
                </div>
            </div>
        `;
        
        // Estilos inline para garantir funcionamento
        loader.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            z-index: 9999;
            display: none;
            align-items: center;
            justify-content: center;
        `;

        const style = document.createElement('style');
        style.textContent = `
            .ajax-loader-backdrop {
                background: rgba(0, 0, 0, 0.3);
                backdrop-filter: blur(2px);
                width: 100%;
                height: 100%;
                display: flex;
                align-items: center;
                justify-content: center;
            }
            
            .ajax-loader-content {
                background: white;
                border-radius: 12px;
                padding: 2rem;
                box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
                display: flex;
                flex-direction: column;
                align-items: center;
                gap: 1rem;
                min-width: 200px;
            }
            
            .ajax-spinner {
                width: 40px;
                height: 40px;
                border: 4px solid #f3f3f3;
                border-top: 4px solid #1976d2;
                border-radius: 50%;
                animation: spin 1s linear infinite;
            }
            
            .ajax-loader-text {
                color: #333;
                font-size: 16px;
                font-weight: 500;
            }
            
            @keyframes spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
            }
            
            .ajax-loading {
                overflow: hidden;
            }
            
            /* Responsividade */
            @media (max-width: 768px) {
                .ajax-loader-content {
                    margin: 1rem;
                    padding: 1.5rem;
                    min-width: unset;
                    max-width: 300px;
                }
                
                .ajax-spinner {
                    width: 32px;
                    height: 32px;
                    border-width: 3px;
                }
                
                .ajax-loader-text {
                    font-size: 14px;
                }
            }
        `;
        
        document.head.appendChild(style);
        return loader;
    }

    /**
     * Sistema de notificações para feedback
     */
    showNotification(message, type = 'info', duration = 3000) {
        const notification = document.createElement('div');
        notification.className = `ajax-notification ajax-notification-${type}`;
        notification.textContent = message;
        
        // Estilos inline
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: ${this.getNotificationColor(type)};
            color: white;
            padding: 1rem 1.5rem;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
            z-index: 10000;
            animation: slideInRight 0.3s ease-out;
            max-width: 300px;
            word-wrap: break-word;
        `;

        // Adicionar animação
        if (!document.querySelector('style[data-ajax-notifications]')) {
            const style = document.createElement('style');
            style.setAttribute('data-ajax-notifications', 'true');
            style.textContent = `
                @keyframes slideInRight {
                    from {
                        transform: translateX(100%);
                        opacity: 0;
                    }
                    to {
                        transform: translateX(0);
                        opacity: 1;
                    }
                }
                
                @keyframes slideOutRight {
                    from {
                        transform: translateX(0);
                        opacity: 1;
                    }
                    to {
                        transform: translateX(100%);
                        opacity: 0;
                    }
                }
                
                @media (max-width: 768px) {
                    .ajax-notification {
                        top: 10px !important;
                        right: 10px !important;
                        left: 10px !important;
                        max-width: none !important;
                    }
                }
            `;
            document.head.appendChild(style);
        }

        document.body.appendChild(notification);

        // Auto-remover após duração especificada
        setTimeout(() => {
            notification.style.animation = 'slideOutRight 0.3s ease-in';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.parentNode.removeChild(notification);
                }
            }, 300);
        }, duration);

        return notification;
    }

    getNotificationColor(type) {
        const colors = {
            success: '#4caf50',
            error: '#f44336',
            warning: '#ff9800',
            info: '#2196f3'
        };
        return colors[type] || colors.info;
    }

    /**
     * Utilitários
     */
    sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    /**
     * Validação de conectividade
     */
    async checkConnectivity() {
        try {
            await fetch('/', { 
                method: 'HEAD',
                mode: 'no-cors',
                cache: 'no-cache'
            });
            return true;
        } catch {
            return false;
        }
    }
}

// Instância global
window.ajaxUtils = new AjaxUtils();

// Métodos de conveniência globais
window.ajax = {
    get: (url, options) => window.ajaxUtils.get(url, options),
    post: (url, data, options) => window.ajaxUtils.post(url, data, options),
    put: (url, data, options) => window.ajaxUtils.put(url, data, options),
    delete: (url, options) => window.ajaxUtils.delete(url, options),
    notify: (message, type, duration) => window.ajaxUtils.showNotification(message, type, duration),
    clearCache: () => window.ajaxUtils.clearCache()
};

console.log('✅ Ajax Utils carregado com sucesso');