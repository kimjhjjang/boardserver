# Board Server

A RESTful API server for managing bulletin boards, posts, and users.

## Features

- User management (create, read, delete)
- Board/category management (create, read, update, delete)
- Post management (create, read, update, delete)
- View count tracking for posts
- Query posts by board or user

## Technology Stack

- Java 17
- Spring Boot 3.1.5
- Spring Data JPA
- H2 Database (in-memory)
- Maven
- Lombok

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Building the Project

```bash
mvn clean install
```

### Running the Application

```bash
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

## API Endpoints

### Users

- `POST /api/users` - Create a new user
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `DELETE /api/users/{id}` - Delete user

### Boards

- `POST /api/boards` - Create a new board
- `GET /api/boards` - Get all boards
- `GET /api/boards/{id}` - Get board by ID
- `PUT /api/boards/{id}` - Update board
- `DELETE /api/boards/{id}` - Delete board

### Posts

- `POST /api/posts` - Create a new post
- `GET /api/posts` - Get all posts
- `GET /api/posts/{id}` - Get post by ID (increments view count)
- `GET /api/posts/board/{boardId}` - Get posts by board
- `GET /api/posts/user/{userId}` - Get posts by user
- `PUT /api/posts/{id}` - Update post
- `DELETE /api/posts/{id}` - Delete post

## Example Usage

### Create a User

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123",
    "email": "john@example.com"
  }'
```

### Create a Board

```bash
curl -X POST http://localhost:8080/api/boards \
  -H "Content-Type: application/json" \
  -d '{
    "name": "General Discussion",
    "description": "A place for general discussion"
  }'
```

### Create a Post

```bash
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My First Post",
    "content": "This is the content of my first post",
    "userId": 1,
    "boardId": 1
  }'
```

## Database Console

The H2 console is available at `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:boarddb`
- Username: `sa`
- Password: (leave empty)

## Testing

```bash
mvn test
```
