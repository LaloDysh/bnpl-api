## Getting Started

### Prerequisites
- Docker and Docker Compose
- JDK 17 (for development only)
- Maven 3.8+ (for development only)

### Running with Docker
1. Clone the repository
   ```bash
   git clone <repository-url>
   cd bnpl-service
   ```

2. Start the application using Docker Compose
   ```bash
   docker-compose up
   ```

3. The application will be available at http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - OpenAPI Documentation: http://localhost:8080/v3/api-docs

### Test Setup


2. Build the project
   ```bash
   mvn clean install
   ```

3. Run the tests
   ```bash
   mvn test
   ```

4. Generate test coverage reports
   ```bash
   mvn verify
   ```

5. Generate report html
    ```bash
    mvn surefire-report:report
   ```
Test reports can be found at:
    - JUnit reports: `/target/reports/surefire.html`


## API Documentation

### Customer API

#### Create Customer
```
POST /v1/customers
```
Creates a new customer and assigns a credit line based on age.

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "secondLastName": "Smith",
  "dateOfBirth": "1990-01-01"
}
```

**Response:**
```json
{
  "id": "b2863d62-0746-4b26-a6e3-edcb4b9578f2",
  "creditLineAmount": 8000.00,
  "availableCreditLineAmount": 8000.00,
  "createdAt": "2025-05-12T00:00:00.000Z"
}
```

The response header `X-Auth-Token` contains a JWT token used for authentication.

#### Get Customer
```
GET /v1/customers/{customerId}
```
Gets customer details by ID.

### Loan API

#### Create Loan
```
POST /v1/loans
```
Creates a new loan for a customer.

**Request Body:**
```json
{
  "customerId": "b2863d62-0746-4b26-a6e3-edcb4b9578f2",
  "amount": 1000.00
}
```

**Response:**
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa7",
  "customerId": "b2863d62-0746-4b26-a6e3-edcb4b9578f2",
  "amount": 1000.00,
  "status": "ACTIVE",
  "createdAt": "2025-05-12T00:00:00.000Z",
  "paymentPlan": {
    "commissionAmount": 130.00,
    "installments": [
      {
        "amount": 226.00,
        "scheduledPaymentDate": "2025-05-12",
        "status": "NEXT"
      },
      // ... 4 more installments
    ]
  }
}
```

#### Get Loan
```
GET /v1/loans/{loanId}
```
Gets loan details by ID.


## Architecture

The application follows a Hexagonal (Ports & Adapters) Architecture with:

- Domain Layer: Contains business entities and logic
- Application Layer: Contains use cases and services
- Infrastructure Layer: Contains adapters for persistence, web, and security

## Logging

Logs are written to:
- Console
- File at `logs/bnpl-service.log`

Log configuration can be found in `src/main/resources/logback-spring.xml`

## Configuration

Main configuration properties (found in `application.yml`):
- `server.port`: HTTP port (default: 8080)
- `spring.datasource.*`: Database connection settings
- `spring.jpa.hibernate.ddl-auto`: Schema update strategy
- `jwt.secret`: Secret key for JWT token generation
- `jwt.expiration`: JWT token expiration time in seconds


## env variables

```
Generate a file in the root of the project call .env and paste this variables
```
# Database Configuration
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=bnpl

# Spring Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/bnpl
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_JPA_HIBERNATE_DDL_AUTO=validate

# JWT Configuration
JWT_SECRET=asecuresecretkeywithlength32charactr
JWT_EXPIRATION=86400

# PostgreSQL Performance Tuning
POSTGRES_SHARED_BUFFERS=256MB
POSTGRES_EFFECTIVE_CACHE_SIZE=768MB
POSTGRES_WORK_MEM=8MB
POSTGRES_MAINTENANCE_WORK_MEM=64MB
POSTGRES_MAX_CONNECTIONS=100

# Docker Resource Limits
CONTAINER_CPU_LIMIT=1.0
CONTAINER_MEMORY_LIMIT=1G

# Server Configuration
SERVER_PORT=8080