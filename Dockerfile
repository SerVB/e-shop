FROM openjdk:8 as builder

WORKDIR /root/e-shop
COPY . .
RUN chmod +x gradlew
RUN ./gradlew :product:fatJar

FROM openjdk:8-alpine

WORKDIR /root/e-shop

COPY --from=builder /root/e-shop/product/build/libs/product-fat-1.0-SNAPSHOT.jar product.jar

CMD ["java", "-jar", "product.jar"]
