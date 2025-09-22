# Management Selection Admin API (Seek Challenge)

## Description
This is a comprehensive **Backend Java Developer** technical challenge implementation for Seek. The API provides a complete **Candidate Management System** with client registration, data querying, statistical analysis, and advanced monitoring capabilities.

The system demonstrates enterprise-grade development practices including security, scalability, monitoring, and clean architecture patterns.

## About this project

### Back-end Server
The back-end server is built with **Spring Boot 3.5.6** and **Java 21**. It uses a traditional **Spring MVC** stack with **Spring Data JPA** for database operations. The database used is **MySQL 8.0**, and the API is protected using **Spring Security with JWT authentication**.

**Default credentials for testing:**
- **Username:** `admin`
- **Password:** `admin123` 
- **Email:** `admin@seek.com`

### Core Features
- ✅ **Client Registration** - Create new clients with validation
- ✅ **Metrics Consultation** - Statistical analysis (average age, standard deviation, min/max ages)
- ✅ **Client Listing** - Paginated list with derived calculations (life expectancy estimations)
- ✅ **JWT Authentication** - Secure user registration and login
- ✅ **API Documentation** - Interactive Swagger UI with security integration
- ✅ **Exception Handling** - Centralized error management with appropriate HTTP codes
- ✅ **Unit & Integration Testing** - Comprehensive test coverage with JaCoCo reporting
- ✅ **Performance Optimization** - Caching, HTTP compression, pagination
- ✅ **Asynchronous Processing** - Background tasks for improved responsiveness

### Architecture & Design Patterns
The application follows **Clean Architecture** principles with clear separation of concerns:

```
📁 src/main/java/com/example/management_selection_admin_seek/
├── 🏛️ api/                    # API Contracts (OpenAPI interfaces)
├── 🎮 controller/             # REST Controllers
├── 🏗️ service/                # Business Logic Layer
├── 🗃️ repository/             # Data Access Layer
├── 🏢 entity/                 # JPA Entities
├── 📦 dto/                    # Data Transfer Objects
├── 🔄 mapper/                 # MapStruct Mappers
├── ⚙️ config/                 # Configuration Classes
├── 🚨 exception/              # Custom Exceptions & Global Handler
└── 🔢 enums/                  # Enumerations
```

### Technology Stack

#### Core Framework
- **Spring Boot 3.5.6** - Main application framework
- **Spring Security** - Authentication & Authorization (JWT)
- **Spring Data JPA** - Database operations with Hibernate
- **Spring Web MVC** - RESTful API development
- **Spring Cache** - Performance optimization with Caffeine
- **Spring Actuator** - Health checks and monitoring

#### Database & Migrations
- **MySQL 8.0** - Primary database
- **Flyway** - Database migration management
- **HikariCP** - High-performance connection pooling

#### Documentation & Validation
- **SpringDoc OpenAPI 3** - API documentation (Swagger UI)
- **Jakarta Validation** - Request/Response validation
- **MapStruct** - Type-safe bean mapping

#### Security & Authentication
- **JWT (JSON Web Tokens)** - Stateless authentication
- **BCrypt** - Password hashing
- **Spring Security Filter Chain** - Request authentication

#### Monitoring & Observability
- **Prometheus** - Metrics collection and storage
- **Grafana** - Metrics visualization and dashboarding
- **Loki** - Log aggregation system
- **Promtail** - Log shipping to Loki
- **Micrometer** - Application metrics

#### Testing & Quality
- **JUnit 5** - Unit testing framework
- **Mockito** - Mocking framework
- **AssertJ** - Fluent assertions
- **Testcontainers** - Integration testing with real databases
- **JaCoCo** - Code coverage reporting (80% minimum)
- **H2 Database** - In-memory database for testing

#### Build & Deployment
- **Gradle** - Build automation and dependency management
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration

## API Endpoints

### 🔐 Authentication Endpoints
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User authentication
- `POST /api/auth/refresh` - Token refresh

### 👥 Client Management Endpoints (Protected)
- `POST /api/client` - Create new client
- `GET /api/client` - List all clients with pagination and life expectancy calculations
- `GET /api/client/metrics` - Get statistical metrics (average age, standard deviation, etc.)

### 📊 Monitoring Endpoints
- `GET /api/actuator/health` - Application health status
- `GET /api/actuator/metrics` - Application metrics
- `GET /api/actuator/prometheus` - Prometheus metrics format

### 📚 Documentation
- `GET /api/swagger-ui.html` - Interactive API documentation

## Monitoring Stack

### Prometheus
Prometheus collects metrics from the Spring Boot application including:
- HTTP request metrics (response times, status codes)
- JVM metrics (memory usage, garbage collection)
- Custom business metrics
- Database connection pool metrics

Access Prometheus at: `http://localhost:9090`

### Grafana
Grafana provides rich dashboards for visualizing:
- Application performance metrics
- Log analysis with different severity levels (INFO, WARN, ERROR)
- System resource utilization
- Custom business dashboards

**Access Grafana at:** `http://localhost:3000`
- **Username:** `admin`
- **Password:** `admin`

**Log Queries for Custom Dashboards:**
```logql
# INFO logs
{job="seek-management-app"} |= "INFO"

# WARN logs  
{job="seek-management-app"} |= "WARN"

# ERROR logs
{job="seek-management-app"} |= "ERROR"
```

### Loki + Promtail
- **Loki** aggregates application logs for analysis
- **Promtail** ships logs from the application to Loki
- Supports log filtering, searching, and alerting

## How to run locally

### Prerequisites
- **Docker** and **Docker Compose**
- **Java 21** (for development)
- **Gradle** (for building from source)
- Free ports: `8080`, `3000`, `3306`, `3100`, `9090`

### Steps to run the project

1. **Clone the repository:**
```bash
git clone <repository-url>
cd management-selection-admin-seek
```

2. **Start the complete stack:**
```bash
docker-compose up -d
```

3. **Verify all services are running:**
```bash
docker-compose ps
```

4. **Access the services:**
   - **API Documentation:** http://localhost:8080/api/swagger-ui.html
   - **Grafana Dashboard:** http://localhost:3000
   - **Prometheus Metrics:** http://localhost:9090
   - **Application Health:** http://localhost:8080/api/actuator/health

### Alternative: Development Mode

If you want to run the application for development:

1. **Start only the database and monitoring stack:**
```bash
# Comment out the 'app' service in docker-compose.yml
docker-compose up -d mysql loki grafana prometheus promtail
```

2. **Run the Spring Boot application:**
```bash
./gradlew bootRun
```

## Testing

### Run all tests:
```bash
./gradlew test
```

### Generate coverage report:
```bash
./gradlew jacocoTestReport
```
View the report at: `build/reports/jacoco/test/html/index.html`

### Verify coverage thresholds:
```bash
./gradlew jacocoTestCoverageVerification
```

## Example Usage

### 1. Register a new user:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com", 
    "password": "password123",
    "fullName": "Test User"
  }'
```

### 2. Login to get JWT token:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "admin",
    "password": "admin123"
  }'
```

### 3. Create a new client (using JWT token):
```bash
curl -X POST http://localhost:8080/api/client \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Juan",
    "lastName": "Pérez", 
    "age": 32,
    "birthDate": "1993-05-15"
  }'
```

### 4. Get client metrics:
```bash
curl -X GET http://localhost:8080/api/client/metrics \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Performance Features

- **🚀 HTTP/2 Support** - Enhanced connection efficiency
- **📦 Response Compression** - Reduced bandwidth usage  
- **🗄️ Caching** - Improved response times for frequent queries
- **📄 Pagination** - Efficient handling of large datasets
- **⚡ Async Processing** - Non-blocking background tasks
- **🏊 Connection Pooling** - Optimized database connections

## Code Quality & Standards

- **📊 80% Code Coverage** - Enforced via JaCoCo
- **🧪 Comprehensive Testing** - Unit, Integration, and Repository tests
- **📝 API Documentation** - OpenAPI 3.0 with examples
- **🔒 Security Best Practices** - JWT, input validation, error handling
- **🏗️ Clean Architecture** - SOLID principles and separation of concerns
- **🔄 Automated Mapping** - MapStruct for type-safe transformations

## Production Considerations

- **Environment Profiles** - Separate configurations for `dev`, `test`, `prod`
- **Health Checks** - Built-in monitoring endpoints
- **Graceful Degradation** - Comprehensive exception handling
- **Security** - JWT-based stateless authentication
- **Scalability** - Async processing and caching strategies
- **Observability** - Comprehensive logging and metrics

---

**🏆 This implementation demonstrates enterprise-grade Spring Boot development with modern DevOps practices, comprehensive testing, and production-ready monitoring solutions.**
