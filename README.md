# Spring Boot Ecommerce Project

## Overview
A RESTful e-commerce backend built with Spring Boot. Currently, it provides functionality for managing product categories with support for pagination, sorting, and validation.

## Tech Stack
- **Language:** Java 21
- **Framework:** Spring Boot 4.0.1
- **Build Tool:** Maven
- **Database:** H2 (In-memory)
- **Mapping:** ModelMapper
- **Utilities:** Lombok, Spring Data JPA, Spring Validation

## Requirements
- JDK 21 or higher
- Maven (optional, wrapper provided)

## Setup and Run
1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd springboot-ecomm
   ```
2. **Build the project:**
   ```bash
   ./mvnw clean install
   ```
3. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   The server will start at `http://localhost:8080`.

## Scripts
- `./mvnw clean install`: Build and install dependencies.
- `./mvnw spring-boot:run`: Launch the application.
- `./mvnw test`: Run unit and integration tests.

## API Endpoints (Current)
- `GET /api/public/categories`: Fetch all categories (paginated).
- `POST /api/public/categories`: Create a new category.
- `PUT /api/admin/categories/{categoryId}`: Update an existing category.
- `DELETE /api/admin/categories/{categoryId}`: Remove a category.

## Database Console
Access the H2 Console at `http://localhost:8080/h2-console` with:
- **JDBC URL:** `jdbc:h2:mem:test`
- **Username:** `sa` (default)
- **Password:** (leave empty)

## Environment Variables
- TODO: List any externalized configuration variables (currently using defaults in `application.yaml`).

## Tests
Run tests using:
```bash
./mvnw test
```

## Project Structure
```text
springboot-ecomm/
├── src/
│   ├── main/
│   │   ├── java/com/ecommerce/project/
│   │   │   ├── config/          # Configuration classes (AppConfig, AppConstants)
│   │   │   ├── controller/      # REST Controllers
│   │   │   ├── exceptions/      # Global Exception Handling
│   │   │   ├── model/           # JPA Entities
│   │   │   ├── payload/         # DTOs (Data Transfer Objects)
│   │   │   ├── repository/      # Spring Data Repositories
│   │   │   ├── service/         # Business Logic Layer
│   │   │   └── SpringbootEcommApplication.java # Main Entry Point
│   │   └── resources/
│   │       └── application.yaml # Application Configuration
│   └── test/                    # Unit and Integration Tests
├── pom.xml                      # Maven Configuration
└── mvnw                         # Maven Wrapper
```

## License
- TODO: Add license information.

