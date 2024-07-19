FROM ubuntu:latest As build

RUN apt-get update

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

EXPOSE 8080

FROM openjdk:17-jdk-slim

COPY --from=build /target/download-content-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ] 
