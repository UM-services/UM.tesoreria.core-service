FROM maven:3.8.7-eclipse-temurin-17-alpine as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
RUN --mount=type=cache,target=/root/.m2 mvn -f $HOME/pom.xml clean package

FROM eclipse-temurin:17-jdk
COPY --from=build /usr/app/target/*.jar app.jar
COPY marca_etec.png marca_etec.png
COPY marca_um.png marca_um.png
COPY medios.png medios.png
ENTRYPOINT ["java","-jar","/app.jar"]
