# ==========================================
# Estágio 1: compilação
# ==========================================
FROM maven:3.9.16-eclipse-temurin-25-alpine AS builder

WORKDIR /code

# Mantém as dependências em uma camada reutilizável
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Compila a aplicação no formato Quarkus fast-jar
COPY src ./src
RUN mvn package -DskipTests -B

# ==========================================
# Estágio 2: execução
# ==========================================
FROM eclipse-temurin:25-jre-alpine-3.23

WORKDIR /work

# Usuário e grupo exclusivos, sem privilégios administrativos
RUN addgroup -S appgroup && adduser -S -D -H -G appgroup appuser

# Copia todas as camadas necessárias do Quarkus fast-jar
COPY --from=builder --chown=appuser:appgroup /code/target/quarkus-app/lib/ /work/lib/
COPY --from=builder --chown=appuser:appgroup /code/target/quarkus-app/*.jar /work/
COPY --from=builder --chown=appuser:appgroup /code/target/quarkus-app/app/ /work/app/
COPY --from=builder --chown=appuser:appgroup /code/target/quarkus-app/quarkus/ /work/quarkus/

EXPOSE 8080

USER appuser:appgroup

ENTRYPOINT ["java", "-Dquarkus.http.host=0.0.0.0", "-jar", "/work/quarkus-run.jar"]
