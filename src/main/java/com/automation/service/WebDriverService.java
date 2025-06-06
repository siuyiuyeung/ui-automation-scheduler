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