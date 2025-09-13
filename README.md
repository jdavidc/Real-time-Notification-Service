# Real-time Notification Service

A scalable, real-time notification service built with Spring Boot and WebSockets, designed to handle notifications across multiple channels with a focus on performance and reliability. This service provides both REST API and WebSocket interfaces for flexible integration with various clients.

## ✨ Features

### Core Features
- **Real-time Notifications**: Instant push notifications using WebSockets (STOMP)
- **Multiple Notification Types**: Support for different notification types (INFO, WARNING, ERROR, SUCCESS)
- **User-specific Notifications**: Target notifications to specific user sessions
- **Status Management**: Track notification status (UNREAD, READ, ARCHIVED)
- **Pagination Support**: Efficiently retrieve large sets of notifications

### Technical Features
- **RESTful API**: Versioned API (v1, v2) with comprehensive CRUD operations
- **WebSocket Support**: Real-time event streaming using STOMP over WebSockets
- **Database Integration**: Built with Spring Data JPA
  - H2 (in-memory) for development
  - Configurable for PostgreSQL in production
- **Input Validation**: Robust request validation using Bean Validation
- **Global Exception Handling**: Consistent error responses with proper HTTP status codes
- **Actuator Endpoints**: Built-in monitoring and management endpoints
- **Pagination & Sorting**: Support for paginated results with custom sorting
- **Comprehensive Testing**: Unit and integration tests with JUnit 5 and MockMvc

## 🚀 Tech Stack

- **Java 17**
- **Spring Boot 3.5.5**
- **Spring Web**
- **Spring WebSocket** (STOMP)
- **Spring Data JPA**
- **H2 Database** (in-memory for development)
- **PostgreSQL** (production-ready configuration)
- **Lombok**
- **Spring Boot Actuator**
- **JUnit 5** & **MockMvc**
- **Validation API**
- **Spring Test**

## 📦 Prerequisites

- JDK 17 or higher
- Gradle 7.6+ (Gradle Wrapper included)
- (Optional) PostgreSQL 13+ for production

## 🛠️ Setup & Installation

### Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/Real-time-Notification-Service.git
   cd Real-time-Notification-Service
   ```

2. **Run the application**
   ```bash
   ./gradlew bootRun
   ```
   The application starts on `http://localhost:8080`

3. **Access H2 Console** (Development only)
   - URL: `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:notificationdb`
   - Username: `sa`
   - Password: `password`

### Production Setup

1. **Configure PostgreSQL**
   - Create a PostgreSQL database
   - Update `application.yml` or set environment variables:
     ```yaml
     spring:
       datasource:
         url: jdbc:postgresql://localhost:5432/your_database
         username: your_username
         password: your_password
     ```

2. **Build and Run**
   ```bash
   ./gradlew build
   java -jar build/libs/notifications-*.jar
   ```

## 🌐 API Documentation

### Base URL
All API endpoints are prefixed with `/api`

### API v2 (Current)
**Base Path**: `/v2/notifications`

#### Notification Object
```json
{
  "id": 1,
  "title": "System Update",
  "message": "Scheduled maintenance tonight at 2 AM",
  "recipientId": "user123",
  "type": "INFO",
  "status": "UNREAD",
  "createdAt": "2023-10-15T14:30:00Z",
  "updatedAt": "2023-10-15T14:30:00Z"
}
```

#### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET`  | `/` | Get paginated notifications |
| `GET`  | `/{id}` | Get notification by ID |
| `GET`  | `/unread/count` | Get count of unread notifications |
| `POST` | `/` | Create a new notification |
| `PATCH`| `/{id}/read` | Mark notification as read |
| `DELETE`| `/{id}` | Delete a notification |

#### Examples

**Get Notifications**
```http
GET /api/v2/notifications?userId=user123&page=0&size=10&sort=createdAt,desc
```

**Create Notification**
```http
POST /api/v2/notifications
Content-Type: application/json

{
  "title": "Welcome",
  "message": "Welcome to our service!",
  "recipientId": "user123",
  "type": "INFO"
}
```

### WebSocket Configuration

#### Endpoints
- **WebSocket Endpoint**: `/ws`
- **Application Destination Prefix**: `/app`
- **Topic Prefix**: `/topic`
- **User Destination Prefix**: `/user`

#### Subscribing to Notifications
```javascript
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function(frame) {
    // Subscribe to user-specific notifications
    stompClient.subscribe(`/topic/notifications/{userId}`, function(message) {
        const notification = JSON.parse(message.body);
        console.log('Received notification:', notification);
    });
});
```

### API v1 (Deprecated)
**Base Path**: `/v1/notifications`
> ⚠️ This version is deprecated and will be removed in v3.0.0. Please migrate to v2.

- `GET /` - Get all notifications for a user (not paginated)
  - Query Params: `userId` (optional, defaults to 'test-user')
  
- `POST /` - Create a new notification
  - Request Body: NotificationDto (JSON)

## 🧪 Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests with coverage report
./gradlew test jacocoTestReport
```

### Test Structure
- Unit tests are located in `src/test/java`
- Integration tests use `@SpringBootTest`
- Test data is managed via `TestDataFactory`

## 🚀 Deployment

### Docker

1. **Build the Docker image**
   ```bash
   docker build -t notification-service .
   ```

2. **Run the container**
   ```bash
   docker run -d -p 8080:8080 --name notification-service notification-service
   ```

### Kubernetes

Example deployment configuration:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: notification-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: notification-service
  template:
    metadata:
      labels:
        app: notification-service
    spec:
      containers:
      - name: notification-service
        image: notification-service:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
```

## 📊 Monitoring

The service includes Spring Boot Actuator endpoints for monitoring:

- Health: `/actuator/health`
- Metrics: `/actuator/metrics`
- Info: `/actuator/info`
- Env: `/actuator/env`

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style
- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- Use meaningful commit messages
- Write tests for new features
- Update documentation when necessary

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot)
- [WebSocket](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket)
- [H2 Database](https://www.h2database.com/)
- [PostgreSQL](https://www.postgresql.org/)
- **Subscribe to user notifications**: 
  - Destination: `/user/queue/notifications`
- **Send notification via WebSocket**: 
  - Destination: `/app/notification`
  - Body: NotificationDto (JSON)

## 📝 Example Usage

### Creating a Notification

```http
POST /notifications
Content-Type: application/json

{
  "title": "New Message",
  "message": "You have a new message from John",
  "recipientId": "user123",
  "type": "INFO"
}
```

### Subscribing to Notifications (WebSocket)

1. Connect to WebSocket endpoint:
   ```javascript
   const socket = new SockJS('http://localhost:8080/ws');
   const stompClient = Stomp.over(socket);
   
   stompClient.connect({}, function(frame) {
       console.log('Connected: ' + frame);
       
       // Subscribe to notifications for a specific user
       stompClient.subscribe('/topic/notifications/user123', function(notification) {
           console.log('Received notification: ' + notification.body);
       });
   });
   ```

## 🧪 Testing

Run the test suite with:
```bash
./gradlew test
```

## 🔧 Configuration

Configuration can be managed through `application.yml` or environment variables. Key configurations include:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/notification_db
    username: your_username
    password: your_password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

# WebSocket configuration
websocket:
  endpoint: /ws
  app-destination-prefix: /app
  topic-prefix: /topic

# Actuator endpoints
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
```

## 📊 Monitoring

The service includes Spring Boot Actuator endpoints for monitoring:

- `/actuator/health` - Application health information
- `/actuator/info` - Application information
- `/actuator/metrics` - Application metrics

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Built with [Spring Boot](https://spring.io/projects/spring-boot)
- Uses [WebSocket](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket) for real-time communication
- Inspired by modern microservice architectures
