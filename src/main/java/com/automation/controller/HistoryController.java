package com.automation.controller;

import com.automation.model.AutomationResult;
import com.automation.repository.AutomationResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {
    
    private final AutomationResultRepository resultRepository;
    
    @GetMapping
    public Page<AutomationResult> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long configId,
            @RequestParam(required = false) AutomationResult.Status status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("startTime").descending());
        
        if (configId != null) {
            return resultRepository.findByConfigId(configId, pageRequest);
        } else if (status != null) {
            return resultRepository.findByStatus(status, pageRequest);
        } else if (startDate != null && endDate != null) {
            return resultRepository.findByStartTimeBetween(startDate, endDate, pageRequest);
        } else {
            return resultRepository.findAll(pageRequest);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AutomationResult> getResult(@PathVariable Long id) {
        return resultRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResult(@PathVariable Long id) {
        if (resultRepository.existsById(id)) {
            resultRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
} 