FROM eclipse-temurin:21-jre-alpine

# Instalar curl en la imagen
RUN apk update && apk add curl

# Copiar el archivo JAR
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Copiar los archivos de la imagen de la marca
COPY marca_etec.png marca_etec.png
COPY marca_um.png marca_um.png
COPY medio_pago_1.png medio_pago_1.png
COPY medio_pago_2.png medio_pago_2.png
ENTRYPOINT ["java","-jar","/app.jar"]
