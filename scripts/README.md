# Scripts Docker - Maiconsoft

Scripts utilitarios para gerenciar os containers Docker.

## Scripts Disponiveis

### start.bat
Inicia todos os containers (PostgreSQL + Backend + Frontend)

\\\ash
.\start.bat
\\\

### stop.bat
Para todos os containers

\\\ash
.\stop.bat
\\\

### logs.bat
Exibe logs em tempo real de todos os containers

\\\ash
.\logs.bat
\\\

### status.bat
Mostra status e uso de recursos dos containers

\\\ash
.\status.bat
\\\

### rebuild.bat
Rebuild dos containers apos mudancas no codigo

\\\ash
.\rebuild.bat
\\\

### reset.bat
Reset completo - para, remove volumes, limpa cache e rebuild
**ATENCAO: Apaga dados do banco!**

\\\ash
.\reset.bat
\\\

## Uso Rapido

1. **Primeira vez:**
   \\\ash
   .\start.bat
   \\\
   Aguarde 3-5 minutos (compilacao Maven)

2. **Ver se esta rodando:**
   \\\ash
   .\status.bat
   \\\

3. **Ver logs:**
   \\\ash
   .\logs.bat
   \\\

4. **Parar:**
   \\\ash
   .\stop.bat
   \\\

## Acesso

Apos iniciar:
- Frontend: http://localhost:3000
- Backend: http://localhost:8090/api
- PostgreSQL: localhost:5432

## Credenciais

### Banco
- User: postgres
- Password: postgres123
- Database: maiconsoft

### Aplicacao
- Admin: ADM001 / 123456
- Usuario: W36K0D / 123456
