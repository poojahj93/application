FROM openjdk:11-jdk-alpine
WORKDIR /opt/nets
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} /opt/nets/app.jar
EXPOSE 8084
CMD java -jar app.jar
#ENTRYPOINT ["java","-jar","app.jar"]