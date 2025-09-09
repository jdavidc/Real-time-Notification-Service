# Real-time Notification Service

A scalable, real-time notification service built with Spring Boot and WebSockets, designed to handle notifications across multiple channels with a focus on performance and reliability.

## ✨ Features

- **Real-time Notifications**: Instant push notifications using WebSockets
- **Multiple Notification Types**: Support for different notification types (INFO, WARNING, ERROR, SUCCESS)
- **User-specific Notifications**: Send notifications to specific users
- **Status Tracking**: Track notification status (UNREAD, READ, ARCHIVED)
- **RESTful API**: Comprehensive API for managing notifications
- **WebSocket Support**: Real-time event streaming to connected clients
- **Database Integration**: Built with Spring Data JPA, supporting both H2 (in-memory) and PostgreSQL
- **Input Validation**: Robust request validation
- **Global Exception Handling**: Consistent error responses
- **Actuator Endpoints**: Built-in monitoring and management

## 🚀 Tech Stack

- **Java 17**
- **Spring Boot 3.5.5**
- **Spring Web**
- **Spring WebSocket**
- **Spring Data JPA**
- **PostgreSQL** / **H2** (for development)
- **Lombok**
- **Spring Boot Actuator**
- **JUnit 5**

## 📦 Prerequisites

- JDK 17 or higher
- Maven or Gradle (Gradle Wrapper included)
- PostgreSQL (for production) or H2 (for development)

## 🛠️ Setup & Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/Real-time-Notification-Service.git
   cd Real-time-Notification-Service
   ```

2. **Configure the database**
   - For development (using H2 in-memory database):
     - No additional setup required
   - For production (using PostgreSQL):
     - Create a PostgreSQL database
     - Update `application.yml` with your database credentials

3. **Build the application**
   ```bash
   ./gradlew build
   ```

4. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

   The application will start on `http://localhost:8080`

## 🌐 API Endpoints

### Notifications

- `GET /notifications` - Get all notifications for the current user
  - Query Params: `userId` (optional, defaults to 'test-user')
  
- `GET /notifications/unread/count` - Get count of unread notifications
  - Query Params: `userId` (optional, defaults to 'test-user')
  
- `POST /notifications` - Create a new notification
  - Request Body: NotificationDto (JSON)
  
- `PUT /notifications/{id}/read` - Mark a notification as read
  - Path Variable: `id` (notification ID)
  
- `DELETE /notifications/{id}` - Delete a notification
  - Path Variable: `id` (notification ID)

### WebSocket Endpoints

- **WebSocket Connection**: `ws://localhost:8080/ws`
- **Subscribe to notifications**: `/topic/notifications/{userId}`
- **Send notification**: `/app/notifications/send`

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
