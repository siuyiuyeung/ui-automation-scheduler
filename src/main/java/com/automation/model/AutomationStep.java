package com.automation.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "automation_steps")
public class AutomationStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "step_order")
    private int order;
    
    @Enumerated(EnumType.STRING)
    private StepType type;
    
    private String selector; // CSS selector or XPath
    
    private String value; // Input value or URL
    
    @Column(name = "wait_seconds")
    private int waitSeconds = 0;
    
    @Column(name = "capture_screenshot")
    private boolean captureScreenshot = false;
    
    @Column(name = "capture_selector")
    private String captureSelector; // Specific area to capture
    
    public enum StepType {
        NAVIGATE, CLICK, INPUT, WAIT, SCREENSHOT, SCROLL, SELECT
    }
} 