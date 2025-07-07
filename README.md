# Task Management API

A task management service built with Dropwizard 2.1.x, designed for testing modernization tools.

## Features

- **Dropwizard 2.1.x** framework
- **MySQL** database with raw SQL migrations  
- **Dual authentication** (Basic Auth + API Keys)
- **JDBI + Raw JDBC** mixed database access
- **JUnit 4** test suite
- **Gradle 7.6** build system
- **Log4j2** logging configuration
- **Minimal Swagger** documentation
- **Java 14** runtime

## Prerequisites

- Java 14 or higher
- Gradle 7.6
- MySQL 8.0+

## Quick Start

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd task-management-api
   ```

2. **Setup MySQL database**
   ```sql
   CREATE DATABASE tasks;
   CREATE USER tasks WITH PASSWORD 'tasks123';
   GRANT ALL PRIVILEGES ON DATABASE tasks TO tasks;
   ```

3. **Run database migrations**
   ```bash
   ./gradlew flywayMigrate
   ```

4. **Build the application**
   ```bash
   ./gradlew build
   ```

5. **Run the application**
   ```bash
   java -jar build/libs/task-management-api-*.jar server src/main/resources/config.yml
   ```

6. **Access the application**
   - API: http://localhost:8080/tasks
   - Admin: http://localhost:8080/admin

## Authentication

The API supports dual authentication methods:

### Basic Authentication
```bash
curl -u "admin:password" http://localhost:8080/tasks
```

### API Key Authentication  
```bash
curl -u "task-api-key-123:" http://localhost:8080/tasks
```

## API Endpoints

### Tasks API
- `GET /tasks` - List all tasks
- `GET /tasks/{id}` - Get specific task
- `POST /tasks` - Create new task
- `PUT /tasks/{id}` - Update existing task
- `DELETE /tasks/{id}` - Delete task
- `GET /tasks/user/{userId}` - Get tasks by user

### Example Request
```bash
curl -X POST http://localhost:8080/tasks \
  -u "admin:password" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Complete project",
    "description": "Finish the task management API",
    "priority": "high",
    "assigneeId": "user123"
  }'
```

## Testing

Run the test suite:
```bash
./gradlew test
```

## Development Notes

This application uses intentionally outdated patterns for testing purposes:
- Dropwizard 2.1.x (could be updated to newer version)
- JUnit 4 (could be migrated to JUnit 5)  
- Mixed database access patterns (JDBI + raw JDBC)
- Older Swagger documentation (1.6.x)
- Log4j2 configuration
- Gradle 7.6 (older version)

## Health Check

Check application health:
```bash
curl http://localhost:8080/admin/healthcheck
```