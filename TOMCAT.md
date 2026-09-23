# HW-37: Order Servlet (Tomcat)

## What is inside
- `Product` — id, name, cost (`BigDecimal`)
- `Order` — id, date, cost, products
- `OrderServlet` — CRUD:
  - `POST /orders` — create
  - `GET /orders?id=1` — read by id (`GET /orders` — all)
  - `PUT /orders` — update
  - `DELETE /orders?id=1` — delete
- Jackson JSON mapping
- JUnit 5 + Mockito tests

## Install Apache Tomcat (macOS)

1. Download Tomcat 10.1.x: https://tomcat.apache.org/download-10.cgi  
   (Core → tar.gz)
2. Unpack, for example to `~/tomcat`
3. Or in IntelliJ:  
   **Settings → Build Tools / Application Servers → + → Tomcat Server** and point to the unpacked folder.

Java 17+ and **Tomcat 10+** (Jakarta Servlet) are required.

## Run in IntelliJ

1. Open project, wait for Maven import
2. `mvn clean package` → `target/order-servlet.war`
3. Run configuration: **Tomcat Server** → Deployment → add artifact `order-servlet:war`  
   Application context: `/order-servlet`
4. Start Tomcat
5. API base: `http://localhost:8080/order-servlet/orders`

## Example requests (curl)

```bash
# CREATE
curl -s -X POST http://localhost:8080/order-servlet/orders \
  -H 'Content-Type: application/json' \
  -d '{
    "date":"2026-09-23",
    "products":[
      {"id":1,"name":"Laptop","cost":1000},
      {"id":2,"name":"Mouse","cost":50}
    ]
  }'

# READ
curl -s 'http://localhost:8080/order-servlet/orders?id=1'

# UPDATE
curl -s -X PUT http://localhost:8080/order-servlet/orders \
  -H 'Content-Type: application/json' \
  -d '{
    "id":1,
    "date":"2026-09-24",
    "products":[{"id":1,"name":"Laptop","cost":900}]
  }'

# DELETE
curl -s -X DELETE 'http://localhost:8080/order-servlet/orders?id=1'
```

## Tests

```bash
mvn test
```
