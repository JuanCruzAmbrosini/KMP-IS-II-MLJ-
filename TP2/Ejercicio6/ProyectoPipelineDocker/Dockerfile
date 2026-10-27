# ====================================================================
# Dockerfile optimizado para empaquetar Spring Boot con Java 17
# ====================================================================

# Imagen base oficial de Eclipse Temurin JRE 17 sobre Alpine Linux (ultra ligera)
FROM eclipse-temurin:17-jre-alpine

# Metadatos del contenedor
LABEL maintainer="DevOps Team"
LABEL description="API REST de Gestión de Catálogo - Spring Boot 3 & Java 17"

# Buenas prácticas de seguridad: Crear usuario y grupo no-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Argumento para especificar el JAR generado por Maven
ARG JAR_FILE=target/*.jar

# Copiar el binario JAR empaquetado
COPY ${JAR_FILE} app.jar

# Asignar la propiedad de los archivos al usuario sin privilegios
RUN chown -R appuser:appgroup /app

# Cambiar al usuario sin privilegios
USER appuser

# Exponer el puerto HTTP predeterminado
EXPOSE 8080

# Opciones de JVM optimizadas para contenedores (respeto a límites cgroups de RAM y CPU)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"

# Healthcheck para validación periódica del estado del contenedor
HEALTHCHECK --interval=30s --timeout=5s --retries=3 --start-period=30s \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/api/books || exit 1

# Comando de ejecución de la aplicación
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

