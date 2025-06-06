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