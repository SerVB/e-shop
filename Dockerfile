FROM openjdk:8 as builder

WORKDIR /root/e-shop
COPY . .
RUN chmod +x gradlew
RUN ./gradlew :server:fatJar

FROM openjdk:8-alpine

WORKDIR /root/e-shop

COPY --from=builder /root/e-shop/server/build/libs/server-fat-1.0-SNAPSHOT.jar server.jar

CMD ["java", "-jar", "server.jar"]
