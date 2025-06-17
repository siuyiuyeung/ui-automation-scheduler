# UI Automation Scheduler

A comprehensive Spring Boot application for scheduling and executing web UI automation tasks using Selenium WebDriver. This application provides a user-friendly web interface for creating, managing, and monitoring automated web interactions with built-in scheduling capabilities.

## 🚀 Features

- **Web-based Dashboard**: Intuitive interface for managing automation configurations
- **Flexible Scheduling**: Support for cron expressions, intervals, and one-time executions
- **Multiple Step Types**: Navigate, Click, Input, Wait, Screenshot, Scroll, and Select operations
- **Screenshot Capture**: Automatic screenshot capture during automation execution
- **Execution History**: Comprehensive tracking of automation runs with detailed logs
- **Real-time Monitoring**: Live status updates and execution progress tracking
- **REST API**: Full API support for programmatic access and integration
- **Database Persistence**: H2 (development) and PostgreSQL (production) support

## 🛠️ Technology Stack

- **Backend**: Java 24, Spring Boot 3.5.0
- **Web Automation**: Selenium WebDriver 4.20.0, WebDriverManager 5.8.0
- **Frontend**: Thymeleaf, Bootstrap 5.3.0, JavaScript
- **Database**: H2 (embedded), PostgreSQL (production)
- **Build Tool**: Maven
- **Additional**: Lombok, Jackson, Spring Data JPA

## 📋 Prerequisites

- **Java 24** or higher
- **Maven 3.6+**
- **Chrome/Firefox browser** (for Selenium WebDriver)
- **Git** (for cloning the repository)

## 🔧 Installation

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/ui-automation-scheduler.git
cd ui-automation-scheduler
```

### 2. Build the Application
```bash
mvn clean package
```

### 3. Run the Application
```bash
mvn spring-boot:run
```

### 4. Access the Application
- **Web UI**: http://localhost:8080
- **H2 Database Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:./data/automation`
  - Username: `sa`
  - Password: (leave empty)

## 📖 Usage Guide

### Creating an Automation Configuration

1. **Access the Dashboard**: Navigate to http://localhost:8080
2. **Create New Configuration**: Click "Create New" button
3. **Configure Basic Settings**:
   - Name: Descriptive name for your automation
   - Description: Optional description
   - Active: Enable/disable the configuration

4. **Add Automation Steps**:
   - **NAVIGATE**: Navigate to a specific URL
   - **CLICK**: Click on an element using CSS selector
   - **INPUT**: Enter text into form fields
   - **WAIT**: Pause execution for specified seconds
   - **SCREENSHOT**: Capture screenshot of page or specific element
   - **SCROLL**: Scroll page or element
   - **SELECT**: Select option from dropdown

5. **Configure Scheduling** (Optional):
   - **Cron Expression**: Use cron syntax for complex scheduling
   - **Interval**: Run every X seconds/minutes/hours
   - **One-time**: Execute once at specified time

### Example Automation: Login Flow
```
Step 1: NAVIGATE to https://example.com/login
Step 2: INPUT "username" in selector "#username"
Step 3: INPUT "password" in selector "#password"
Step 4: CLICK on selector "#login-button"
Step 5: WAIT for 3 seconds
Step 6: SCREENSHOT with selector ".dashboard"
```

### Viewing Results

1. **History Tab**: View all automation executions
2. **Filter Results**: Filter by status, configuration, or date range
3. **Detailed View**: Click "Details" to see:
   - Execution logs
   - Screenshots captured
   - Error messages (if any)
   - Execution timeline

## 📁 Project Structure

```
ui-automation-scheduler/
├── src/main/java/com/automation/
│   ├── UiAutomationApplication.java          # Main Spring Boot application
│   ├── config/                               # Configuration classes
│   │   ├── SchedulerConfig.java             # Task scheduler configuration
│   │   ├── SeleniumConfig.java              # WebDriver configuration
│   │   └── WebConfig.java                   # Web MVC configuration
│   ├── controller/                          # REST controllers
│   │   ├── AutomationController.java        # Automation management API
│   │   ├── HistoryController.java           # Execution history API
│   │   └── WebController.java               # Web page controllers
│   ├── model/                               # JPA entities
│   │   ├── AutomationConfig.java            # Automation configuration
│   │   ├── AutomationStep.java              # Individual automation steps
│   │   ├── AutomationResult.java            # Execution results
│   │   └── ScheduleConfig.java              # Scheduling configuration
│   ├── service/                             # Business logic
│   │   ├── AutomationService.java           # Core automation execution
│   │   ├── SchedulerService.java            # Task scheduling management
│   │   ├── WebDriverService.java            # WebDriver management
│   │   └── ConfigurationService.java        # Configuration management
│   ├── repository/                          # Data access layer
│   └── dto/                                 # Data transfer objects
├── src/main/resources/
│   ├── application.yml                      # Application configuration
│   ├── static/                              # Static web resources
│   │   ├── css/style.css                   # Custom styles
│   │   ├── js/app.js                       # Frontend JavaScript
│   │   └── index.html                      # Single-page application
│   └── templates/                           # Thymeleaf templates
│       ├── dashboard.html                   # Main dashboard
│       ├── config.html                     # Configuration details
│       ├── history.html                    # Execution history
│       └── result-details.html             # Result details view
├── screenshots/                             # Generated screenshots
├── data/                                    # H2 database files
└── pom.xml                                  # Maven configuration
```

## ⚙️ Configuration

### Application Settings (application.yml)

```yaml
spring:
  application:
    name: UI Automation Scheduler
  datasource:
    url: jdbc:h2:file:./data/automation
    driver-class-name: org.h2.Driver
    username: sa
    password:

automation:
  screenshot:
    path: ./screenshots
  driver:
    headless: false
    timeout: 30
```

### Environment Variables

- `SPRING_PROFILES_ACTIVE`: Set to `prod` for production configuration
- `DATABASE_URL`: PostgreSQL connection string for production
- `AUTOMATION_HEADLESS`: Set to `true` for headless browser execution

### Database Configuration

**Development (H2)**:
- Embedded database stored in `./data/automation.mv.db`
- Automatic schema creation and updates

**Production (PostgreSQL)**:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/automation
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
```

## 🔌 API Documentation

### Automation Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/automation/configs` | List all configurations |
| GET | `/api/automation/configs/{id}` | Get specific configuration |
| POST | `/api/automation/configs` | Create new configuration |
| PUT | `/api/automation/configs/{id}` | Update configuration |
| DELETE | `/api/automation/configs/{id}` | Delete configuration |
| POST | `/api/automation/configs/{id}/run` | Execute configuration immediately |
| POST | `/api/automation/configs/{id}/toggle` | Toggle active status |

### Execution History

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/history` | List execution history (paginated) |
| GET | `/api/history/{id}` | Get specific execution result |
| GET | `/api/history/{id}/screenshot/{index}` | Get screenshot by index |

### Request/Response Examples

**Create Configuration**:
```json
POST /api/automation/configs
{
  "name": "Login Test",
  "description": "Automated login flow",
  "active": true,
  "steps": [
    {
      "order": 1,
      "type": "NAVIGATE",
      "value": "https://example.com/login"
    },
    {
      "order": 2,
      "type": "INPUT",
      "selector": "#username",
      "value": "testuser"
    }
  ],
  "schedule": {
    "type": "CRON",
    "cronExpression": "0 0 9 * * MON-FRI"
  }
}
```

## 🤝 Contributing

### Development Setup

1. **Fork the repository**
2. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Set up development environment**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Run tests**:
   ```bash
   mvn test
   ```

### Code Style Guidelines

- Follow Java naming conventions
- Use Lombok annotations for boilerplate code
- Write comprehensive JavaDoc for public methods
- Include unit tests for new features
- Ensure all tests pass before submitting PR

### Submitting Changes

1. **Commit your changes**:
   ```bash
   git commit -m "Add: Brief description of changes"
   ```

2. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```

3. **Create a Pull Request** with:
   - Clear description of changes
   - Screenshots for UI changes
   - Test coverage information

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🐛 Troubleshooting

### Common Issues

**WebDriver Issues**:
- Ensure Chrome/Firefox is installed
- WebDriverManager automatically downloads drivers
- Check browser compatibility with Selenium version

**Database Issues**:
- H2 database files are created automatically
- Check file permissions in `./data/` directory
- Use H2 console for database inspection

**Scheduling Issues**:
- Validate cron expressions using online tools
- Check application logs for scheduling errors
- Ensure system time is correct

### Getting Help

- **Issues**: Report bugs via GitHub Issues
- **Discussions**: Use GitHub Discussions for questions
- **Documentation**: Check the `/docs` folder for additional guides

## 🚀 Deployment

### Docker Deployment (Recommended)

```dockerfile
FROM openjdk:24-jdk-slim
COPY target/ui-automation-scheduler-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Production Considerations

- Use PostgreSQL for production database
- Enable headless mode for server environments
- Configure proper logging levels
- Set up monitoring and alerting
- Use reverse proxy (nginx) for SSL termination

---

**Built with ❤️ using Spring Boot and Selenium WebDriver**
