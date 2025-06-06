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