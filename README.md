[![Kotlin Badge](https://img.shields.io/badge/kotlin-1.3.71-green.svg)](https://kotlinlang.org/)
[![Ktor Badge](https://img.shields.io/badge/ktor-1.3.2-green.svg)](https://ktor.io/)
[![Ktor-OpenAPI-Generator Badge](https://img.shields.io/badge/ktor--openapi--generator-646f366-green.svg)](https://github.com/papsign/Ktor-OpenAPI-Generator)
[![Exposed Badge](https://img.shields.io/badge/Exposed-0.22.1-green.svg)](https://github.com/papsign/Ktor-OpenAPI-Generator)
[![Kotest Badge](https://img.shields.io/badge/kotest-4.0.2-green.svg)](https://github.com/kotest/kotest)
![jdbc postgresql Badge](https://img.shields.io/badge/jdbc-postgresql-darkblue.svg)
[![testcontainrs Badge](https://img.shields.io/badge/testcontainers-1.13.0-blue.svg)](https://www.testcontainers.org/)
[![Docker-compose Badge](https://img.shields.io/badge/docker-compose-blue.svg)](https://docs.docker.com/compose/)
[![Build status Badge](https://github.com/SerVB/e-shop/workflows/Build/badge.svg)](https://github.com/SerVB/e-shop/actions)
[![Tests status Badge](https://github.com/SerVB/e-shop/workflows/Tests/badge.svg)](https://github.com/SerVB/e-shop/actions)
# e-shop
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

After this, you can run `./gradlew :product:run` and `./gradlew :auth:run`. Don't forget about setting env like in docker-compose: `DB_USER=user;DB_HOST=localhost;DB_PORT=5432;DB_DB=mydb;DB_PASSWORD=123`.

## How does it work
![Container diagram](https://kroki.io/c4plantuml/svg/eNqdk99v2jAQx9_9V9x4KdPo2m2d1MdSgtYfFDIShPYUGWzAWuKL7Espqva_zw4OpWqkTc1DJF_uPt_v3TknMLiAraINxDnXNHsYMXZliRuqipx9UHqZV0K6pGyAmrjS0nwu_Sc26v-azNIsncRZNJmPm_P8Nr3JRsMfw3HU_cgYKcpdeVMLQvG14QWs0IA8tRssGYulsai7lZWmB7P63emDPwKuQlbHsZKdJVlk11hpwc2uKzP_xSU3KfDMwD0HtZCRlQZFtaSMl-qQfRqC0I9voV-WuVpyUqg7LuMeKVe6B_eExp9jg49KSBu8QCi1sKr00hfxXNEOHhWH6TBJPdL5bbXCK9q89uEj7zFR1_2_gWjRFYtmEntc3X7EiS-4lbUEWlobmfwc-VPihJ3coVml3dKKvb9WuHfkV-eN_RNbr9fItbJkauYxH7gW-wattNYFrFP8w9hU5uGatG52jua33d_mxrVXvDy_PIcSjWv7LpmMz27SNHa8N7Cj3RyRfDRQvrRQakybm1fTnkou3HUxWNStbY0iNwNCD_5-8e1rAA-fSrRSfLqLrge9pgctaevsBMdvvL4M_l0ivvZF4Upq4f_uv3VnXos=)

## Rules
- [x] Public repo.
- [x] Task per branch.
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
- [ ] Products changing endpoints must be available only with auth tokens.
