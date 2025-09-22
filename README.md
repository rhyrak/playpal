# 🎮 PlayPal – Social Platform API for Gamers

PlayPal is a social platform API where gamers can find teammates.
Users can create posts showing which game they play and add details.

## ⚡ Quickstart

Clone the repository and start the application with Docker:

```bash
git clone https://www.github.com/rhyrak/playpal.git && cd playpal
mv .env.example .env
docker compose up
```

The OpenAPI definition will be available at:
👉 http://localhost:8080/swagger-ui/index.html

Default admin user e-mail: admin@playpal.com pw: admin123

## 🚀 Features

- 🔐 Authentication: User registration & login with Spring Security + JWT
- 👥 Roles:
    - User → Create, and manage their own posts, view other user's posts
    - Admin → Manage the game catalog (add/edit/delete games)
- 🧪 Testing:
    - Unit tests for services and utilities
    - Integration tests for REST APIs and database interactions
- 🐳 Dockerized for easy deployment

## 🛠 Tech Stack

- Java 21
- JWT Authentication
- Spring Boot (Web, Security, Data JPA)
- PostgreSQL
- Testcontainers
- Docker

## ⚡ API Endpoints

### 🔑 Authentication

| Method | Endpoint                | Description         |
|--------|-------------------------|---------------------|
| POST   | `/api/v1/auth/register` | Register a new user |
| POST   | `/api/v1/auth/refresh`  | Refresh tokens      |
| POST   | `/api/v1/auth/logout`   | Logout user         |
| POST   | `/api/v1/auth/login`    | Login user          |

### Posts

| Method | Endpoint             | Description   |
|--------|----------------------|---------------|
| GET    | `/api/v1/posts`      | Get all posts |
| POST   | `/api/v1/posts`      | Create a post |
| GET    | `/api/v1/posts/{id}` | Get a post    |
| DELETE | `/api/v1/posts/{id}` | Delete a post |

### 🎮 Games

| Method | Endpoint             | Description         |
|--------|----------------------|---------------------|
| GET    | `/api/v1/games/{id}` | Get game by ID      |
| PUT    | `/api/v1/games/{id}` | Update a game by ID |
| DELETE | `/api/v1/games/{id}` | Delete a game       |
| GET    | `/api/v1/games`      | Get all games       |
| POST   | `/api/v1/games`      | Create a new game   |

