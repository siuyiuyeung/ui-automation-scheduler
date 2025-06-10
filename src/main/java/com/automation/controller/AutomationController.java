package com.automation.controller;

import com.automation.dto.AutomationConfigDTO;
import com.automation.model.AutomationConfig;
import com.automation.model.AutomationResult;
import com.automation.model.AutomationStep;
import com.automation.model.ScheduleConfig;
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