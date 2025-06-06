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