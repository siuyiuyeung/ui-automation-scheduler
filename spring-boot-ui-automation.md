# Spring Boot UI Automation Project

## Project Structure
```
ui-automation-scheduler/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── automation/
│       │           ├── UiAutomationApplication.java
│       │           ├── config/
│       │           │   ├── WebConfig.java
│       │           │   ├── SchedulerConfig.java
│       │           │   └── SeleniumConfig.java
│       │           ├── controller/
│       │           │   ├── AutomationController.java
│       │           │   └── HistoryController.java
│       │           ├── model/
│       │           │   ├── AutomationConfig.java
│       │           │   ├── AutomationStep.java
│       │           │   ├── AutomationResult.java
│       │           │   └── ScheduleConfig.java
│       │           ├── repository/
│       │           │   ├── AutomationConfigRepository.java
│       │           │   └── AutomationResultRepository.java
│       │           ├── service/
│       │           │   ├── AutomationService.java
│       │           │   ├── SchedulerService.java
│       │           │   └── WebDriverService.java
│       │           └── dto/
│       │               ├── AutomationConfigDTO.java
│       │               └── AutomationResultDTO.java
│       └── resources/
│           ├── application.yml
│           ├── static/
│           │   ├── css/
│           │   ├── js/
│           │   └── index.html
│           └── templates/
│               ├── config.html
│               ├── history.html
│               └── dashboard.html
```

## 1. pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
   <modelVersion>4.0.0</modelVersion>

   <parent>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-parent</artifactId>
      <version>3.3.0</version>
      <relativePath/>
   </parent>

   <groupId>com.automation</groupId>
   <artifactId>ui-automation-scheduler</artifactId>
   <version>1.0.0</version>
   <name>UI Automation Scheduler</name>

   <properties>
      <java.version>24</java.version>
      <selenium.version>4.20.0</selenium.version>
   </properties>

   <dependencies>
      <!-- Spring Boot Starters -->
      <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-web</artifactId>
      </dependency>
      <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-data-jpa</artifactId>
      </dependency>
      <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-thymeleaf</artifactId>
      </dependency>
      <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-validation</artifactId>
      </dependency>

      <!-- No Quartz dependency needed, using Spring's built-in scheduling -->

      <!-- Database -->
      <dependency>
         <groupId>com.h2database</groupId>
         <artifactId>h2</artifactId>
         <scope>runtime</scope>
      </dependency>
      <dependency>
         <groupId>org.postgresql</groupId>
         <artifactId>postgresql</artifactId>
         <scope>runtime</scope>
      </dependency>

      <!-- Selenium -->
      <dependency>
         <groupId>org.seleniumhq.selenium</groupId>
         <artifactId>selenium-java</artifactId>
         <version>${selenium.version}</version>
      </dependency>
      <dependency>
         <groupId>org.seleniumhq.selenium</groupId>
         <artifactId>selenium-devtools-v129</artifactId>
         <version>${selenium.version}</version>
      </dependency>
      <dependency>
         <groupId>io.github.bonigarcia</groupId>
         <artifactId>webdrivermanager</artifactId>
         <version>5.8.0</version>
      </dependency>

      <!-- Utilities -->
      <dependency>
         <groupId>org.projectlombok</groupId>
         <artifactId>lombok</artifactId>
         <optional>true</optional>
      </dependency>
      <dependency>
         <groupId>com.fasterxml.jackson.datatype</groupId>
         <artifactId>jackson-datatype-jsr310</artifactId>
      </dependency>

      <!-- Test -->
      <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-test</artifactId>
         <scope>test</scope>
      </dependency>
   </dependencies>

   <build>
      <plugins>
         <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
         </plugin>
      </plugins>
   </build>
</project>
```

## 2. Main Application Class

```java
package com.automation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UiAutomationApplication {
    public static void main(String[] args) {
        SpringApplication.run(UiAutomationApplication.class, args);
    }
}
```

## 3. Models

### AutomationConfig.java
```java
package com.automation.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "automation_configs")
@EqualsAndHashCode(exclude = {"steps", "schedule", "results"})
@ToString(exclude = {"results"})
public class AutomationConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "config_id")
    @JsonManagedReference
    private List<AutomationStep> steps;
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "schedule_id")
    private ScheduleConfig schedule;
    
    @OneToMany(mappedBy = "config", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<AutomationResult> results;
    
    private boolean active = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
```

### AutomationStep.java
```java
package com.automation.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
@Table(name = "automation_steps")
@EqualsAndHashCode(exclude = {"config"})
public class AutomationStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "step_order")
    private int order;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StepType type;
    
    @Column(name = "selector")
    private String selector; // CSS selector or XPath
    
    @Column(name = "input_value")
    private String value; // Input value or URL
    
    @Column(name = "wait_seconds")
    private int waitSeconds = 0;
    
    @Column(name = "capture_screenshot")
    private boolean captureScreenshot = false;
    
    @Column(name = "capture_selector")
    private String captureSelector; // Specific area to capture
    
    @ManyToOne
    @JoinColumn(name = "config_id")
    @JsonBackReference
    private AutomationConfig config;
    
    public enum StepType {
        NAVIGATE, CLICK, INPUT, WAIT, SCREENSHOT, SCROLL, SELECT
    }
}
```

### AutomationResult.java
```java
package com.automation.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "automation_results")
public class AutomationResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "config_id", nullable = false)
    private AutomationConfig config;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Lob
    @Column(name = "logs")
    private String logs;
    
    @ElementCollection
    @CollectionTable(name = "result_screenshots")
    private List<String> screenshotPaths;
    
    @Lob
    @Column(name = "error_message")
    private String errorMessage;
    
    public enum Status {
        RUNNING, SUCCESS, FAILED, CANCELLED
    }
}
```

### ScheduleConfig.java
```java
package com.automation.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "schedule_configs")
public class ScheduleConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private ScheduleType type;
    
    @Column(name = "cron_expression")
    private String cronExpression;
    
    @Column(name = "interval_minutes")
    private Integer intervalMinutes;
    
    @Column(name = "run_once_at")
    private String runOnceAt; // ISO datetime string
    
    public enum ScheduleType {
        ONCE, INTERVAL, CRON
    }
}
```

## 4. Repositories

### AutomationConfigRepository.java
```java
package com.automation.repository;

import com.automation.model.AutomationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AutomationConfigRepository extends JpaRepository<AutomationConfig, Long> {
    List<AutomationConfig> findByActiveTrue();
    List<AutomationConfig> findByNameContainingIgnoreCase(String name);
}
```

### AutomationResultRepository.java
```java
package com.automation.repository;

import com.automation.model.AutomationResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public interface AutomationResultRepository extends JpaRepository<AutomationResult, Long> {
    Page<AutomationResult> findByConfigId(Long configId, Pageable pageable);
    Page<AutomationResult> findByStartTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<AutomationResult> findByStatus(AutomationResult.Status status, Pageable pageable);
    long countByConfigId(Long configId);
    void deleteByConfigId(Long configId);
}
```

## 5. Services

### WebDriverService.java
```java
package com.automation.service;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class WebDriverService {
    
    @Value("${automation.screenshot.path:screenshots}")
    private String screenshotPath;
    
    @Value("${automation.driver.headless:false}")
    private boolean headless;
    
    public WebDriver createDriver() {
        // Suppress CDP version warnings
        Logger.getLogger("org.openqa.selenium").setLevel(Level.WARNING);
        System.setProperty("webdriver.chrome.silentOutput", "true");
        
        // Setup ChromeDriver using WebDriverManager
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        
        if (headless) {
            options.addArguments("--headless=new");
        }
        
        // Common Chrome options for stability
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-web-security");
        options.addArguments("--window-size=1920,1080");
        
        // Disable automation info bar
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);
        
        // Prefs to disable notifications and other popups
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);
        
        // Set page load strategy
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        
        // Disable logs
        options.addArguments("--log-level=3");
        options.addArguments("--silent");
        
        return new ChromeDriver(options);
    }
    
    public String captureScreenshot(WebDriver driver, String selector) throws Exception {
        String fileName = UUID.randomUUID() + ".png";
        Path path = Paths.get(screenshotPath, fileName);
        Files.createDirectories(path.getParent());
        
        if (selector != null && !selector.isEmpty()) {
            try {
                WebElement element = driver.findElement(By.cssSelector(selector));
                // Scroll element into view before taking screenshot
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
                Thread.sleep(500); // Small delay to ensure element is in view
                
                File screenshot = element.getScreenshotAs(OutputType.FILE);
                Files.copy(screenshot.toPath(), path);
            } catch (NoSuchElementException e) {
                // If element not found, take full page screenshot
                TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
                File screenshot = takesScreenshot.getScreenshotAs(OutputType.FILE);
                Files.copy(screenshot.toPath(), path);
            }
        } else {
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File screenshot = takesScreenshot.getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), path);
        }
        
        return path.toString();
    }
    
    public void waitForElement(WebDriver driver, String selector, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
    }
    
    public void waitForElementClickable(WebDriver driver, String selector, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(selector)));
    }
}
```

### AutomationService.java
```java
package com.automation.service;

import com.automation.model.*;
import com.automation.repository.AutomationResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutomationService {
    
    private final WebDriverService webDriverService;
    private final AutomationResultRepository resultRepository;
    
    public AutomationResult executeAutomation(AutomationConfig config) {
        AutomationResult result = new AutomationResult();
        result.setConfig(config);
        result.setStartTime(LocalDateTime.now());
        result.setStatus(AutomationResult.Status.RUNNING);
        result.setScreenshotPaths(new ArrayList<>());
        
        StringBuilder logs = new StringBuilder();
        WebDriver driver = null;
        
        try {
            driver = webDriverService.createDriver();
            logs.append("Driver initialized\n");
            
            for (AutomationStep step : config.getSteps()) {
                executeStep(driver, step, result, logs);
            }
            
            result.setStatus(AutomationResult.Status.SUCCESS);
            logs.append("Automation completed successfully\n");
            
        } catch (Exception e) {
            result.setStatus(AutomationResult.Status.FAILED);
            result.setErrorMessage(e.getMessage());
            logs.append("Error: ").append(e.getMessage()).append("\n");
            log.error("Automation failed for config: " + config.getName(), e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
            result.setEndTime(LocalDateTime.now());
            result.setLogs(logs.toString());
            resultRepository.save(result);
        }
        
        return result;
    }
    
    private void executeStep(WebDriver driver, AutomationStep step, 
                           AutomationResult result, StringBuilder logs) throws Exception {
        logs.append("Executing step: ").append(step.getType()).append("\n");
        
        // Validate step data
        validateStep(step);
        
        switch (step.getType()) {
            case NAVIGATE:
                String url = step.getValue();
                if (url == null || url.trim().isEmpty()) {
                    throw new IllegalArgumentException("Navigate step requires a valid URL");
                }
                // Add protocol if missing
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "https://" + url;
                }
                driver.get(url);
                logs.append("Navigated to: ").append(url).append("\n");
                break;
                
            case CLICK:
                if (step.getSelector() == null || step.getSelector().trim().isEmpty()) {
                    throw new IllegalArgumentException("Click step requires a selector");
                }
                WebElement clickElement = driver.findElement(By.cssSelector(step.getSelector()));
                clickElement.click();
                logs.append("Clicked element: ").append(step.getSelector()).append("\n");
                break;
                
            case INPUT:
                if (step.getSelector() == null || step.getSelector().trim().isEmpty()) {
                    throw new IllegalArgumentException("Input step requires a selector");
                }
                if (step.getValue() == null) {
                    step.setValue(""); // Allow empty input
                }
                WebElement inputElement = driver.findElement(By.cssSelector(step.getSelector()));
                inputElement.clear();
                inputElement.sendKeys(step.getValue());
                logs.append("Input text to: ").append(step.getSelector()).append("\n");
                break;
                
            case WAIT:
                int waitTime = step.getWaitSeconds() > 0 ? step.getWaitSeconds() : 1;
                Thread.sleep(waitTime * 1000L);
                logs.append("Waited for: ").append(waitTime).append(" seconds\n");
                break;
                
            case SCREENSHOT:
                String screenshotPath = webDriverService.captureScreenshot(driver, step.getCaptureSelector());
                result.getScreenshotPaths().add(screenshotPath);
                logs.append("Screenshot captured: ").append(screenshotPath).append("\n");
                break;
                
            case SCROLL:
                String scrollValue = step.getValue() != null ? step.getValue() : "0";
                ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, " + scrollValue + ")");
                logs.append("Scrolled to position: ").append(scrollValue).append("\n");
                break;
                
            case SELECT:
                if (step.getSelector() == null || step.getSelector().trim().isEmpty()) {
                    throw new IllegalArgumentException("Select step requires a selector");
                }
                if (step.getValue() == null || step.getValue().trim().isEmpty()) {
                    throw new IllegalArgumentException("Select step requires a value");
                }
                Select select = new Select(driver.findElement(By.cssSelector(step.getSelector())));
                select.selectByValue(step.getValue());
                logs.append("Selected option: ").append(step.getValue()).append("\n");
                break;
        }
        
        if (step.isCaptureScreenshot() && step.getType() != AutomationStep.StepType.SCREENSHOT) {
            String screenshotPath = webDriverService.captureScreenshot(driver, step.getCaptureSelector());
            result.getScreenshotPaths().add(screenshotPath);
            logs.append("Step screenshot captured: ").append(screenshotPath).append("\n");
        }
        
        if (step.getWaitSeconds() > 0 && step.getType() != AutomationStep.StepType.WAIT) {
            Thread.sleep(step.getWaitSeconds() * 1000L);
        }
    }
    
    private void validateStep(AutomationStep step) {
        if (step.getType() == null) {
            throw new IllegalArgumentException("Step type is required");
        }
    }
}
```

### SchedulerService.java
```java
package com.automation.service;

import com.automation.model.AutomationConfig;
import com.automation.model.ScheduleConfig;
import com.automation.repository.AutomationConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {
    
    private final TaskScheduler taskScheduler;
    private final AutomationService automationService;
    private final AutomationConfigRepository configRepository;
    
    // Map to store scheduled tasks
    private final Map<Long, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        log.info("Initializing scheduler service");
        scheduleActiveAutomations();
    }
    
    @PreDestroy
    public void cleanup() {
        log.info("Cleaning up scheduled tasks");
        scheduledTasks.values().forEach(task -> task.cancel(false));
        scheduledTasks.clear();
    }
    
    public void scheduleActiveAutomations() {
        configRepository.findByActiveTrue().forEach(this::scheduleAutomation);
    }
    
    public void scheduleAutomation(AutomationConfig config) {
        if (!config.isActive() || config.getSchedule() == null) {
            return;
        }
        
        // Cancel existing schedule if any
        unscheduleAutomation(config.getId());
        
        try {
            ScheduledFuture<?> scheduledTask = null;
            ScheduleConfig schedule = config.getSchedule();
            
            Runnable task = () -> {
                log.info("Executing scheduled automation: " + config.getName());
                automationService.executeAutomation(config);
            };
            
            switch (schedule.getType()) {
                case ONCE:
                    LocalDateTime runTime = LocalDateTime.parse(schedule.getRunOnceAt());
                    Instant instant = runTime.atZone(ZoneId.systemDefault()).toInstant();
                    scheduledTask = taskScheduler.schedule(task, instant);
                    log.info("Scheduled one-time automation '{}' at {}", config.getName(), runTime);
                    break;
                    
                case INTERVAL:
                    Duration interval = Duration.ofMinutes(schedule.getIntervalMinutes());
                    scheduledTask = taskScheduler.scheduleAtFixedRate(task, Instant.now(), interval);
                    log.info("Scheduled interval automation '{}' every {} minutes", 
                            config.getName(), schedule.getIntervalMinutes());
                    break;
                    
                case CRON:
                    if (isValidCronExpression(schedule.getCronExpression())) {
                        CronTrigger cronTrigger = new CronTrigger(schedule.getCronExpression());
                        scheduledTask = taskScheduler.schedule(task, cronTrigger);
                        log.info("Scheduled cron automation '{}' with expression: {}", 
                                config.getName(), schedule.getCronExpression());
                    } else {
                        log.error("Invalid cron expression for config {}: {}", 
                                config.getName(), schedule.getCronExpression());
                        return;
                    }
                    break;
                    
                default:
                    log.warn("Unknown schedule type: " + schedule.getType());
                    return;
            }
            
            if (scheduledTask != null) {
                scheduledTasks.put(config.getId(), scheduledTask);
            }
            
        } catch (Exception e) {
            log.error("Failed to schedule automation: " + config.getName(), e);
        }
    }
    
    public void unscheduleAutomation(Long configId) {
        ScheduledFuture<?> scheduledTask = scheduledTasks.remove(configId);
        if (scheduledTask != null) {
            scheduledTask.cancel(false);
            log.info("Unscheduled automation with id: " + configId);
        }
    }
    
    public void rescheduleAutomation(AutomationConfig config) {
        unscheduleAutomation(config.getId());
        if (config.isActive() && config.getSchedule() != null) {
            scheduleAutomation(config);
        }
    }
    
    public boolean isScheduled(Long configId) {
        return scheduledTasks.containsKey(configId) && 
               !scheduledTasks.get(configId).isCancelled();
    }
    
    public Map<Long, Boolean> getScheduledStatus() {
        Map<Long, Boolean> status = new ConcurrentHashMap<>();
        scheduledTasks.forEach((configId, future) -> {
            status.put(configId, !future.isCancelled() && !future.isDone());
        });
        return status;
    }
    
    private boolean isValidCronExpression(String cronExpression) {
        try {
            CronExpression.parse(cronExpression);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
```

## 6. Controllers

### AutomationController.java
```java
package com.automation.controller;

import com.automation.dto.AutomationConfigDTO;
import com.automation.model.AutomationConfig;
import com.automation.model.AutomationResult;
import com.automation.repository.AutomationConfigRepository;
import com.automation.service.AutomationService;
import com.automation.service.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/automation")
@RequiredArgsConstructor
public class AutomationController {
    
    private final AutomationConfigRepository configRepository;
    private final AutomationService automationService;
    private final SchedulerService schedulerService;
    private final ConfigurationService configurationService;
    
    @GetMapping("/configs")
    public List<AutomationConfig> getAllConfigs() {
        return configRepository.findAll();
    }
    
    @GetMapping("/configs/{id}")
    public ResponseEntity<AutomationConfig> getConfig(@PathVariable Long id) {
        return configRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/configs")
    public ResponseEntity<AutomationConfig> createConfig(@RequestBody AutomationConfigDTO dto) {
        // Validate configuration
        validateConfiguration(dto);
        
        AutomationConfig config = new AutomationConfig();
        // Map DTO to entity
        config.setName(dto.getName());
        config.setDescription(dto.getDescription());
        config.setSteps(dto.getSteps());
        config.setSchedule(dto.getSchedule());
        config.setActive(dto.isActive());
        
        AutomationConfig saved = configRepository.save(config);
        
        if (saved.isActive() && saved.getSchedule() != null) {
            schedulerService.scheduleAutomation(saved);
        }
        
        return ResponseEntity.ok(saved);
    }
    
    @PutMapping("/configs/{id}")
    public ResponseEntity<AutomationConfig> updateConfig(@PathVariable Long id, 
                                                       @RequestBody AutomationConfigDTO dto) {
        // Validate configuration
        validateConfiguration(dto);
        
        return configRepository.findById(id)
            .map(config -> {
                config.setName(dto.getName());
                config.setDescription(dto.getDescription());
                config.setSteps(dto.getSteps());
                config.setSchedule(dto.getSchedule());
                config.setActive(dto.isActive());
                
                AutomationConfig saved = configRepository.save(config);
                
                // Reschedule if needed
                schedulerService.rescheduleAutomation(saved);
                
                return ResponseEntity.ok(saved);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    private void validateConfiguration(AutomationConfigDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Configuration name is required");
        }
        
        if (dto.getSteps() == null || dto.getSteps().isEmpty()) {
            throw new IllegalArgumentException("At least one step is required");
        }
        
        // Validate each step
        for (int i = 0; i < dto.getSteps().size(); i++) {
            AutomationStep step = dto.getSteps().get(i);
            if (step.getType() == null) {
                throw new IllegalArgumentException("Step " + (i + 1) + ": Type is required");
            }
            
            switch (step.getType()) {
                case NAVIGATE:
                    if (step.getValue() == null || step.getValue().trim().isEmpty()) {
                        throw new IllegalArgumentException("Step " + (i + 1) + " (NAVIGATE): URL is required");
                    }
                    break;
                case CLICK:
                case INPUT:
                case SELECT:
                    if (step.getSelector() == null || step.getSelector().trim().isEmpty()) {
                        throw new IllegalArgumentException("Step " + (i + 1) + " (" + step.getType() + "): Selector is required");
                    }
                    break;
            }
        }
        
        // Validate schedule if present
        if (dto.getSchedule() != null) {
            ScheduleConfig schedule = dto.getSchedule();
            switch (schedule.getType()) {
                case ONCE:
                    if (schedule.getRunOnceAt() == null || schedule.getRunOnceAt().trim().isEmpty()) {
                        throw new IllegalArgumentException("Schedule: Run once date/time is required");
                    }
                    break;
                case INTERVAL:
                    if (schedule.getIntervalMinutes() == null || schedule.getIntervalMinutes() <= 0) {
                        throw new IllegalArgumentException("Schedule: Interval must be greater than 0");
                    }
                    break;
                case CRON:
                    if (schedule.getCronExpression() == null || schedule.getCronExpression().trim().isEmpty()) {
                        throw new IllegalArgumentException("Schedule: Cron expression is required");
                    }
                    break;
            }
        }
    }
    
    @DeleteMapping("/configs/{id}")
    public ResponseEntity<Map<String, Object>> deleteConfig(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean force) {
        
        if (!configRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        
        // Check if there are associated results
        long resultCount = configurationService.countResultsForConfig(id);
        
        if (resultCount > 0 && !force) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Cannot delete configuration");
            response.put("message", String.format(
                "This configuration has %d execution results. " +
                "Delete the results first or use force=true to delete everything.", 
                resultCount));
            response.put("resultCount", resultCount);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        
        try {
            configurationService.deleteConfiguration(id, force);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Configuration deleted successfully");
            response.put("deletedResults", force ? resultCount : 0);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Failed to delete configuration");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/configs/{id}/run")
    public ResponseEntity<AutomationResult> runNow(@PathVariable Long id) {
        return configRepository.findById(id)
            .map(config -> {
                AutomationResult result = automationService.executeAutomation(config);
                return ResponseEntity.ok(result);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/configs/{id}/toggle")
    public ResponseEntity<Map<String, Object>> getScheduleStatus(@PathVariable Long id) {
        return configRepository.findById(id)
            .map(config -> {
                Map<String, Object> status = new HashMap<>();
                status.put("configId", id);
                status.put("configName", config.getName());
                status.put("isActive", config.isActive());
                status.put("isScheduled", schedulerService.isScheduled(id));
                status.put("schedule", config.getSchedule());
                return ResponseEntity.ok(status);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/configs/{id}/toggle")
    public ResponseEntity<AutomationConfig> toggleActive(@PathVariable Long id) {
        return configRepository.findById(id)
            .map(config -> {
                config.setActive(!config.isActive());
                AutomationConfig saved = configRepository.save(config);
                
                if (saved.isActive() && saved.getSchedule() != null) {
                    schedulerService.scheduleAutomation(saved);
                } else {
                    schedulerService.unscheduleAutomation(id);
                }
                
                return ResponseEntity.ok(saved);
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
```

### HistoryController.java
```java
package com.automation.controller;

import com.automation.model.AutomationResult;
import com.automation.repository.AutomationResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {
    
    private final AutomationResultRepository resultRepository;
    
    @GetMapping
    public Page<AutomationResultDTO> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long configId,
            @RequestParam(required = false) AutomationResult.Status status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("startTime").descending());
        Page<AutomationResult> results;
        
        if (configId != null) {
            results = resultRepository.findByConfigId(configId, pageRequest);
        } else if (status != null) {
            results = resultRepository.findByStatus(status, pageRequest);
        } else if (startDate != null && endDate != null) {
            results = resultRepository.findByStartTimeBetween(startDate, endDate, pageRequest);
        } else {
            results = resultRepository.findAll(pageRequest);
        }
        
        return results.map(AutomationResultDTO::fromEntity);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AutomationResultDTO> getResult(@PathVariable Long id) {
        return resultRepository.findById(id)
            .map(result -> ResponseEntity.ok(AutomationResultDTO.fromEntity(result)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/screenshot/{index}")
    public ResponseEntity<byte[]> getScreenshot(@PathVariable Long id, @PathVariable int index) {
        return resultRepository.findById(id)
            .map(result -> {
                if (result.getScreenshotPaths() != null && 
                    index >= 0 && 
                    index < result.getScreenshotPaths().size()) {
                    try {
                        Path path = Paths.get(result.getScreenshotPaths().get(index));
                        byte[] image = Files.readAllBytes(path);
                        return ResponseEntity.ok()
                            .header("Content-Type", "image/png")
                            .body(image);
                    } catch (IOException e) {
                        return ResponseEntity.notFound().<byte[]>build();
                    }
                }
                return ResponseEntity.notFound().<byte[]>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        if (resultRepository.existsById(id)) {
            resultRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
```

## 7. Configuration Files

### application.yml
```yaml
spring:
  application:
    name: UI Automation Scheduler
  
  datasource:
    url: jdbc:h2:file:./data/automation
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
        globally_quoted_identifiers: true
  
  jackson:
    serialization:
      write-dates-as-timestamps: false
    date-format: com.fasterxml.jackson.databind.util.ISO8601DateFormat
  
  h2:
    console:
      enabled: true
      path: /h2-console

automation:
  screenshot:
    path: ./screenshots
  driver:
    headless: false
    timeout: 30

# Suppress Selenium CDP warnings
logging:
  level:
    com.automation: DEBUG
    org.springframework.web: INFO
    org.hibernate: WARN
    org.openqa.selenium.devtools: OFF
    org.openqa.selenium.chromium: OFF
```

## 8. DTOs

### AutomationConfigDTO.java
```java
package com.automation.dto;

import com.automation.model.AutomationStep;
import com.automation.model.ScheduleConfig;
import lombok.Data;
import java.util.List;

@Data
public class AutomationConfigDTO {
    private String name;
    private String description;
    private List<AutomationStep> steps;
    private ScheduleConfig schedule;
    private boolean active;
}
```

### AutomationResultDTO.java
```java
package com.automation.dto;

import com.automation.model.AutomationResult;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AutomationResultDTO {
    private Long id;
    private Long configId;
    private String configName;
    private String configDescription;
    private AutomationResult.Status status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String logs;
    private List<String> screenshotPaths;
    private String errorMessage;
    private List<AutomationStepDTO> steps;
    
    public static AutomationResultDTO fromEntity(AutomationResult result) {
        AutomationResultDTO dto = new AutomationResultDTO();
        dto.setId(result.getId());
        dto.setConfigId(result.getConfig().getId());
        dto.setConfigName(result.getConfig().getName());
        dto.setConfigDescription(result.getConfig().getDescription());
        dto.setStatus(result.getStatus());
        dto.setStartTime(result.getStartTime());
        dto.setEndTime(result.getEndTime());
        dto.setLogs(result.getLogs());
        dto.setScreenshotPaths(result.getScreenshotPaths());
        dto.setErrorMessage(result.getErrorMessage());
        
        // Convert steps
        if (result.getConfig().getSteps() != null) {
            dto.setSteps(result.getConfig().getSteps().stream()
                .map(AutomationStepDTO::fromEntity)
                .toList());
        }
        
        return dto;
    }
}
```

## 9. Configuration Classes

### WebConfig.java
```java
package com.automation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve screenshots from file system
        registry.addResourceHandler("/screenshots/**")
                .addResourceLocations("file:./screenshots/");
        
        // Serve static resources
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}
```

### SchedulerConfig.java
```java
package com.automation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("automation-scheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        scheduler.initialize();
        return scheduler;
    }
}
```

### SeleniumConfig.java
```java
package com.automation.config;

import jakarta.annotation.PreDestroy;
import org.openqa.selenium.WebDriver;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class SeleniumConfig {
    
    private final List<WebDriver> activeDrivers = new ArrayList<>();
    
    public void registerDriver(WebDriver driver) {
        activeDrivers.add(driver);
    }
    
    public void unregisterDriver(WebDriver driver) {
        activeDrivers.remove(driver);
    }
    
    @PreDestroy
    public void cleanup() {
        activeDrivers.forEach(driver -> {
            try {
                driver.quit();
            } catch (Exception e) {
                // Ignore
            }
        });
    }
}
```

## 10. Frontend (Basic HTML/JS)

### static/index.html
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>UI Automation Scheduler</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
        <div class="container">
            <a class="navbar-brand" href="/">UI Automation Scheduler</a>
            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="#configs">Configurations</a>
                <a class="nav-link" href="#history">History</a>
                <a class="nav-link" href="#create">Create New</a>
            </div>
        </div>
    </nav>

    <div class="container mt-4">
        <div id="configs" class="mb-5">
            <h2>Automation Configurations</h2>
            <div class="row" id="configsList"></div>
        </div>

        <div id="history" class="mb-5">
            <h2>Execution History</h2>
            <div class="table-responsive">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>Config Name</th>
                            <th>Status</th>
                            <th>Start Time</th>
                            <th>Duration</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody id="historyTable"></tbody>
                </table>
            </div>
        </div>

        <div id="create" class="mb-5">
            <h2>Create New Configuration</h2>
            <form id="configForm">
                <div class="mb-3">
                    <label for="configName" class="form-label">Name</label>
                    <input type="text" class="form-control" id="configName" required>
                </div>
                <div class="mb-3">
                    <label for="configDescription" class="form-label">Description</label>
                    <textarea class="form-control" id="configDescription" rows="2"></textarea>
                </div>
                
                <h4>Steps</h4>
                <div id="stepsContainer"></div>
                <button type="button" class="btn btn-sm btn-secondary mb-3" onclick="addStep()">Add Step</button>
                
                <h4>Schedule</h4>
                <div class="mb-3">
                    <label for="scheduleType" class="form-label">Schedule Type</label>
                    <select class="form-select" id="scheduleType" onchange="updateScheduleFields()">
                        <option value="">No Schedule (Manual Only)</option>
                        <option value="ONCE">Run Once</option>
                        <option value="INTERVAL">Interval</option>
                        <option value="CRON">Cron Expression</option>
                    </select>
                </div>
                <div id="scheduleFields"></div>
                
                <button type="submit" class="btn btn-primary">Create Configuration</button>
            </form>
        </div>
    </div>
    
    <!-- Details Modal -->
    <div class="modal fade" id="detailsModal" tabindex="-1">
        <div class="modal-dialog modal-xl">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Automation Execution Details</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body" id="detailsContent">
                    <!-- Content will be loaded dynamically -->
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="/js/app.js"></script>
</body>
</html>
```

### static/js/app.js
```javascript
// Load configurations
async function loadConfigs() {
    const response = await fetch('/api/automation/configs');
    const configs = await response.json();
    
    const container = document.getElementById('configsList');
    container.innerHTML = '';
    
    configs.forEach(config => {
        const card = `
            <div class="col-md-4 mb-3">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">${config.name}</h5>
                        <p class="card-text">${config.description || 'No description'}</p>
                        <p class="card-text">
                            <small class="text-muted">
                                Status: ${config.active ? 'Active' : 'Inactive'}
                            </small>
                        </p>
                        <button class="btn btn-sm btn-primary" onclick="runNow(${config.id})">Run Now</button>
                        <button class="btn btn-sm btn-secondary" onclick="toggleActive(${config.id})">
                            ${config.active ? 'Deactivate' : 'Activate'}
                        </button>
                        <button class="btn btn-sm btn-danger" onclick="deleteConfig(${config.id})">Delete</button>
                    </div>
                </div>
            </div>
        `;
        container.innerHTML += card;
    });
}

// Load history
async function loadHistory() {
    try {
        const response = await fetch('/api/history');
        const data = await response.json();
        
        const tbody = document.getElementById('historyTable');
        tbody.innerHTML = '';
        
        // Debug: log first result to check date format
        if (data.content.length > 0) {
            console.log('Sample result:', data.content[0]);
            console.log('Start time:', data.content[0].startTime);
        }
        
        data.content.forEach(result => {
            let duration = 'Running...';
            let startTimeStr = 'N/A';
            
            if (result.startTime) {
                // Handle different date formats
                let startTime;
                if (Array.isArray(result.startTime)) {
                    // Handle array format [year, month, day, hour, minute, second, nano]
                    const [year, month, day, hour, minute, second] = result.startTime;
                    startTime = new Date(year, month - 1, day, hour, minute, second);
                } else {
                    // Handle ISO string format
                    startTime = new Date(result.startTime);
                }
                
                if (!isNaN(startTime.getTime())) {
                    startTimeStr = formatDateTime(startTime);
                    
                    if (result.endTime) {
                        let endTime;
                        if (Array.isArray(result.endTime)) {
                            const [year, month, day, hour, minute, second] = result.endTime;
                            endTime = new Date(year, month - 1, day, hour, minute, second);
                        } else {
                            endTime = new Date(result.endTime);
                        }
                        
                        if (!isNaN(endTime.getTime())) {
                            const durationMs = endTime.getTime() - startTime.getTime();
                            duration = formatDuration(durationMs);
                        }
                    }
                }
            }
            
            const row = `
                <tr>
                    <td>${result.configName || 'Unknown'}</td>
                    <td>
                        <span class="badge bg-${getStatusColor(result.status)}">
                            ${result.status}
                        </span>
                    </td>
                    <td>${startTimeStr}</td>
                    <td>${duration}</td>
                    <td>
                        <button class="btn btn-sm btn-info" onclick="viewDetails(${result.id})">Details</button>
                    </td>
                </tr>
            `;
            tbody.innerHTML += row;
        });
    } catch (error) {
        console.error('Error loading history:', error);
        const tbody = document.getElementById('historyTable');
        tbody.innerHTML = '<tr><td colspan="5" class="text-center text-danger">Error loading history</td></tr>';
    }
}

// Format date time for display
function formatDateTime(date) {
    if (!date || isNaN(date.getTime())) {
        return 'N/A';
    }
    
    const options = {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
    };
    
    return date.toLocaleDateString('en-US', options);
}

// Format duration from milliseconds
function formatDuration(milliseconds) {
    if (isNaN(milliseconds) || milliseconds < 0) {
        return 'N/A';
    }
    
    const seconds = Math.floor(milliseconds / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    
    if (hours > 0) {
        return `${hours}h ${minutes % 60}m ${seconds % 60}s`;
    } else if (minutes > 0) {
        return `${minutes}m ${seconds % 60}s`;
    } else {
        return `${seconds}s`;
    }
}

async function viewDetails(resultId) {
    try {
        const response = await fetch(`/api/history/${resultId}`);
        if (!response.ok) throw new Error('Failed to fetch details');
        
        const result = await response.json();
        
        let screenshotsHtml = '';
        if (result.screenshotPaths && result.screenshotPaths.length > 0) {
            screenshotsHtml = '<h6>Screenshots:</h6><div class="row">';
            result.screenshotPaths.forEach((path, index) => {
                screenshotsHtml += `
                    <div class="col-md-4 mb-3">
                        <img src="/api/history/${resultId}/screenshot/${index}" 
                             class="img-fluid img-thumbnail" 
                             alt="Screenshot ${index + 1}"
                             style="cursor: pointer;"
                             onclick="window.open('/api/history/${resultId}/screenshot/${index}', '_blank')">
                        <small class="text-muted d-block mt-1">Screenshot ${index + 1}</small>
                    </div>
                `;
            });
            screenshotsHtml += '</div>';
        }
        
        let duration = 'N/A';
        let startTimeStr = 'N/A';
        let endTimeStr = 'N/A';
        
        if (result.startTime) {
            const startTime = new Date(result.startTime);
            startTimeStr = formatDateTime(startTime);
            
            if (result.endTime) {
                const endTime = new Date(result.endTime);
                endTimeStr = formatDateTime(endTime);
                const durationMs = endTime.getTime() - startTime.getTime();
                duration = formatDuration(durationMs);
            }
        }
        
        const detailsHtml = `
            <div class="row mb-4">
                <div class="col-md-6">
                    <h6>Configuration:</h6>
                    <p><strong>Name:</strong> ${result.configName}</p>
                    <p><strong>Description:</strong> ${result.configDescription || 'N/A'}</p>
                </div>
                <div class="col-md-6">
                    <h6>Execution Info:</h6>
                    <p><strong>Status:</strong> 
                        <span class="badge bg-${getStatusColor(result.status)}">${result.status}</span>
                    </p>
                    <p><strong>Start Time:</strong> ${startTimeStr}</p>
                    <p><strong>End Time:</strong> ${endTimeStr}</p>
                    <p><strong>Duration:</strong> ${duration}</p>
                </div>
            </div>
            
            ${result.errorMessage ? `
                <div class="alert alert-danger">
                    <h6>Error Message:</h6>
                    <pre class="mb-0">${escapeHtml(result.errorMessage)}</pre>
                </div>
            ` : ''}
            
            <div class="mb-4">
                <h6>Execution Logs:</h6>
                <div class="bg-light p-3 rounded" style="max-height: 300px; overflow-y: auto;">
                    <pre class="mb-0">${escapeHtml(result.logs || 'No logs available')}</pre>
                </div>
            </div>
            
            ${screenshotsHtml}
            
            <div class="mb-4">
                <h6>Automation Steps:</h6>
                <div class="table-responsive">
                    <table class="table table-sm table-bordered">
                        <thead>
                            <tr>
                                <th>Order</th>
                                <th>Type</th>
                                <th>Selector</th>
                                <th>Value</th>
                                <th>Wait</th>
                                <th>Screenshot</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${result.steps ? result.steps.map(step => `
                                <tr>
                                    <td>${step.order}</td>
                                    <td>${step.type}</td>
                                    <td><code>${step.selector || '-'}</code></td>
                                    <td>${step.value || '-'}</td>
                                    <td>${step.waitSeconds}s</td>
                                    <td>${step.captureScreenshot ? '✓' : '-'}</td>
                                </tr>
                            `).join('') : '<tr><td colspan="6">No steps available</td></tr>'}
                        </tbody>
                    </table>
                </div>
            </div>
        `;
        
        document.getElementById('detailsContent').innerHTML = detailsHtml;
        const modal = new bootstrap.Modal(document.getElementById('detailsModal'));
        modal.show();
        
    } catch (error) {
        alert('Failed to load details: ' + error.message);
    }
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Helper functions
function getStatusColor(status) {
    switch(status) {
        case 'SUCCESS': return 'success';
        case 'FAILED': return 'danger';
        case 'RUNNING': return 'primary';
        default: return 'secondary';
    }
}

async function runNow(configId) {
    const response = await fetch(`/api/automation/configs/${configId}/run`, { method: 'POST' });
    if (response.ok) {
        alert('Automation started!');
        loadHistory();
    }
}

async function toggleActive(configId) {
    const response = await fetch(`/api/automation/configs/${configId}/toggle`, { method: 'POST' });
    if (response.ok) {
        loadConfigs();
    }
}

async function deleteConfig(configId) {
    if (!confirm('Are you sure you want to delete this configuration?')) {
        return;
    }
    
    try {
        // First try to delete without force
        let response = await fetch(`/api/automation/configs/${configId}`, { method: 'DELETE' });
        
        if (response.status === 409) {
            // Conflict - has results
            const data = await response.json();
            const forceDelete = confirm(
                `${data.message}\n\n` +
                `Do you want to delete the configuration and all ${data.resultCount} execution results?`
            );
            
            if (forceDelete) {
                response = await fetch(`/api/automation/configs/${configId}?force=true`, { method: 'DELETE' });
            } else {
                return;
            }
        }
        
        if (response.ok) {
            const result = await response.json();
            alert(result.message || 'Configuration deleted successfully');
            loadConfigs();
            loadHistory();
        } else {
            const error = await response.json();
            alert('Failed to delete: ' + (error.message || 'Unknown error'));
        }
    } catch (error) {
        alert('Error deleting configuration: ' + error.message);
    }
}

// Step management
let stepCount = 0;

function addStep() {
    const container = document.getElementById('stepsContainer');
    const stepDiv = document.createElement('div');
    stepDiv.className = 'card mb-2';
    stepDiv.id = `step-${stepCount}`;
    
    stepDiv.innerHTML = `
        <div class="card-body">
            <div class="row">
                <div class="col-md-3">
                    <select class="form-select step-type" onchange="updateStepFields(${stepCount})">
                        <option value="NAVIGATE">Navigate</option>
                        <option value="CLICK">Click</option>
                        <option value="INPUT">Input</option>
                        <option value="WAIT">Wait</option>
                        <option value="SCREENSHOT">Screenshot</option>
                        <option value="SCROLL">Scroll</option>
                        <option value="SELECT">Select</option>
                    </select>
                </div>
                <div class="col-md-8" id="stepFields-${stepCount}">
                    <input type="text" class="form-control" placeholder="URL (e.g., https://example.com or example.com)" data-field="value" required>
                </div>
                <div class="col-md-1">
                    <button class="btn btn-sm btn-danger" onclick="removeStep(${stepCount})">X</button>
                </div>
            </div>
            <div class="row mt-2">
                <div class="col-md-6">
                    <div class="form-check">
                        <input class="form-check-input" type="checkbox" id="captureScreenshot-${stepCount}" data-field="captureScreenshot">
                        <label class="form-check-label" for="captureScreenshot-${stepCount}">
                            Capture screenshot after this step
                        </label>
                    </div>
                </div>
                <div class="col-md-3">
                    <input type="number" class="form-control form-control-sm" placeholder="Wait after (seconds)" data-field="waitSeconds" min="0" value="0">
                </div>
                <div class="col-md-3">
                    <input type="text" class="form-control form-control-sm" placeholder="Screenshot selector (optional)" data-field="captureSelector">
                </div>
            </div>
        </div>
    `;
    
    container.appendChild(stepDiv);
    stepCount++;
}

function updateStepFields(stepId) {
    const stepDiv = document.getElementById(`step-${stepId}`);
    const stepType = stepDiv.querySelector('.step-type').value;
    const fieldsContainer = document.getElementById(`stepFields-${stepId}`);
    
    // Save existing values
    const existingValues = {};
    fieldsContainer.querySelectorAll('[data-field]').forEach(input => {
        existingValues[input.dataset.field] = input.value;
    });
    
    let fields = '';
    let preserveValue = false;
    
    switch(stepType) {
        case 'NAVIGATE':
            fields = '<input type="text" class="form-control" placeholder="URL (e.g., https://example.com or example.com)" data-field="value" required>';
            preserveValue = existingValues.value;
            break;
        case 'CLICK':
            fields = '<input type="text" class="form-control" placeholder="CSS Selector (e.g., #submit-button, .btn-primary)" data-field="selector" required>';
            break;
        case 'INPUT':
            fields = `
                <input type="text" class="form-control mb-2" placeholder="CSS Selector (e.g., #username, input[name=\'email\'])" data-field="selector" required>
                <input type="text" class="form-control" placeholder="Text to input" data-field="value">
            `;
            break;
        case 'WAIT':
            fields = '<input type="number" class="form-control" placeholder="Seconds to wait" data-field="waitSeconds" min="1" value="1">';
            preserveValue = existingValues.waitSeconds;
            break;
        case 'SCREENSHOT':
            fields = '<input type="text" class="form-control" placeholder="CSS Selector for specific area (optional, leave empty for full page)" data-field="captureSelector">';
            preserveValue = existingValues.captureSelector;
            break;
        case 'SCROLL':
            fields = '<input type="number" class="form-control" placeholder="Scroll position in pixels (e.g., 500)" data-field="value" value="0">';
            preserveValue = existingValues.value;
            break;
        case 'SELECT':
            fields = `
                <input type="text" class="form-control mb-2" placeholder="CSS Selector (e.g., #country-select)" data-field="selector" required>
                <input type="text" class="form-control" placeholder="Option value to select" data-field="value" required>
            `;
            break;
    }
    
    fieldsContainer.innerHTML = fields;
    
    // Restore values where applicable
    fieldsContainer.querySelectorAll('[data-field]').forEach(input => {
        const fieldName = input.dataset.field;
        if (existingValues[fieldName] !== undefined) {
            input.value = existingValues[fieldName];
        }
    });
}

function removeStep(stepId) {
    document.getElementById(`step-${stepId}`).remove();
}

// Schedule fields
function updateScheduleFields() {
    const scheduleType = document.getElementById('scheduleType').value;
    const container = document.getElementById('scheduleFields');
    
    let fields = '';
    switch(scheduleType) {
        case 'ONCE':
            fields = '<input type="datetime-local" class="form-control" id="runOnceAt">';
            break;
        case 'INTERVAL':
            fields = '<input type="number" class="form-control" id="intervalMinutes" placeholder="Minutes">';
            break;
        case 'CRON':
            fields = '<input type="text" class="form-control" id="cronExpression" placeholder="0 0 * * * ?">';
            break;
    }
    container.innerHTML = fields;
}

// Form submission
document.getElementById('configForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    // Validate form
    const name = document.getElementById('configName').value.trim();
    if (!name) {
        alert('Configuration name is required');
        return;
    }
    
    const steps = [];
    const stepDivs = document.querySelectorAll('[id^="step-"]');
    
    if (stepDivs.length === 0) {
        alert('At least one step is required');
        return;
    }
    
    // Validate and collect steps
    for (let index = 0; index < stepDivs.length; index++) {
        const stepDiv = stepDivs[index];
        const type = stepDiv.querySelector('.step-type').value;
        const step = {
            order: index,
            type: type,
            waitSeconds: 0,
            captureScreenshot: false
        };
        
        // Collect all fields including checkboxes
        stepDiv.querySelectorAll('[data-field]').forEach(input => {
            if (input.type === 'checkbox') {
                step[input.dataset.field] = input.checked;
            } else if (input.type === 'number') {
                step[input.dataset.field] = parseInt(input.value) || 0;
            } else {
                step[input.dataset.field] = input.value;
            }
        });
        
        // Validate required fields based on step type
        let error = null;
        switch (type) {
            case 'NAVIGATE':
                if (!step.value || !step.value.trim()) {
                    error = `Step ${index + 1} (Navigate): URL is required`;
                }
                break;
            case 'CLICK':
                if (!step.selector || !step.selector.trim()) {
                    error = `Step ${index + 1} (Click): CSS Selector is required`;
                }
                break;
            case 'INPUT':
                if (!step.selector || !step.selector.trim()) {
                    error = `Step ${index + 1} (Input): CSS Selector is required`;
                }
                break;
            case 'SELECT':
                if (!step.selector || !step.selector.trim()) {
                    error = `Step ${index + 1} (Select): CSS Selector is required`;
                }
                if (!step.value || !step.value.trim()) {
                    error = `Step ${index + 1} (Select): Option value is required`;
                }
                break;
            case 'WAIT':
                if (!step.waitSeconds || step.waitSeconds <= 0) {
                    step.waitSeconds = 1; // Default to 1 second
                }
                break;
        }
        
        if (error) {
            alert(error);
            return;
        }
        
        steps.push(step);
    }
    
    const scheduleType = document.getElementById('scheduleType').value;
    let schedule = null;
    if (scheduleType) {
        schedule = { type: scheduleType };
        switch(scheduleType) {
            case 'ONCE':
                const runOnceAt = document.getElementById('runOnceAt').value;
                if (!runOnceAt) {
                    alert('Schedule: Run once date/time is required');
                    return;
                }
                schedule.runOnceAt = runOnceAt;
                break;
            case 'INTERVAL':
                const intervalMinutes = parseInt(document.getElementById('intervalMinutes').value);
                if (!intervalMinutes || intervalMinutes <= 0) {
                    alert('Schedule: Interval must be greater than 0');
                    return;
                }
                schedule.intervalMinutes = intervalMinutes;
                break;
            case 'CRON':
                const cronExpression = document.getElementById('cronExpression').value;
                if (!cronExpression || !cronExpression.trim()) {
                    alert('Schedule: Cron expression is required');
                    return;
                }
                schedule.cronExpression = cronExpression;
                break;
        }
    }
    
    const config = {
        name: name,
        description: document.getElementById('configDescription').value,
        steps: steps,
        schedule: schedule,
        active: true
    };
    
    try {
        const response = await fetch('/api/automation/configs', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(config)
        });
        
        if (response.ok) {
            alert('Configuration created successfully!');
            document.getElementById('configForm').reset();
            document.getElementById('stepsContainer').innerHTML = '';
            document.getElementById('scheduleFields').innerHTML = '';
            stepCount = 0;
            loadConfigs();
        } else {
            const error = await response.text();
            alert('Failed to create configuration: ' + error);
        }
    } catch (error) {
        alert('Error creating configuration: ' + error.message);
    }
});

// Initial load
loadConfigs();
loadHistory();

// Auto-refresh history every 10 seconds
setInterval(loadHistory, 10000);
```

### static/css/style.css
```css
body {
   background-color: #f8f9fa;
}

.card {
   box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
   transition: transform 0.2s;
}

.card:hover {
   transform: translateY(-5px);
}

.navbar-brand {
   font-weight: bold;
}

#stepsContainer .card {
   background-color: #f1f3f5;
}

.table {
   background-color: white;
}

.badge {
   padding: 0.5em 1em;
}

/* Modal styles */
.modal-xl {
   max-width: 90%;
}

.img-thumbnail {
   transition: transform 0.2s;
}

.img-thumbnail:hover {
   transform: scale(1.05);
}

pre {
   white-space: pre-wrap;
   word-wrap: break-word;
   font-size: 0.875rem;
}

code {
   font-size: 0.875rem;
   color: #d63384;
   background-color: #f8f9fa;
   padding: 2px 4px;
   border-radius: 3px;
}

/* Status badge colors */
.bg-success { background-color: #28a745 !important; }
.bg-danger { background-color: #dc3545 !important; }
.bg-primary { background-color: #007bff !important; }
.bg-secondary { background-color: #6c757d !important; }
```

## Usage Instructions

1. **Build and Run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

2. **Access the application:**
   - Web UI: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console
   - API Documentation: http://localhost:8080/swagger-ui.html (if you add SpringDoc)

3. **Create an automation:**
   - Navigate to the web UI
   - Click "Create New"
   - Add steps (e.g., Navigate → Click → Input → Screenshot)
   - Configure schedule (optional)
   - Save the configuration

4. **Example automation for login:**
   ```
   Step 1: NAVIGATE to https://example.com/login
   Step 2: INPUT username in selector "#username"
   Step 3: INPUT password in selector "#password"
   Step 4: CLICK on selector "#login-button"
   Step 5: WAIT for 2 seconds
   Step 6: SCREENSHOT with selector ".dashboard-info"
   ```

5. **View results:**
   - Check the History tab for execution results
   - Screenshots are saved in the ./screenshots directory
   - Click "Details" to view logs and captured images

## Additional Features to Consider

1. **Email notifications** on automation failure
2. **Webhook integration** for external notifications
3. **Data extraction** capabilities (not just screenshots)
4. **Parallel execution** support
5. **Docker containerization** for easy deployment
6. **API authentication** with Spring Security
7. **Export/Import** configurations
8. **Visual workflow builder** with drag-and-drop

This project provides a solid foundation for web UI automation with scheduling capabilities, history tracking, and a user-friendly interface.