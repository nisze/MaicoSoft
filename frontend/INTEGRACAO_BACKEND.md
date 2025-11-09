#  Integrao Frontend + Backend - Maiconsoft

##  **Status Atual**
 Frontend completo com tema construo civil  
 Sistema de navegao SPA implementado  
 Sistema de simulao para desenvolvimento  
 **Precisa integrar com API Spring Boot**  

##  **Como Testar Agora**

### 1. **Iniciar com Docker (Recomendado):**
```bash
cd scripts
start.bat
```
Acesse: `http://localhost:3000`

### 2. **Credenciais de Teste:**
- **Admin**: `ADM001` / `123456`
- **Vendedor**: `W36K0D` / `123456`

### 3. **Navegao Funcional:**
- Login funciona com credenciais acima
- Navegao entre pginas baseada no perfil
- Controle de acesso por tipo de usurio

##  **Arquitetura de Integrao:**

### **Docker Stack (Recomendado):**
```yaml
Services:
  - Frontend: Nginx na porta 3000
  - Backend: Spring Boot na porta 8090
  - Database: PostgreSQL na porta 5432
  - Network: maiconsoft-network (bridge)
```

### **Desenvolvimento Local:**
```bash
# Iniciar Backend
cd maiconsoft_api
mvn spring-boot:run
# Backend estar em localhost:8090

# Servir Frontend (outra porta)
cd frontend
python -m http.server 8000
# Frontend em localhost:8000, APIs em localhost:8090
```

### **Endpoints Implementados:**

| Endpoint | Mtodo | Descrio |
|----------|--------|-----------|
| `/api/auth/login` | POST | Autenticao de usurio |
| `/api/usuarios` | GET/POST/PUT/DELETE | Gesto de usurios |
| `/api/clientes` | GET/POST/PUT/DELETE | Gesto de clientes |
| `/api/vendas` | GET/POST/PUT/DELETE | Gesto de vendas |
| `/api/cupons` | GET/POST/PUT/DELETE | Gesto de cupons |
| `/api/relatorios/**` | GET | Relatrios diversos |

**Base URL:** `http://localhost:8090/api` (desenvolvimento) ou `/api` (Docker/produo)

### 3. **Formato de Resposta do Login:**
```json
{
    "token": "token-here",
    "perfil": "admin", 
    "nome": "Nome do Usurio",
    "email": "email@exemplo.com"
}
```

### **CORS Configuration:**
 J configurado no backend (`application.properties`):
```properties
# CORS configuration
cors.allowed-origins=http://localhost:3000,http://localhost,http://127.0.0.1:5500,http://localhost:5500
cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
cors.allowed-headers=*
cors.allow-credentials=true
```

No Docker, o Nginx proxy encaminha requisies para o backend, eliminando problemas de CORS.

##  **Funcionalidades Prontas**

###  **Sistema de Login:**
- Validao de credenciais
- Redirecionamento baseado em perfil
- Armazenamento de sesso

###  **Dashboard (Admin/Diretor):**
- KPIs e mtricas
- Aes rpidas
- Atividades recentes

###  **Gesto de Clientes:**
- Formulrio completo
- Validao de campos
- Interface responsiva

###  **Pginas Adicionais:**
- Vendas - Controle de vendas
- Usurios - Administrao (Diretor)
- Relatrios - Anlises e reports

##  **Prximos Passos**

1. **Iniciar Stack Completo:**
   ```bash
   cd scripts
   start.bat
   ```

2. **Verificar Logs:**
   ```bash
   cd scripts
   logs.bat
   ```

3. **Acessar Sistema:**
   - Frontend: `http://localhost:3000`
   - Backend API: `http://localhost:8090/api`
   - Banco de Dados: `localhost:5432` (postgres/postgres123)

4. **Testar Integrao:**
   - Login com credenciais
   - CRUD de clientes
   - Navegao entre pginas
   - Upload de fotos de perfil

##  **Monitoramento:**

```bash
# Ver status dos containers
cd scripts
status.bat

# Parar sistema
stop.bat

# Reconstruir containers
rebuild.bat
```

**Sistema est pronto para usar! **