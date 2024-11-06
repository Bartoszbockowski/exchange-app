FROM openjdk:21-jdk

WORKDIR /app

COPY target/exchange-0.0.1-SNAPSHOT.jar /app/exchange-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java", "-jar", "/app/currency-exchange-0.0.1-SNAPSHOT.jar"]