#  MAICONSOFT - SETUP RÁPIDO

##  Pré-requisitos
- Docker Desktop instalado
- Git instalado

##  Setup em 3 Passos

```bash
# 1. Clonar projeto
git clone https://github.com/nisze/MaicoSoft.git
cd MaicoSoft

# 2. Configurar ambiente
cp .env.example .env

# 3. Executar
docker-compose up --build -d
```

##  Credenciais Já Configuradas

###  Banco de Dados (Supabase)
- **URL:** db.hmjldrzvmaqgetjcepay.supabase.co
- **Senha:** `CkTMz5oUISI5gIUn` (já está no .env.example)

###  Email (Gmail)
- **Email:** empresamaiconsoft@gmail.com
- **Senha:** cvjznokkvtzuaqzm
- **Localização:** `application.properties` (já configurado)

##  Acessos

- **Frontend:** http://localhost
- **Backend:** http://localhost:8090  
- **Swagger:** http://localhost:8090/swagger-ui.html

##  Login Padrão

- **Email:** admin@maiconsoft.com
- **Senha:** admin123

##  Problemas?

```bash
# Ver logs
docker-compose logs -f

# Reiniciar tudo
docker-compose down
docker-compose up --build -d
```

---
 **Sistema funciona 100% sem configurações adicionais!**
