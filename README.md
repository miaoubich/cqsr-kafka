# Store Application - CQRS Architecture with Spring Boot

## Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
- [Technologies Used](#technologies-used)
- [Services](#services)
  - [Producer Service - store-command](#producer-service---store-command)
  - [Consumer Service - store-query](#consumer-service---store-query)
- [Security](#security)
- [Database](#database)
- [Logging](#logging)
- [Exception Handling](#exception-handling)
- [Testing](#testing)
- [Installation](#installation)
- [Usage](#usage)
- [License](#license)

## Overview
This application demonstrates a microservices-based architecture using the CQRS (Command Query Responsibility Segregation) design pattern. It consists of two services: `store-command` for handling product commands and `store-query` for retrieving product data. The services communicate asynchronously using Apache Kafka.

## Architecture
- **CQRS Pattern**: Separates read and write operations into different services.
- **Database per Service**: Each service has its own dedicated database.
- **Asynchronous Communication**: Apache Kafka is used for event-driven communication between services.

## Technologies Used
- Spring Boot 3.5.4
- Java 17
- Apache Kafka
- MySQL Workbench
- Hibernate / JPA
- MapStruct
- SLF4J
- Keycloak OAuth2.0
- Records for DTOs
- JUnit 5
- Mockito
- Spring Boot Test

## Services

### Producer Service - store-command
Handles product creation, updates, deletion, and quantity reduction.

#### REST APIs
- `POST /api/v1/products` - Create a new product
- `PUT /api/v1/products/{productId}` - Update an existing product
- `DELETE /api/v1/products/{id}` - Delete a product
- `PATCH /api/v1/products/{id}/reduce?amount={amount}` - Reduce product quantity

#### Layers
- Controller Layer
- Service Layer
- Repository Layer

#### Database
- `productcmddb`

### Consumer Service - store-query
Handles product retrieval operations.

#### REST APIs
- `GET /api/v1/products/{id}` - Get product by ID
- `GET /api/v1/products` - Get all products

#### Layers
- Controller Layer
- Service Layer
- Repository Layer

#### Database
- `productquerydb`

## Security
- OAuth2.0 authentication and authorization using Keycloak.

## Database
- MySQL Workbench is used for managing databases.
- Each service has its own database to ensure separation of concerns.

## Logging
- SLF4J is used for logging across services.

## Exception Handling
- Custom exception handlers are implemented for better error management.

## Testing
The application includes comprehensive unit tests using Mockito and JUnit 5 for both services:

### Test Structure
- **Service Layer Tests**: Mock repository dependencies to test business logic
- **Controller Layer Tests**: Use MockMvc to test REST endpoints
- **Kafka Producer Tests**: Verify event publishing functionality
- **Exception Handling Tests**: Ensure proper error responses

### Test Coverage
- **store-command Service**:
  - ProductService unit tests for CRUD operations
  - ProductController integration tests
  - KafkaProducerService tests for event publishing
  - Exception handler tests

- **store-query Service**:
  - ProductQueryService unit tests for read operations
  - ProductQueryController integration tests
  - Event consumer tests

### Running Tests
```bash
# Run tests for store-command service
cd store-command
mvn test

# Run tests for store-query service
cd store-query
mvn test

# Run all tests with coverage
mvn clean test

## Installation
1. Clone the repository from GitHub.
2. Set up MySQL databases: `productcmddb` and `productquerydb`.
3. Configure Keycloak for OAuth2.0 security.
4. Start Apache Kafka server.
5. Build and run both services using Maven or your preferred IDE.

## Usage
- Use the REST APIs provided by each service to interact with products.
- Producer service handles commands (create, update, delete, reduce).
- Consumer service handles queries (get by ID, get all).
- API Gateway and Service Discovery
- This application uses an API Gateway as a single entry point to route requests to the appropriate microservices. The gateway handles routing, load balancing, and security enforcement.
- All services are registered with a Eureka Discovery Server, enabling dynamic discovery and communication between services without hardcoded URLs.
- API Gateway: Centralized access point for all client requests
- Eureka Server: Service registry for dynamic service discovery

## License
This project is free of use.
