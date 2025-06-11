package com.automation.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Data
@Table(name = "automation_steps")
@EqualsAndHashCode(exclude = {"config"})
public class AutomationStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "step_order")
    private int order;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private StepType type;

    @Column(name = "selector")
    private String selector; // CSS selector or XPath

    @Column(name = "input_value")
    private String value; // Input value or URL

    @Column(name = "wait_seconds")
    private int waitSeconds = 0;

    @Column(name = "capture_screenshot")
    private boolean captureScreenshot = false;

    @Column(name = "capture_selector")
    private String captureSelector; // Specific area to capture

    @ManyToOne
    @JoinColumn(name = "config_id")
    @JsonBackReference
    private AutomationConfig config;

    public enum StepType {
        NAVIGATE, CLICK, INPUT, WAIT, SCREENSHOT, SCROLL, SELECT
    }
}