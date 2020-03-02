[![Kotlin Badge](https://img.shields.io/badge/kotlin-1.3.61-green.svg)](https://kotlinlang.org/)
[![Ktor Badge](https://img.shields.io/badge/ktor-1.3.1-green.svg)](https://ktor.io/)
[![Ktor-OpenAPI-Generator Badge](https://img.shields.io/badge/ktor--openapi--generator-0.0--beta.0-green.svg)](https://github.com/papsign/Ktor-OpenAPI-Generator)
[![Docker-compose Badge](https://img.shields.io/badge/docker-compose-blue.svg)](https://docs.docker.com/compose/)
# e-shop
## Running
You can run the project easily using Docker:
```shell script
docker-compose up
```

After that, you can check Swagger UI at <http://localhost:8080/swagger-ui> for the API description.

## Rules
- [x] Public repo.
- [x] Task per branch.
- [x] Description.
- [ ] Architecture graph.
- [x] Swagger UI.
- [x] Docker-compose.

## Stages
### 1. 2020.02.08 – 2020.03.07
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
- [ ] Data storage in DB.
- [x] Versioning.
- [x] Logging.
