# Spring Boot Demo for Logging Request and Errors to DB

## Requirements
- java 11
- maven 3.8.6

## Build

```shell
mvn clean install
```

## Run

```shell
java -jar target/logging-0.0.1-SNAPSHOT.jar
```

## H2 Console access

http://localhost:8080/h2
- Driver Class: org.h2.Driver
- JDBC URL: jdbc:h2:mem:testdb
- User Name: sa
- Password:
