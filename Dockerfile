# =========================================================
# ETAPA 1: BUILD  -> compila el proyecto con Maven + JDK 21
# =========================================================
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# 1) Copiamos SOLO el pom.xml y descargamos dependencias.
#    Docker cachea esta capa: si no cambias el pom, no vuelve a descargar nada.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# 2) Copiamos el código fuente y empaquetamos el .jar.
#    Las pruebas ya se ejecutan en Jenkins (JUnit + JaCoCo + SonarQube),
#    por eso aquí se omiten para que el build en la nube sea rápido.
COPY src ./src
RUN mvn -B -q package -DskipTests \
    && cp target/*.jar app.jar

# =========================================================
# ETAPA 2: RUNTIME -> solo JRE 21 (sin Maven, sin JDK, sin código fuente)
# =========================================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Usuario sin privilegios (buena práctica de seguridad: no correr como root)
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiamos únicamente el .jar generado en la etapa anterior
COPY --from=build /app/app.jar app.jar

# Puerto por defecto; en la nube Render/Railway inyectan la variable PORT
ENV PORT=8080
EXPOSE 8080

# Opciones de JVM pensadas para contenedores pequeños (512 MB en capa gratuita)
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseSerialGC -Xss512k -XX:TieredStopAtLevel=1"

# "exec" hace que Java sea el PID 1 y reciba correctamente las señales de apagado
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
