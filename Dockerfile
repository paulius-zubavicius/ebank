
FROM openjdk:8-jre
CMD mkdir -p /usr/web/ebank/logs
COPY target/ebank.jar /usr/web/ebank/ebank.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/web/ebank/ebank.jar"]