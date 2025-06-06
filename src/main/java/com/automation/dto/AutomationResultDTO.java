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