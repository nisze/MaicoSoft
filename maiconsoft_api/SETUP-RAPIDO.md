# 🚀 Setup Rápido - Maiconsoft Backend

## ⚡ **Execução em 3 Passos (Máquinas Novas)**

### 1️⃣ **Pré-requisitos**
```bash
# Verifique se Java 17+ está instalado
java -version

# Se não estiver, baixe: https://adoptium.net/
```

### 2️⃣ **Clone e Execute**
```bash
# Windows
git clone https://github.com/nisze/MaicoSoft.git
cd MaicoSoft/maiconsoft_api
run-with-supabase.bat

# Linux/Mac
git clone https://github.com/nisze/MaicoSoft.git
cd MaicoSoft/maiconsoft_api
chmod +x run-with-supabase.sh
./run-with-supabase.sh
```

### 3️⃣ **Teste**
```bash
# API estará em: http://localhost:8090
# Swagger: http://localhost:8090/swagger-ui.html
```

---

## 🔧 **Soluções para Problemas Comuns**

### ❌ **Erro: "Port 8090 already in use"**
```bash
# Windows
netstat -ano | findstr :8090
taskkill /PID [PID_NUMBER] /F

# Linux/Mac
lsof -i :8090
kill -9 [PID_NUMBER]
```

### ❌ **Erro: "Could not connect to database"**
1. Verifique internet (projeto usa Supabase na nuvem)
2. Teste conexão: `ping db.hmjldrzvmaqgetjcepay.supabase.co`
3. Se offline, use modo local (ver abaixo)

### ❌ **Erro: "Java not found"**
```bash
# Baixe Java 17+ de: https://adoptium.net/
# Ou use SDKMAN (Linux/Mac):
curl -s "https://get.sdkman.io" | bash
sdk install java 17.0.8-tem
```

---

## 🏠 **Modo Offline (Sem Internet)**

### Usando H2 Database (Temporário)
```bash
# Edite application.properties e altere:
spring.profiles.active=dev

# Execute:
mvn spring-boot:run
```

### Usando PostgreSQL Local
```bash
# 1. Instale PostgreSQL
# 2. Crie banco: CREATE DATABASE maiconsoft_dev;
# 3. Edite application.properties:
spring.profiles.active=postgres

# 4. Execute:
mvn spring-boot:run
```

---

## 📋 **Variáveis de Ambiente (Opcional)**

Para configurações avançadas, crie arquivo `.env`:
```env
SUPABASE_PASSWORD=CkTMz5oUISI5gIUn
SERVER_PORT=8090
SPRING_PROFILES_ACTIVE=supabase
```

---

## 🌐 **URLs Importantes**

| Serviço | URL | Descrição |
|---------|-----|-----------|
| API | http://localhost:8090 | API principal |
| Swagger | http://localhost:8090/swagger-ui.html | Documentação interativa |
| Actuator | http://localhost:8090/actuator/health | Health check |

---

## 📞 **Suporte Rápido**

### Logs da Aplicação
```bash
# Logs estão em: ./logs/maiconsoft-api.log
# Para debug em tempo real:
tail -f logs/maiconsoft-api.log
```

### Teste de API
```bash
# Teste simples com curl:
curl http://localhost:8090/actuator/health

# Resposta esperada:
# {"status":"UP"}
```

### Reset Completo
```bash
# Se tudo der errado, reset completo:
mvn clean
rm -rf target/
./mvnw spring-boot:run -Dspring-boot.run.profiles=supabase
```

---

## 🔥 **Setup para Desenvolvimento**

### VS Code
```bash
# Extensões recomendadas:
# - Extension Pack for Java
# - Spring Boot Extension Pack
# - REST Client
```

### IntelliJ IDEA
```bash
# Abra como projeto Maven
# Profile ativo: supabase
# Build automaticamente: Enable
```

---

**✅ Se seguiu tudo e ainda tem problemas, abra uma issue no GitHub!**