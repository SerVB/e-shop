[![Kotlin Badge](https://img.shields.io/badge/kotlin-1.3.71-green.svg)](https://kotlinlang.org/)
[![Ktor Badge](https://img.shields.io/badge/ktor-1.3.2-green.svg)](https://ktor.io/)
[![Ktor-OpenAPI-Generator Badge](https://img.shields.io/badge/ktor--openapi--generator-0.2--beta.1--experimental-green.svg)](https://github.com/papsign/Ktor-OpenAPI-Generator)
[![Exposed Badge](https://img.shields.io/badge/Exposed-0.22.1-green.svg)](https://github.com/papsign/Ktor-OpenAPI-Generator)
[![Kotest Badge](https://img.shields.io/badge/kotest-4.0.0--BETA3-green.svg)](https://github.com/kotest/kotest)
![jdbc postgresql Badge](https://img.shields.io/badge/jdbc-postgresql-darkblue.svg)
[![Docker-compose Badge](https://img.shields.io/badge/docker-compose-blue.svg)](https://docs.docker.com/compose/)
[![Build status Badge](https://github.com/SerVB/e-shop/workflows/Build/badge.svg)](https://github.com/SerVB/e-shop/actions)
# e-shop
## Running in Docker
You can run the project easily using Docker:
```shell script
docker-compose up
```

After that, you can check Swagger UI at <http://localhost:8080/swagger-ui> for the API description.

## Running locally
If you want to check it out locally, you should provide database like this:
```shell script
docker run -e POSTGRES_PASSWORD=123 -e POSTGRES_USER=user -e POSTGRES_DB=mydb -p 5432:5432 postgres:9.6
```

After this, you can run `./gradlew run`. Don't forget about setting env like in docker-compose: `DB_USER=user;DB_HOST=localhost;DB_PORT=5432;DB_DB=mydb;DB_PASSWORD=123`.

Another option is to disable database storage. Just run `./gradlew run` with env `io.github.servb.eShop.forceInMemory=true`.

## Architecture graph
![Architecture graph](docs/e-shop-architecture-graph.svg)

## Rules
- [x] Public repo.
- [x] Task per branch.
- [x] Description.
- [x] Architecture graph.
- [x] Swagger UI.
- [x] Docker-compose.

## Stages
### 1. 2020.02.08 – 2020.03.15
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

### 2. 2020.02.15 – 2020.03.15
- [x] Data storage in DB.

### 3. 2020.03.01 – 2020.03.30
- [x] Docker-compose.

### 4. 2020.03.01 – 2020.03.30
- [ ] Registration (via login+password).
- [ ] Auth (change login+password to access+refresh tokens).
- [ ] A way to change a refresh token to new access+refresh tokens.
- [ ] Validation endpoint.
- [ ] All products endpoints must be available only with auth tokens.
