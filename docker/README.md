# Maiconsoft - Docker PostgreSQL

##  Como usar

### 1. Iniciar o banco de dados
\\\ash
docker-compose up -d
\\\

### 2. Verificar status
\\\ash
docker-compose ps
\\\

### 3. Parar o banco
\\\ash
docker-compose down
\\\

### 4. Parar e remover volumes (limpar dados)
\\\ash
docker-compose down -v
\\\

##  Configurações

- **Host**: localhost
- **Porta**: 5432
- **Database**: maiconsoft
- **User**: postgres
- **Password**: postgres123

##  Logs

Ver logs do PostgreSQL:
\\\ash
docker-compose logs -f postgres
\\\

##  Executar Backend

Após iniciar o Docker:

1. Na pasta \maiconsoft_api\:
   \\\ash
   mvn spring-boot:run
   \\\

2. Ou use sua IDE favorita (IntelliJ, Eclipse, VS Code)

O backend irá conectar automaticamente no PostgreSQL do Docker.
