# Docker context is meant to be the root project dir
FROM openjdk:8 as builder

WORKDIR /root/build
COPY . .
RUN chmod +x gradlew
RUN ./gradlew :auth:fatJar

FROM openjdk:8-alpine

WORKDIR /root/e-shop-auth

COPY --from=builder /root/build/auth/build/libs/auth-fat-1.0-SNAPSHOT.jar auth.jar

CMD ["java", "-jar", "auth.jar"]
