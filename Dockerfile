# Build e runtime separados: a imagem final só tem o JRE e o jar.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline
COPY src/ src/
RUN ./mvnw -B -q package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --no-create-home appuser
USER appuser
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# JWT_SECRET é obrigatório (mín. 32 caracteres); sem ele a app não sobe.
ENTRYPOINT ["java", "-jar", "app.jar"]
