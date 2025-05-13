# Library Management System

![Java](https://img.shields.io/badge/Java-21-yellow)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-green)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-red)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16.8-blue)
![Querydsl](https://img.shields.io/badge/Querydsl-5.1.0-orange)
![OpenAPI](https://img.shields.io/badge/OpenAPI-2.8.5-brightgreen)

## Overview

The Library Management System is a comprehensive application. This modern solution provides robust book management, user management, and borrowing/returning processes with role-based access control.

## 📌 Features

- **Book Management**: Add, view, update, and delete books with detailed information
- **User Management**: Register, view, update, and delete users with different roles
- **Borrowing and Returning**: Manage book borrowing, returns, and track due dates
- **Search Functionality**: Search for books by title, author, ISBN, or genre with pagination
- **Overdue Management**: Track overdue books and generate reports
- **Role-Based Access Control**
- **Authentication**: Secure JWT-based authentication and authorization

## 🛠 Technology Stack

| Component | Version |
|-----------|---------|
| Java | 21 |
| Spring Boot | 3.4.5 |
| Spring Security | JWT Authentication |
| PostgreSQL | 16.8 |
| Spring Data JPA | - |
| Querydsl | 5.1.0 |
| OpenAPI (Swagger) | 2.8.5 |
| Maven | - |
| Docker | - |

## 📂 Project Structure

```
src/
├── main/
│   ├── java/com/alisimsek/librarymanagement/
│   │   ├── auth/               # Authentication controllers and DTOs
│   │   ├── book/               # Book management
│   │   │   ├── controller/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   ├── borrow/             # Borrowing and returning functionality
│   │   │   ├── controller/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   ├── common/             # Common utilities and base classes
│   │   ├── config/             # Application configuration
│   │   ├── initializer/        # Data initialization
│   │   ├── security/           # Security configuration and JWT handling
│   │   ├── user/               # User management
│   │   │   ├── controller/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── LibraryManagementApplication.java
│   │
│   └── resources/
│       ├── application.yml     # Application configuration
│       └── exceptions.properties
│
├── test/                       # Test cases
│   └── java/com/alisimsek/librarymanagement/
│
└── docker/
    └── local/
        └── docker-compose.yml  # Local development environment
```

## ⚙️ Running the Application

### Prerequisites

- Java 21 or higher
- Maven
- Docker (optional)

### Using Docker

```bash
cd docker/local
docker-compose up -d
```

Then run the application:

```bash
mvn spring-boot:run
```

### Manual Setup

1. Set up a PostgreSQL database
2. Update application.yml with your database credentials
3. Run the application:

```bash
mvn spring-boot:run
```

## 👤 Default Users

The system initializes with the following default users:

| Username | Password | User Type | Permissions |
|----------|----------|-----------|------------|
| admin@mail.com | Aa123456 | ADMIN | All permissions |
| librarian@mail.com | Aa123456 | LIBRARIAN | Book management, borrowing management |
| patron@mail.com | Aa123456 | PATRON | View books, borrow/return books |

## 📡 API Documentation

API documentation is available via Swagger UI at `/swagger-ui/index.html` when the application is running.

## 💡 Business Rules

### Book Management
- Books must have a unique ISBN
- Books can be searched by title, author, ISBN, or genre
- Only librarians and admins can add, update, or delete books

### Borrowing Rules
- Patrons can borrow available books
- Borrowed books are marked as unavailable
- Borrowing period is between 1 and 30 days
- Books must be returned by the due date
- Overdue books are tracked and flagged

### User Management
- Users can have one of three roles: ADMIN, LIBRARIAN, or PATRON
- User authentication is handled via JWT tokens