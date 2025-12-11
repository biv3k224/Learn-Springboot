Product Catalog API
A full-stack product management system with Spring Boot backend and modern web interface.

🚀 Quick Setup
1. Clone & Setup
bash
git clone https://github.com/yourusername/product-catalog-api.git
cd product-catalog-api
2. Start Database (Docker)
bash
# Run MySQL in Docker
docker run --name mysql-product \
  -e MYSQL_ROOT_PASSWORD=password \
  -e MYSQL_DATABASE=product_catalog \
  -p 3306:3306 \
  -d mysql:8

# Verify it's running
docker ps
3. Run Application
bash
# Start Spring Boot
./mvnw spring-boot:run
4. Access Application
Dashboard: http://localhost:8080

API: http://localhost:8080/api/products

📌 Default Credentials
Database: product_catalog

Username: root

Password: password

Port: 3306

🎯 Features
✅ Full CRUD operations

✅ Modern responsive UI

✅ Real-time statistics

✅ Search & filter

✅ Docker MySQL setup

🔧 API Examples
bash
# Get all products
curl http://localhost:8080/api/products

# Create product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","price":100,"category":"Test"}'
Just 3 steps: Clone → Run Docker → Start App!

