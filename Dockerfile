# ==========================================
# ETAPA 1: Compilação (Build)
# ==========================================
# Usamos a imagem do Maven com JDK 25 para compilar a aplicação
FROM maven:3.9.16-eclipse-temurin-25-alpine AS build

# Define o diretório de trabalho dentro do container de build
WORKDIR /code

# Copia os arquivos de configuração do Maven primeiro (melhora o cache do Docker)
COPY mvnw /code/mvnw
COPY .mvn /code/.mvn
COPY pom.xml /code/pom.xml

# Baixa as dependências (evita baixar tudo de novo se o pom.xml não mudou)
RUN mvn dependency:go-offline -B

# Copia o código fonte da sua aplicação
COPY src /code/src

# Executa o build empacotando o JAR da aplicação (pulando testes)
RUN mvn package -DskipTests -B

# ==========================================
# ETAPA 2: Imagem Final de Execução (Runtime)
# ==========================================
# Imagem minimalista com JRE 25 para rodar a aplicação de forma segura e leve
FROM eclipse-temurin:25-jre-alpine-3.23

WORKDIR /work/

# Cria um usuário não-root para segurança da execução
RUN addgroup -S quarkus && adduser -S quarkus -G quarkus \
    && chown -R quarkus:quarkus /work

USER quarkus

# Copia o JAR compilado e as dependências geradas pelo Quarkus (padrão fast-jar)
COPY --from=build --chown=quarkus:quarkus /code/target/quarkus-app/lib/ /work/lib/
COPY --from=build --chown=quarkus:quarkus /code/target/quarkus-app/*.jar /work/
COPY --from=build --chown=quarkus:quarkus /code/target/quarkus-app/app/ /work/app/
COPY --from=build --chown=quarkus:quarkus /code/target/quarkus-app/quarkus/ /work/quarkus/

EXPOSE 8080

# Variáveis de ambiente para o Quarkus rodar correto no container
ENV JAVA_APP_JAR="/work/quarkus-run.jar"

ENTRYPOINT ["java", "-Dquarkus.http.host=0.0.0.0", "-jar", "/work/quarkus-run.jar"]