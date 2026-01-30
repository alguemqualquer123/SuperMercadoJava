# ========================================
# Multi-stage Dockerfile para SuperMercado PDV
# ========================================

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copiar arquivos de configuração Maven
COPY pom.xml .
COPY src ./src

# Build da aplicação
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Instalar dependências para JavaFX (se necessário para headless)
RUN apk add --no-cache \
    fontconfig \
    ttf-dejavu

# Copiar o JAR da aplicação do stage de build
COPY --from=builder /app/target/*.jar app.jar

# Expor porta da aplicação
EXPOSE 8080

# Variáveis de ambiente padrão
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Executar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
