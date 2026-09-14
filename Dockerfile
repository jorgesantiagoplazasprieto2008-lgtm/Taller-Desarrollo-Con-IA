# Multi-stage / Runtime Dockerfile para Entre Copas
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="Equipo de Ingeniería Entre Copas"
LABEL description="Contenedor de producción para la plataforma web Entre Copas (Spring Boot 3 + JSP)"

WORKDIR /app

# Copia del artefacto ejecutable generado con maven package
COPY target/entre-copas-app-0.0.1-SNAPSHOT.war app.war

# Variables de entorno predeterminadas
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

EXPOSE 8080

# Parámetros de JVM optimizados para contenedores
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.war"]
