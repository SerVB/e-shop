# e-shop
## Rules
- [x] Public repo.
- [x] Task per branch.
- [ ] Description.
- [ ] Architecture graph.
- [ ] Postman scheme/Swagger.
- [ ] Docker-compose (run only with single `docker-compose up` command).

## Stages
### 1.
Rest API:
- [x] Create product (POST `/product`).
- [x] Remove product (DELETE `/product`).
- [x] Return list of products (GET `/products`).
- [x] Return product (GET `/product`).
- [x] Edit product (PATCH `/product`).

"Product" entity:
* Name.
* ID.
* Type.

Extra points:
- [x] Pagination in list of products.
- [ ] Data storage in DB.
- [x] Versioning.
- [ ] Logging.
