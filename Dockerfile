# ==========================================
# ETAPA 1: Compilação (Build)
# ==========================================
FROM quay.io/quarkus/ubi9-quarkus-mandrel-builder-image:jdk-25 AS build

USER root

# Define o diretório de trabalho dentro do container de build
WORKDIR /code

# Copia os arquivos de configuração do Maven primeiro (melhora o cache do Docker)
COPY mvnw /code/mvnw
COPY .mvn /code/.mvn
COPY pom.xml /code/pom.xml

# Baixa as dependências (evita baixar tudo de novo se o pom.xml não mudou)
RUN ./mvnw dependency:go-offline

# Copia o código fonte da sua aplicação
COPY src /code/src

# Executa o build nativo dentro do container
RUN ./mvnw package -Dnative -DskipTests \
    -Dquarkus.native.native-image-xmx=5g \
    -Dquarkus.native.additional-build-args=-J-Xmx5g,--initialize-at-run-time=org.apache.http.impl.auth.NTLMEngineImpl

# ==========================================
# ETAPA 2: Imagem Final de Execução
# ==========================================
FROM quay.io/quarkus/ubi9-quarkus-micro-image:2.0

WORKDIR /work/

RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work

# A mágica acontece aqui: copiamos o executável gerado na ETAPA 1 (build)
# Atenção aqui: o "--from=build" garante que o Docker busque da Etapa 1, e não da sua máquina!
COPY --from=build --chown=1001:root --chmod=0755 /code/target/*-runner /work/application

EXPOSE 8080
USER 1001

ENTRYPOINT ["./application", "-Dquarkus.http.host=0.0.0.0"]