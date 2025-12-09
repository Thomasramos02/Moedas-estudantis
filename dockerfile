FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar primeiro o pom.xml para aproveitar cache
COPY pom.xml .

# Baixar dependências (inclui Lombok)
RUN mvn dependency:go-offline -B

# Copiar código fonte
COPY src ./src

# Compilar com preview features habilitadas
RUN mvn clean compile package -DskipTests \
    -Dmaven.compiler.source=21 \
    -Dmaven.compiler.target=21 \
    -Dmaven.compiler.fork=true

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar o JAR gerado
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Executar com preview features habilitadas
ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]
