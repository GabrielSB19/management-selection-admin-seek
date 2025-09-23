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
- ✅ **Asynchronous Processing** - Background tasks using Spring @Async thread pool queues

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

## Asynchronous Processing Queue

**Queue Technology:** **Spring @Async + ThreadPoolTaskExecutor**

Simple in-memory thread pool queue for background client processing tasks (notifications, reports, statistics updates). Chosen for **simplicity** and **zero infrastructure overhead** - perfect for this challenge scope.

**Configuration:** 2-4 threads, 50-task queue capacity, async prefix logging.

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

# Async queue logs
{job="seek-management-app"} |= "[ASYNC]"
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

## Performance Features

- **🚀 HTTP/2 Support** - Enhanced connection efficiency
- **📦 Response Compression** - Reduced bandwidth usage  
- **🗄️ Caching** - Improved response times for frequent queries
- **📄 Pagination** - Efficient handling of large datasets
- **⚡ Async Processing** - Non-blocking background tasks via ThreadPoolTaskExecutor queues
- **🏊 Connection Pooling** - Optimized database connections

## Code Quality & Standards

- **📊 80% Code Coverage** - Enforced via JaCoCo
- **🧪 Comprehensive Testing** - Unit, Integration, and Repository tests
- **📝 API Documentation** - OpenAPI 3.0 with examples
- **🔒 Security Best Practices** - JWT, input validation, error handling
- **🏗️ Clean Architecture** - SOLID principles and separation of concerns
- **🔄 Automated Mapping** - MapStruct for type-safe transformations

---

## AWS EC2 Cloud Deployment

The API is deployed on **AWS EC2** and is fully operational with the complete monitoring stack.

### 🌐 Public Access URLs

- 🚀 API Base URL: http://54.234.168.199:8080
- 📊 Grafana Dashboard -> [Click here](http://54.234.168.199:3000/d/seek-logs-dashboard/seek-management-logs-dashboard?orgId=1&refresh=5s)
- 📈 Prometheus Metrics -> [Click here](http://54.234.168.199:9090/targets)
- 📋 API Documentation ->[Click here](http://54.234.168.199:8080/api/swagger-ui.html)

**Note**: Requests cannot be made from the swager because CORS is not configured in the application.

### 🔑 SSH Access
Connect to the EC2 instance using SSH:
```bash
ssh -i seek.pem ec2-user@54.234.168.199
```

### 📁 Project Location
Once connected via SSH, navigate to the project directory:
```bash
cd /home/ec2-user/management-selection-admin-seek
```

### 🖥️ Service Management
**View running services:**
```bash
docker-compose ps
```

**View application logs:**
```bash
docker logs seek-app --tail=50 -f
```

**Restart the complete stack:**
```bash
docker-compose down && docker-compose up -d
```

### 🔐 Cloud Service Credentials

**Grafana Dashboard Access:**
- **URL:** http://54.234.168.199:3000
- **Username:** `admin`
- **Password:** `admin`

**API Authentication:**
- **Username:** `admin`
- **Email:** `admin@seek.com`
- **Password:** `admin123`

**Database Access (MySQL):**
- **Host:** `54.234.168.199:3306`
- **Database:** `seek_admin_db`
- **Username:** `root`
- **Password:** `admin123`

### 🏗️ Deployment Architecture
```
┌─────────────────────────────────────────────────────────┐
│                    AWS EC2 Instance                     │
│                     54.234.168.199                      │
├─────────────────────────────────────────────────────────┤
│  🐳 Docker Compose Stack                               │
│  ├── 📱 Spring Boot App       (Port 8080)             │
│  ├── 🗄️  MySQL Database       (Port 3306)             │
│  ├── 📊 Grafana               (Port 3000)             │
│  ├── 📈 Prometheus            (Port 9090)             │
│  ├── 📋 Loki                  (Port 3100)             │
│  └── 🚚 Promtail              (Log Shipping)          │
└─────────────────────────────────────────────────────────┘
```

## Postman Collections

The project includes comprehensive **Postman collections** for testing all API endpoints with pre-configured environments.

### 📦 Collection Files
Located in the `postman/` directory:
- **`Seek - Management.postman_collection.json`** - Main API collection
- **`Seek Local.postman_environment.json`** - Local development environment
- **`Seek Cloud.postman_environment.json`** - AWS EC2 production environment

### 🧪 Test Coverage
The collection includes **13 comprehensive tests** covering:

**🔐 Authentication Endpoints:**
- ✅ **201 - Register User** - Successful user registration
- ❌ **422 - Register User** - Validation errors
- ❌ **409 - Register User** - Duplicate user conflict
- ✅ **200 - Login** - Successful authentication
- ❌ **422 - Login** - Invalid credentials format
- ❌ **401 - Login** - Authentication failure

**👥 Client Management Endpoints:**
- ✅ **200 - Create Client** - Successful client creation
- ❌ **422 - Create Client** - Validation errors (age/birthdate mismatch)
- ❌ **400 - Create Client** - Bad request format
- ✅ **200 - Get Metrics** - Statistical analysis
- ✅ **200 - Get All Client** - Client listing with life expectancy calculations

### 🚀 How to Use

1. **Import Collections:**
   - Open Postman
   - Import `Seek - Management.postman_collection.json`
   - Import both environment files

2. **Select Environment:**
   - **For Local Testing:** Select `Seek Local` environment
   - **For Cloud Testing:** Select `Seek Cloud` environment

3. **Authentication Workflow:**
   ```
   1️⃣ Run "200 - Login" request
   2️⃣ Copy the JWT token from response
   3️⃣ Token is automatically stored in {{token_seek}} variable
   4️⃣ All protected endpoints use this token automatically
   ```

4. **Environment Variables:**
   ```bash
   # Local Environment
   protocol: http
   host: localhost
   port: 8080
   baseUrl: api

   # Cloud Environment  
   protocol: http
   host: 54.234.168.199
   port: 8080
   baseUrl: api
   ```

### 🎯 Pre-configured Test Data
Each request includes realistic test data:
- **User Registration:** Complete user profiles with validation
- **Client Creation:** Age-consistent client data (32 years old, born 1993-05-15)
- **Authentication:** Valid admin credentials
- **Error Scenarios:** Invalid data for testing error handling

### 🔄 Automated Testing
The collection supports:
- **Environment Switching** - Seamless local ↔ cloud testing
- **Token Management** - Automatic JWT handling
- **Error Validation** - Expected error responses for negative tests
- **Data Consistency** - Realistic test scenarios

**🏆 This implementation demonstrates enterprise-grade Spring Boot development with modern DevOps practices, comprehensive testing, and production-ready monitoring solutions.**
