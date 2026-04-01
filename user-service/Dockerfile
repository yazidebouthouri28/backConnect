# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copie uniquement les fichiers nécessaires pour le cache Maven
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Préparer le wrapper et télécharger les dépendances Maven pour utiliser le cache
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copier le code source
COPY src/ src/

# Build le jar sans tests
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copier le jar depuis le stage build
COPY --from=build /app/target/*.jar app.jar

# Expose le port de ton service
EXPOSE 8084

# Commande de lancement
ENTRYPOINT ["java", "-jar", "app.jar"]