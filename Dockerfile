# Estágio de build
FROM ubuntu:latest AS build

# Atualiza o índice de pacotes e instala dependências
RUN apt-get update && \
    apt-get install -y openjdk-22-jdk maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copia o código-fonte para o contêiner
COPY . .

# Compila o projeto com Maven
RUN mvn clean install

# Estágio final
FROM openjdk:22-jdk-slim

# Expõe a porta 8080
EXPOSE 8080

# Copia o arquivo JAR do estágio de build para o estágio final
COPY --from=build target/Khat-0.0.1-SNAPSHOT.jar Khat.jar

# Define o ponto de entrada
ENTRYPOINT ["java", "-jar", "Khat.jar"]
