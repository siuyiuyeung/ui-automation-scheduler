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