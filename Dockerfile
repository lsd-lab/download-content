# Etapa de build
FROM ubuntu:latest AS build

# Atualizar e instalar dependências necessárias
RUN apt-get update && apt-get install -y \
    python3 \
    python3-pip \
    python3-venv \
    openjdk-17-jdk \
    maven

# Criar um ambiente virtual e instalar youtube_dl
RUN python3 -m venv /opt/venv
ENV PATH="/opt/venv/bin:$PATH"
RUN pip install --upgrade pip
RUN pip install --upgrade youtube_dl

# Copiar o código fonte e construir o projeto
COPY . .
RUN mvn clean install

# Expor a porta da aplicação
EXPOSE 8080

# Etapa final
FROM openjdk:17-jdk-slim

# Copiar o jar da etapa de build
COPY --from=build /target/download-content-0.0.1-SNAPSHOT.jar app.jar

# Definir o ponto de entrada
ENTRYPOINT ["java", "-jar", "app.jar"]
