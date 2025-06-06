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