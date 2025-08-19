# ===== Build =====
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copia el wrapper primero para cachear dependencias
COPY gradlew gradlew
COPY gradle gradle
RUN chmod +x gradlew

# Copia el resto del proyecto
COPY . .

# Construye el JAR ejecutable
ENV GRADLE_OPTS="-Dorg.gradle.daemon=false -Dorg.gradle.jvmargs='-Xmx1g -Dfile.encoding=UTF-8'"
RUN ./gradlew clean bootJar --no-daemon

# ===== Run =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia el JAR construido (comodín por si cambia el nombre)
COPY --from=build /app/build/libs/*.jar app.jar

# Puerto por defecto (se puede sobreescribir con SERVER_PORT)
ENV SERVER_PORT=8080
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
