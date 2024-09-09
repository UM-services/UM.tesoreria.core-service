FROM eclipse-temurin:21-jre-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY marca_etec.png marca_etec.png
COPY marca_um.png marca_um.png
COPY medios.pdf medios.pdf
ENTRYPOINT ["java","-jar","/app.jar"]
