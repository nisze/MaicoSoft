# ===============================
# MAICONSOFT API - DOCKERFILE
# ===============================
FROM openjdk:21-jdk-slim as build

# Instalar Maven
RUN apt-get update && apt-get install -y maven

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivos do Maven primeiro (para cache de dependências)
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# Baixar dependências (será cacheado se pom.xml não mudar)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Construir aplicação
RUN mvn clean package -DskipTests

# ===============================
# RUNTIME STAGE
# ===============================
FROM openjdk:21-jdk-slim

# Instalar dependências necessárias
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Criar usuário não-root para segurança
RUN useradd -r -s /bin/false maiconsoft

# Definir diretório de trabalho
WORKDIR /app

# Criar diretórios necessários
RUN mkdir -p uploads/profiles logs && \
    chown -R maiconsoft:maiconsoft /app

# Copiar JAR da aplicação
COPY --from=build /app/target/*.jar app.jar

# Configurar permissões
RUN chown maiconsoft:maiconsoft app.jar

# Mudar para usuário não-root
USER maiconsoft

# Configurar JVM para container
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Expor porta
EXPOSE 8090

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8090/api/health || exit 1

# Comando de inicialização
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]