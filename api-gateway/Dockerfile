# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copier Maven wrapper et pom.xml séparément pour un cache Docker efficace
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Rendre mvnw exécutable et télécharger toutes les dépendances Maven (offline)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copier le code source
COPY src/ src/

# Compiler le projet et créer le JAR sans exécuter les tests
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copier le JAR depuis le stage build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port du service
EXPOSE 8088

# Lancer le service
ENTRYPOINT ["java", "-jar", "app.jar"]