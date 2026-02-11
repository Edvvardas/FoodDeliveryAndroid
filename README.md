# Food Delivery App

A full-stack food delivery application with an Android client and a Spring Boot REST API backend.

## Project Structure

```
├── android/    — Android mobile client
└── backend/    — Spring Boot REST API server
```

## Backend

Spring Boot 4.0 REST API that provides the server-side logic and data persistence.

### Tech Stack
- Java 21
- Spring Boot 4.0 (Web MVC, Data JPA, HATEOAS)
- MySQL
- Lombok
- Gson

### API Endpoints

| Area | Description |
|------|-------------|
| **Users** | Registration, login/validation, profile management for customers and drivers |
| **Restaurants & Menus** | CRUD operations for restaurants and cuisines |
| **Orders** | Create orders with multiple items, track order status, view order history |
| **Drivers** | Accept and complete deliveries, view available orders |
| **Chat** | In-order messaging between customers and drivers |
| **Reviews** | Create and browse restaurant reviews |

### Setup

1. Install Java 21 and MySQL
2. Create a MySQL database (auto-created as `kursinisMaybe` if it doesn't exist)
3. Configure database credentials in `backend/src/main/resources/application.properties`
4. Run the backend:
   ```bash
   cd backend
   ./gradlew bootRun
   ```

## Android Client

Native Android app that communicates with the backend REST API.

### Tech Stack
- Java 11
- Android SDK 36 (min SDK 30)
- Material Design Components
- Gson for JSON serialization

### Features
- User registration and login (customer / driver roles)
- Browse restaurants and menus
- Place food orders with multiple items
- Track order status
- Driver order management (accept, pick up, deliver)
- In-order chat between customer and driver
- Restaurant reviews and ratings

### Setup

1. Open the `android/` directory in Android Studio
2. Update the server URL in `Constants.java` to point to your backend instance
3. Build and run on a device or emulator (API 30+)

## Running the Full Stack

1. Start MySQL
2. Start the backend (`./gradlew bootRun` in `backend/`)
3. Update `Constants.java` in the Android app with the backend server IP
4. Run the Android app from Android Studio
