FROM ubuntu:latest As build

RUN apt-get update

RUN apt-get install python3 -y
RUN apt-get install python3-pip -y
COPY . .
RUN pip install youtube_dl

RUN apt-get install openjdk-17-jdk -y
COPY . .

RUN apt-get install maven -y
RUN mvn clean install

EXPOSE 8080

FROM openjdk:17-jdk-slim

COPY --from=build /target/download-content-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT [ "java", "-jar", "app.jar" ] 
