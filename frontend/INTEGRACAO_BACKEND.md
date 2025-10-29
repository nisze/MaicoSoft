# 🏗️ Integração Frontend + Backend - Maiconsoft

## 📋 **Status Atual**
✅ Frontend completo com tema construção civil  
✅ Sistema de navegação SPA implementado  
✅ Sistema de simulação para desenvolvimento  
⚠️ **Precisa integrar com API Spring Boot**  

## 🚀 **Como Testar Agora (Modo Simulação)**

### 1. **Credenciais de Teste:**
- **Admin**: `admin` / `123`
- **Diretor**: `diretor` / `123` 
- **Funcionário**: `funcionario` / `123`

### 2. **Abrir o Sistema:**
```bash
start index.html
```

### 3. **Navegação Funcional:**
- Login funciona com credenciais acima
- Navegação entre páginas baseada no perfil
- Controle de acesso por tipo de usuário

## 🔧 **Para Integrar com seu Backend Spring Boot:**

### 1. **Inicie o Backend:**
```bash
cd maiconsoft_api
mvn spring-boot:run
```

### 2. **Endpoints Necessários na API:**
```java
// AuthController.java
@PostMapping("/auth/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request)

@GetMapping("/auth/test") // Teste de conectividade
public ResponseEntity<?> test()

// ClienteController.java - já existe
// VendaController.java - já existe 
// UserController.java - já existe
```

### 3. **Formato de Resposta do Login:**
```json
{
    "token": "jwt-token-here",
    "perfil": "admin|diretor|funcionario", 
    "nome": "Nome do Usuário",
    "email": "email@exemplo.com"
}
```

### 4. **CORS Configuration:**
```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
```

## 🎯 **Funcionalidades Prontas**

### ✅ **Sistema de Login:**
- Validação de credenciais
- Redirecionamento baseado em perfil
- Armazenamento de sessão

### ✅ **Dashboard (Admin/Diretor):**
- KPIs e métricas
- Ações rápidas
- Atividades recentes

### ✅ **Gestão de Clientes:**
- Formulário completo
- Validação de campos
- Interface responsiva

### ✅ **Páginas Adicionais:**
- Vendas - Controle de vendas
- Usuários - Administração (Diretor)
- Relatórios - Análises e reports

## 🔄 **Próximos Passos**

1. **Testar com Simulação** (funciona agora)
2. **Iniciar Backend Spring Boot**
3. **Ajustar endpoints se necessário**
4. **Desabilitar simulação em produção**

## 📞 **Links Diretos para Teste:**
- [Login](file:///C:/Users/denise.a.de.oliveira/Documents/faculdade/front-back/maiconsoft_api/frontend/index.html)
- [Dashboard](file:///C:/Users/denise.a.de.oliveira/Documents/faculdade/front-back/maiconsoft_api/frontend/index.html#dashboard)
- [Clientes](file:///C:/Users/denise.a.de.oliveira/Documents/faculdade/front-back/maiconsoft_api/frontend/index.html#cliente)

**Sistema está pronto para usar! 🚀**