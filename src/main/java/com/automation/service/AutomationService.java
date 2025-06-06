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
                ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, " + step.getValue() + ")");
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