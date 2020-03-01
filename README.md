# e-shop
## Running
You can run the project easily using Docker:
```shell script
docker-compose up
```

After that, you can check Swagger UI at <http://localhost:8080> for the API description.

## Rules
- [x] Public repo.
- [x] Task per branch.
- [x] Description.
- [ ] Architecture graph.
- [x] Swagger UI.
- [x] Docker-compose.

## Stages
### 1.
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
