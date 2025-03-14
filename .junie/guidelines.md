# EAT-SSU Project Guidelines

## Project Overview

EAT-SSU is a mobile application designed for Soongsil University students to review and compare cafeteria menus across campus. The app aims to enhance the dining experience by providing comprehensive information about various dining options, including menu reviews, operating hours, and locations.

### Key Features

- View and compare menus from all campus dining facilities:
  - Student Cafeteria (학생식당)
  - Dodam Cafeteria (도담식당)
  - Food Court (푸드코트)
  - Snack Corner (스낵코너)
  - Dormitory Cafeteria (기숙사 식당)
- Write and read reviews for meals
- Check restaurant operating hours and locations
- User authentication system

## Technical Stack

### Backend
- **Framework**: Spring Boot 3.0.4
- **Language**: Java 17
- **Database**: MySQL (Production), H2 (Testing)
- **ORM**: Spring Data JPA with QueryDSL
- **Security**: Spring Security with JWT
- **API Documentation**: OpenAPI (Springdoc)
- **Build Tool**: Gradle
- **Cloud Storage**: AWS S3
- **Notifications**: Slack API integration

### Mobile Applications
- Available on both iOS (App Store) and Android (Play Store)

## Project Structure

The project follows a domain-driven design approach with the following structure:

```
src/main/java/ssu/eatssu/
├── domain/
│   ├── menu/
│   │   ├── entity/
│   │   ├── persistence/
│   │   ├── presentation/
│   │   │   ├── dto/
│   │   │   ├── rest/
│   │   └── service/
│   ├── restaurant/
│   │   ├── entity/
│   │   ├── persistence/
│   │   ├── presentation/
│   │   └── service/
│   └── [other domains]
├── global/
│   ├── config/
│   ├── error/
│   └── security/
└── [other packages]
```

## Development Guidelines

### Code Style
- Follow Java coding conventions
- Use meaningful variable and method names
- Write comprehensive comments for complex logic
- Create unit tests for all new features

### Git Workflow
- Create feature branches from the main branch
- Use descriptive commit messages
- Submit pull requests for code review before merging
- Keep commits focused and atomic

### Security
- Never commit sensitive information (API keys, credentials)
- The `application.yml` file is shared only within the team
- Always validate user input
- Follow the principle of least privilege

### Testing
- Write unit tests for all services
- Use integration tests for API endpoints
- Maintain test coverage for critical components

## Deployment

The application is deployed as a Docker container. The build process creates a JAR file named `eat-ssu.jar`.

## Contact

For more information about the project, visit the [project page](https://hi-jin-1514.notion.site/EAT-SSU-b04aaec9b7814a628c6ef6b3e08c74a3).