# ===============================
# MAICONSOFT API - DOCKERFILE
# ===============================

FROM maven:3.9-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Copiar pom.xml e baixar dependências (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fonte e compilar
COPY src ./src
RUN mvn clean package -DskipTests

# ===============================
# RUNTIME IMAGE
# ===============================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Instalar wget para healthcheck
RUN apk add --no-cache wget

# Copiar JAR compilado
COPY --from=build /app/target/*.jar app.jar

# Criar diretórios necessários
RUN mkdir -p /app/uploads/profiles /app/logs

# Expor porta
EXPOSE 8090

# Executar aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
