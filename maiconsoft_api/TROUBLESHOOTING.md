# 🔧 Troubleshooting - Maiconsoft

## 🚨 **Problemas Mais Comuns e Soluções**

### ❌ **"Não consigo rodar em outras máquinas"**

#### 🔍 **DIAGNÓSTICO AUTOMÁTICO**
```bash
# Execute primeiro para identificar o problema:
check-system.bat    # Windows
chmod +x check-system.sh && ./check-system.sh  # Linux/Mac
```

#### ✅ **SOLUÇÕES POR PROBLEMA:**

---

### 1️⃣ **Java não encontrado**
```bash
# Sintomas:
- "java: command not found"
- "Java not found"

# Solução:
1. Baixe Java 17+: https://adoptium.net/
2. Instale e reinicie o terminal
3. Teste: java -version
```

### 2️⃣ **Porta 8090 em uso**
```bash
# Sintomas:
- "Port 8090 already in use"
- "Address already in use"

# Solução Windows:
netstat -ano | findstr :8090
taskkill /PID [NUMERO_DO_PID] /F

# Solução Linux/Mac:
lsof -i :8090
kill -9 [NUMERO_DO_PID]
```

### 3️⃣ **Supabase inacessível**
```bash
# Sintomas:
- "Could not connect to database"
- "Connection timeout"
- "Network unreachable"

# Solução 1 - Verificar Internet:
ping db.hmjldrzvmaqgetjcepay.supabase.co

# Solução 2 - Modo Offline:
run-offline.bat    # Windows  
./run-offline.sh   # Linux/Mac

# Solução 3 - PostgreSQL Local:
# Edite application.properties:
spring.profiles.active=postgres
```

### 4️⃣ **Erro de compilação**
```bash
# Sintomas:
- "BUILD FAILURE"
- "Cannot resolve dependencies"
- "Compilation error"

# Solução:
mvn clean compile
# Se falhar:
rm -rf target/
mvn clean install -DskipTests
```

### 5️⃣ **Problemas de encoding (Windows)**
```bash
# Sintomas:
- Caracteres estranhos no terminal
- "Encoding error"

# Solução:
chcp 65001    # UTF-8
set JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8
```

---

## 🎯 **Soluções por Cenário**

### 📱 **Máquina Nova (Sem nada instalado)**
```bash
1. Instale Java 17+: https://adoptium.net/
2. Clone o projeto: git clone https://github.com/nisze/MaicoSoft.git
3. Execute: cd maiconsoft_api && run-with-supabase.bat
```

### 🌐 **Sem Internet**
```bash
1. Execute: run-offline.bat
2. Acesse: http://localhost:8090/h2-console
3. Use dados temporários (perdidos ao reiniciar)
```

### 🏢 **Rede Corporativa (Firewall)**
```bash
# Se Supabase estiver bloqueado:
1. Use modo offline: run-offline.bat
2. Ou configure PostgreSQL local
3. Ou peça liberação de: db.hmjldrzvmaqgetjcepay.supabase.co:5432
```

### 🔄 **Máquina de Desenvolvimento**
```bash
# Para desenvolvimento contínuo:
1. Use IDE (VS Code, IntelliJ)
2. Profile recomendado: supabase
3. Para debug: spring.profiles.active=dev
```

---

## 🆘 **Comandos de Emergência**

### 🔥 **Reset Completo**
```bash
# Limpar tudo e recomeçar:
mvn clean
rm -rf target/
rm -rf ~/.m2/repository/com/faculdae
git pull origin main
run-with-supabase.bat
```

### 🔍 **Verificar Logs**
```bash
# Ver logs da aplicação:
tail -f logs/maiconsoft-api.log

# Ver logs do Maven:
mvn spring-boot:run -X

# Verificar health:
curl http://localhost:8090/actuator/health
```

### ⚡ **Testes Rápidos**
```bash
# Testar API:
curl http://localhost:8090/actuator/health

# Testar Swagger:
# Abra: http://localhost:8090/swagger-ui.html

# Testar H2 Console (modo offline):
# Abra: http://localhost:8090/h2-console
```

---

## 📞 **Última Instância**

### 🐛 **Se NADA funcionar:**

1. **Capture informações:**
```bash
java -version
mvn -version
check-system.bat > diagnostico.txt
```

2. **Abra issue no GitHub:**
- Anexe o arquivo `diagnostico.txt`
- Descreva o erro exato
- Inclua sistema operacional

3. **Contato direto:**
- Email: contato@maiconsoft.com.br
- GitHub Issues: https://github.com/nisze/MaicoSoft/issues

---

## 📋 **Checklist de Verificação**

### ✅ **Antes de pedir ajuda:**
- [ ] Executei `check-system.bat`?
- [ ] Testei modo offline?
- [ ] Java 17+ instalado?
- [ ] Internet funcionando?
- [ ] Porta 8090 livre?
- [ ] Logs verificados?

### ✅ **Informações para suporte:**
- [ ] Sistema operacional
- [ ] Versão do Java
- [ ] Output do `check-system`
- [ ] Logs de erro
- [ ] Passos que levaram ao erro

---

**💡 Dica:** 90% dos problemas são resolvidos com Java correto + Internet + Porta livre!