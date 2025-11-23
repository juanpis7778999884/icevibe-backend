FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .

# 🔥 ARREGLA PERMISOS DEL mvnw
RUN chmod +x mvnw

# 🔥 ARREGLA FINES DE LINEA POR SI ESTÁN MAL (CRLF → LF)
RUN sed -i 's/\r$//' mvnw

RUN ./mvnw -q dependency:resolve

COPY src ./src

RUN ./mvnw -q package -DskipTests

EXPOSE 8000

CMD ["java", "-jar", "target/icevibe-0.0.1-SNAPSHOT.jar"]
