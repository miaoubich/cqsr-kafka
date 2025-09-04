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

## License
This project is licensed under the MIT License.
