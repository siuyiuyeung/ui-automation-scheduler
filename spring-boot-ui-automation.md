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
        
        <!-- Scheduling -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-quartz</artifactId>
        </dependency>
        
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
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "automation_configs")
public class AutomationConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "config_id")
    private List<AutomationStep> steps;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "schedule_id")
    private ScheduleConfig schedule;
    
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

@Entity
@Data
@Table(name = "automation_steps")
public class AutomationStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "step_order")
    private int order;
    
    @Enumerated(EnumType.STRING)
    private StepType type;
    
    private String selector; // CSS selector or XPath
    
    private String value; // Input value or URL
    
    @Column(name = "wait_seconds")
    private int waitSeconds = 0;
    
    @Column(name = "capture_screenshot")
    private boolean captureScreenshot = false;
    
    @Column(name = "capture_selector")
    private String captureSelector; // Specific area to capture
    
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
    @JoinColumn(name = "config_id")
    private AutomationConfig config;
    
    @Enumerated(EnumType.STRING)
    private Status status;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(columnDefinition = "TEXT")
    private String logs;
    
    @ElementCollection
    @CollectionTable(name = "result_screenshots")
    private List<String> screenshotPaths;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
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

@Service
public class WebDriverService {
    
    @Value("${automation.screenshot.path:screenshots}")
    private String screenshotPath;
    
    @Value("${automation.driver.headless:false}")
    private boolean headless;
    
    public WebDriver createDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        
        if (headless) {
            options.addArguments("--headless");
        }
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        
        return new ChromeDriver(options);
    }
    
    public String captureScreenshot(WebDriver driver, String selector) throws Exception {
        String fileName = UUID.randomUUID() + ".png";
        Path path = Paths.get(screenshotPath, fileName);
        Files.createDirectories(path.getParent());
        
        if (selector != null && !selector.isEmpty()) {
            WebElement element = driver.findElement(By.cssSelector(selector));
            File screenshot = element.getScreenshotAs(OutputType.FILE);
            Files.copy(screenshot.toPath(), path);
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
        
        switch (step.getType()) {
            case NAVIGATE:
                driver.get(step.getValue());
                logs.append("Navigated to: ").append(step.getValue()).append("\n");
                break;
                
            case CLICK:
                WebElement clickElement = driver.findElement(By.cssSelector(step.getSelector()));
                clickElement.click();
                logs.append("Clicked element: ").append(step.getSelector()).append("\n");
                break;
                
            case INPUT:
                WebElement inputElement = driver.findElement(By.cssSelector(step.getSelector()));
                inputElement.clear();
                inputElement.sendKeys(step.getValue());
                logs.append("Input text to: ").append(step.getSelector()).append("\n");
                break;
                
            case WAIT:
                Thread.sleep(step.getWaitSeconds() * 1000L);
                logs.append("Waited for: ").append(step.getWaitSeconds()).append(" seconds\n");
                break;
                
            case SCREENSHOT:
                String screenshotPath = webDriverService.captureScreenshot(driver, step.getCaptureSelector());
                result.getScreenshotPaths().add(screenshotPath);
                logs.append("Screenshot captured: ").append(screenshotPath).append("\n");
                break;
                
            case SCROLL:
                driver.executeScript("window.scrollTo(0, " + step.getValue() + ")");
                logs.append("Scrolled to position: ").append(step.getValue()).append("\n");
                break;
                
            case SELECT:
                Select select = new Select(driver.findElement(By.cssSelector(step.getSelector())));
                select.selectByValue(step.getValue());
                logs.append("Selected option: ").append(step.getValue()).append("\n");
                break;
        }
        
        if (step.isCaptureScreenshot()) {
            String screenshotPath = webDriverService.captureScreenshot(driver, step.getCaptureSelector());
            result.getScreenshotPaths().add(screenshotPath);
            logs.append("Step screenshot captured: ").append(screenshotPath).append("\n");
        }
        
        if (step.getWaitSeconds() > 0) {
            Thread.sleep(step.getWaitSeconds() * 1000L);
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
import org.quartz.*;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulerService {
    
    private final Scheduler scheduler;
    private final AutomationConfigRepository configRepository;
    
    @PostConstruct
    public void init() {
        try {
            scheduler.start();
            scheduleActiveAutomations();
        } catch (SchedulerException e) {
            log.error("Failed to start scheduler", e);
        }
    }
    
    public void scheduleActiveAutomations() {
        configRepository.findByActiveTrue().forEach(this::scheduleAutomation);
    }
    
    public void scheduleAutomation(AutomationConfig config) {
        if (!config.isActive() || config.getSchedule() == null) {
            return;
        }
        
        try {
            JobDetail jobDetail = JobBuilder.newJob(AutomationJob.class)
                .withIdentity("automation-" + config.getId())
                .usingJobData("configId", config.getId())
                .build();
            
            Trigger trigger = createTrigger(config.getSchedule(), "trigger-" + config.getId());
            
            if (scheduler.checkExists(jobDetail.getKey())) {
                scheduler.deleteJob(jobDetail.getKey());
            }
            
            scheduler.scheduleJob(jobDetail, trigger);
            log.info("Scheduled automation: " + config.getName());
            
        } catch (Exception e) {
            log.error("Failed to schedule automation: " + config.getName(), e);
        }
    }
    
    private Trigger createTrigger(ScheduleConfig schedule, String triggerId) {
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
            .withIdentity(triggerId);
        
        switch (schedule.getType()) {
            case ONCE:
                LocalDateTime runTime = LocalDateTime.parse(schedule.getRunOnceAt());
                Date runDate = Date.from(runTime.atZone(ZoneId.systemDefault()).toInstant());
                return triggerBuilder.startAt(runDate).build();
                
            case INTERVAL:
                return triggerBuilder
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(schedule.getIntervalMinutes())
                        .repeatForever())
                    .build();
                
            case CRON:
                return triggerBuilder
                    .withSchedule(CronScheduleBuilder.cronSchedule(schedule.getCronExpression()))
                    .build();
                
            default:
                throw new IllegalArgumentException("Unknown schedule type: " + schedule.getType());
        }
    }
    
    public void unscheduleAutomation(Long configId) {
        try {
            JobKey jobKey = new JobKey("automation-" + configId);
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
                log.info("Unscheduled automation with id: " + configId);
            }
        } catch (SchedulerException e) {
            log.error("Failed to unschedule automation: " + configId, e);
        }
    }
}
```

### AutomationJob.java
```java
package com.automation.service;

import com.automation.model.AutomationConfig;
import com.automation.repository.AutomationConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AutomationJob implements Job {
    
    @Autowired
    private AutomationService automationService;
    
    @Autowired
    private AutomationConfigRepository configRepository;
    
    @Override
    public void execute(JobExecutionContext context) {
        Long configId = context.getJobDetail().getJobDataMap().getLong("configId");
        log.info("Executing scheduled automation for config id: " + configId);
        
        configRepository.findById(configId).ifPresent(config -> {
            if (config.isActive()) {
                automationService.executeAutomation(config);
            }
        });
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
import java.util.List;

@RestController
@RequestMapping("/api/automation")
@RequiredArgsConstructor
public class AutomationController {
    
    private final AutomationConfigRepository configRepository;
    private final AutomationService automationService;
    private final SchedulerService schedulerService;
    
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
        return configRepository.findById(id)
            .map(config -> {
                config.setName(dto.getName());
                config.setDescription(dto.getDescription());
                config.setSteps(dto.getSteps());
                config.setSchedule(dto.getSchedule());
                config.setActive(dto.isActive());
                
                AutomationConfig saved = configRepository.save(config);
                
                // Reschedule if needed
                schedulerService.unscheduleAutomation(id);
                if (saved.isActive() && saved.getSchedule() != null) {
                    schedulerService.scheduleAutomation(saved);
                }
                
                return ResponseEntity.ok(saved);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/configs/{id}")
    public ResponseEntity<Void> deleteConfig(@PathVariable Long id) {
        if (configRepository.existsById(id)) {
            schedulerService.unscheduleAutomation(id);
            configRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
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
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {
    
    private final AutomationResultRepository resultRepository;
    
    @GetMapping
    public Page<AutomationResult> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long configId,
            @RequestParam(required = false) AutomationResult.Status status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("startTime").descending());
        
        if (configId != null) {
            return resultRepository.findByConfigId(configId, pageRequest);
        } else if (status != null) {
            return resultRepository.findByStatus(status, pageRequest);
        } else if (startDate != null && endDate != null) {
            return resultRepository.findByStartTimeBetween(startDate, endDate, pageRequest);
        } else {
            return resultRepository.findAll(pageRequest);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AutomationResult> getResult(@PathVariable Long id) {
        return resultRepository.findById(id)
            .map(ResponseEntity::ok)
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
  
  h2:
    console:
      enabled: true
      path: /h2-console
  
  quartz:
    job-store-type: jdbc
    properties:
      org:
        quartz:
          scheduler:
            instanceName: AutomationScheduler
            instanceId: AUTO
          jobStore:
            driverDelegateClass: org.quartz.impl.jdbcjobstore.StdJDBCDelegate
            useProperties: false
            tablePrefix: QRTZ_
            isClustered: false
          threadPool:
            threadCount: 10

automation:
  screenshot:
    path: ./screenshots
  driver:
    headless: false
    timeout: 30

server:
  port: 8080

logging:
  level:
    com.automation: DEBUG
    org.springframework.web: INFO
    org.hibernate: WARN
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
    private AutomationResult.Status status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String logs;
    private List<String> screenshotPaths;
    private String errorMessage;
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
        registry.addResourceHandler("/screenshots/**")
                .addResourceLocations("file:./screenshots/");
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
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import javax.sql.DataSource;

@Configuration
public class SchedulerConfig {
    
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
        SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setAutoStartup(true);
        factory.setWaitForJobsToCompleteOnShutdown(true);
        factory.setOverwriteExistingJobs(true);
        return factory;
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
    const response = await fetch('/api/history');
    const data = await response.json();
    
    const tbody = document.getElementById('historyTable');
    tbody.innerHTML = '';
    
    data.content.forEach(result => {
        const duration = result.endTime ? 
            Math.round((new Date(result.endTime) - new Date(result.startTime)) / 1000) + 's' : 
            'Running...';
        
        const row = `
            <tr>
                <td>${result.config.name}</td>
                <td>
                    <span class="badge bg-${getStatusColor(result.status)}">
                        ${result.status}
                    </span>
                </td>
                <td>${new Date(result.startTime).toLocaleString()}</td>
                <td>${duration}</td>
                <td>
                    <button class="btn btn-sm btn-info" onclick="viewDetails(${result.id})">Details</button>
                </td>
            </tr>
        `;
        tbody.innerHTML += row;
    });
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
    if (confirm('Are you sure you want to delete this configuration?')) {
        const response = await fetch(`/api/automation/configs/${configId}`, { method: 'DELETE' });
        if (response.ok) {
            loadConfigs();
        }
    }
}

// Step management
let stepCount = 0;

function addStep() {
    const container = document.getElementById('stepsContainer');
    const stepHtml = `
        <div class="card mb-2" id="step-${stepCount}">
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
                        <input type="text" class="form-control" placeholder="URL" data-field="value">
                    </div>
                    <div class="col-md-1">
                        <button class="btn btn-sm btn-danger" onclick="removeStep(${stepCount})">X</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    container.innerHTML += stepHtml;
    stepCount++;
}

function updateStepFields(stepId) {
    const stepType = document.querySelector(`#step-${stepId} .step-type`).value;
    const fieldsContainer = document.getElementById(`stepFields-${stepId}`);
    
    let fields = '';
    switch(stepType) {
        case 'NAVIGATE':
            fields = '<input type="text" class="form-control" placeholder="URL" data-field="value">';
            break;
        case 'CLICK':
            fields = '<input type="text" class="form-control" placeholder="CSS Selector" data-field="selector">';
            break;
        case 'INPUT':
            fields = `
                <input type="text" class="form-control mb-2" placeholder="CSS Selector" data-field="selector">
                <input type="text" class="form-control" placeholder="Text to input" data-field="value">
            `;
            break;
        case 'WAIT':
            fields = '<input type="number" class="form-control" placeholder="Seconds" data-field="waitSeconds">';
            break;
        case 'SCREENSHOT':
            fields = '<input type="text" class="form-control" placeholder="CSS Selector (optional)" data-field="captureSelector">';
            break;
        case 'SCROLL':
            fields = '<input type="number" class="form-control" placeholder="Scroll position (pixels)" data-field="value">';
            break;
        case 'SELECT':
            fields = `
                <input type="text" class="form-control mb-2" placeholder="CSS Selector" data-field="selector">
                <input type="text" class="form-control" placeholder="Option value" data-field="value">
            `;
            break;
    }
    fieldsContainer.innerHTML = fields;
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
    
    const steps = [];
    document.querySelectorAll('[id^="step-"]').forEach((stepDiv, index) => {
        const type = stepDiv.querySelector('.step-type').value;
        const step = {
            order: index,
            type: type
        };
        
        stepDiv.querySelectorAll('[data-field]').forEach(input => {
            step[input.dataset.field] = input.value;
        });
        
        steps.push(step);
    });
    
    const scheduleType = document.getElementById('scheduleType').value;
    let schedule = null;
    if (scheduleType) {
        schedule = { type: scheduleType };
        switch(scheduleType) {
            case 'ONCE':
                schedule.runOnceAt = document.getElementById('runOnceAt').value;
                break;
            case 'INTERVAL':
                schedule.intervalMinutes = parseInt(document.getElementById('intervalMinutes').value);
                break;
            case 'CRON':
                schedule.cronExpression = document.getElementById('cronExpression').value;
                break;
        }
    }
    
    const config = {
        name: document.getElementById('configName').value,
        description: document.getElementById('configDescription').value,
        steps: steps,
        schedule: schedule,
        active: true
    };
    
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
        loadConfigs();
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