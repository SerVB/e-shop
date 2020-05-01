[![Build status Badge](https://github.com/SerVB/e-shop/workflows/Build/badge.svg)](https://github.com/SerVB/e-shop/actions)
[![Tests status Badge](https://github.com/SerVB/e-shop/workflows/Tests/badge.svg)](https://github.com/SerVB/e-shop/actions)

[![Docker-compose Badge](https://img.shields.io/badge/docker-compose-blue.svg)](https://docs.docker.com/compose/)
![jdbc postgresql Badge](https://img.shields.io/badge/jdbc-postgresql-darkblue.svg)
![Swagger UI Badge](https://img.shields.io/badge/swagger-ui-black.svg)

[![Kotlin Badge](https://img.shields.io/badge/Kotlin-1.3.71-green.svg)](https://kotlinlang.org/)

[![Exposed Badge](https://img.shields.io/badge/Exposed-0.22.1-green.svg)](https://github.com/papsign/Ktor-OpenAPI-Generator)
[![gRPC Badge](https://img.shields.io/badge/gRPC-1.28.0-388.svg)](https://grpc.io/)
[![Kotest Badge](https://img.shields.io/badge/Kotest-4.0.2-green.svg)](https://github.com/kotest/kotest)
[![Ktor Badge](https://img.shields.io/badge/Ktor-1.3.2-green.svg)](https://ktor.io/)
[![Ktor-OpenAPI-Generator Badge](https://img.shields.io/badge/Ktor%20OpenAPI%20Generator-646f366-green.svg)](https://github.com/papsign/Ktor-OpenAPI-Generator)
[![Testcontainrs Badge](https://img.shields.io/badge/Testcontainers-1.13.0-blue.svg)](https://www.testcontainers.org/)
# e-shop
A project to learn modern web technologies – backend for an online shop.

## Running in Docker
You can run the project easily using Docker:
```shell script
docker-compose up
```

After that, you can check Swagger UI at <http://localhost:8080/swagger-ui> and <http://localhost:8081/swagger-ui> for the API description.

## Running locally
If you want to check it out locally, you should provide database like this:
```shell script
docker run -it -e POSTGRES_PASSWORD=123 -e POSTGRES_USER=user -e POSTGRES_DB=mydb -p 5432:5432 postgres:9.6
```

After this, you can run `./gradlew :product:run` and `./gradlew :auth:run`. Don't forget about setting env like in docker-compose: `DB_USER=user;DB_HOST=localhost;DB_PORT=5432;DB_DB=mydb;DB_PASSWORD=123;AUTH_PORT=8081;AUTH_HOST=localhost`.

## How does it work
![Container diagram](https://kroki.io/c4plantuml/svg/eNqdVFtv2jAUfvevOOOJaXSjWyv1sZSg9YIgI-nQniITm2A1sSPbKUPT_vuOjQNUjbapeYh0Ts53Of6sXBtLtW2qkrwTMi8bxmF8kY2VtFRIrj_W7hOZjn7MH9MsncdZNF_O2np5l95m08nXySzqvyfEClsivMUCE7TQtIK10sDPzEbVhMRcGyX7jeF6AI_-3RuBK0Gtw1QPuZKdsbzKblQjGdW7Ps_cFxxuR-AXAXwOamEiq7ViTW4zWovD9Flowii-g1FdlyKnVijZw4kHZUshB_BglXZ1rNWzYNwELxCgBtaNzB2IlsLu4FlQWEyS1FGi304rtLGblz5c5y0mPO7_DUSrPlu1J7Gn8-tH1NIVNdxLKGMLzZNvU1clKIxyh2WFxNCqvb9OcufIReeM_ZPWx6t5IYzVnvOUH6hk-wUNNwYbBhV_E7LgZbgmnckulX4ysBWIa107xavh1RBqpXHt-2Q--3SbpjHyvSI7yeaEyXUDy3kHi6PJZlwUm5XqvnCv2b9jXIxaPAWa57ggWPXEpfd6ORxenkPQKRbxeOANBELJ7RadBfNdYi8yXnDK8JJqVfkD3WrhNK3yQhdfPgeZyc9aGc4-3Ec3KPc3qeMOx7jfJOKwR4VrLpn_p_wB7QiCdA==)

## Rules
- [x] Public repo.
- [x] Description.
- [x] Architecture graph.
- [x] Swagger UI.
- [x] Docker-compose.

## Stages
### 1. 2020.02.08
Rest API:
- [x] Create a product.
- [x] Remove a product.
- [x] Return a list of products.
- [x] Return a product.
- [x] Edit a product.

"Product" entity:
* Name.
* ID.
* Type.

Extra points:
- [x] Pagination in list of products.
- [x] Data storage in DB.
- [x] Versioning.
- [x] Logging.

### 2. 2020.02.15
- [x] Data storage in DB.

### 3. 2020.03.01
- [x] Docker-compose.

### 4. 2020.03.01
- [x] Registration (via login+password).
- [x] Auth (change login+password to access+refresh tokens).
- [x] A way to change a refresh token to new access+refresh tokens.
- [x] Validation endpoint.
- [x] Products changing endpoints must be available only with auth tokens.

### 7. 2020.04.14
- [x] Replace token validation to gRPC variant.
- [x] Make protobuf files shared (extract to a module).
- [x] Hash passwords in storage.
- [x] Add roles: admin and user.
    - Admin can create, edit, remove, and view products. Also, they can create a new administrator (however, first admin can be created without admin rights).
    - User can view products.
