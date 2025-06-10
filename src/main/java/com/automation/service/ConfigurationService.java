package com.automation.service;

import com.automation.model.AutomationConfig;
import com.automation.repository.AutomationConfigRepository;
import com.automation.repository.AutomationResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigurationService {

    private final AutomationConfigRepository configRepository;
    private final AutomationResultRepository resultRepository;
    private final SchedulerService schedulerService;

    @Transactional
    public void deleteConfiguration(Long configId, boolean force) {
        AutomationConfig config = configRepository.findById(configId)
                .orElseThrow(() -> new IllegalArgumentException("Configuration not found"));

        // Unschedule first
        schedulerService.unscheduleAutomation(configId);

        if (force) {
            // Delete associated results and their screenshots
            resultRepository.findAll().stream()
                    .filter(result -> result.getConfig().getId().equals(configId))
                    .forEach(result -> {
                        // Delete screenshot files
                        if (result.getScreenshotPaths() != null) {
                            result.getScreenshotPaths().forEach(screenshotPath -> {
                                try {
                                    Path path = Paths.get(screenshotPath);
                                    Files.deleteIfExists(path);
                                    log.info("Deleted screenshot: " + screenshotPath);
                                } catch (Exception e) {
                                    log.error("Failed to delete screenshot: " + screenshotPath, e);
                                }
                            });
                        }
                    });

            // Delete all results for this config
            resultRepository.deleteByConfigId(configId);
        }

        // Delete the configuration
        configRepository.deleteById(configId);
        log.info("Deleted configuration: " + config.getName());
    }

    public long countResultsForConfig(Long configId) {
        return resultRepository.countByConfigId(configId);
    }
}
