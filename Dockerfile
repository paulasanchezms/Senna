# Usa JDK 21
FROM eclipse-temurin:21-jdk

# Carpeta de trabajo en el contenedor
WORKDIR /app

# Copia el proyecto completo al contenedor
COPY . .

# Construye el JAR sin ejecutar tests
RUN ./mvnw clean package -DskipTests

# Expone el puerto usado por Spring Boot
EXPOSE 8080

# Ejecuta el JAR generado
CMD ["java", "-jar", "target/Senna-0.0.1-SNAPSHOT.jar"]