package com.claims.monitoring;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
@CrossOrigin(origins = "*")
public class JobMonitoringController {
    
    private final JobMonitoringService jobMonitoringService;
    
    public JobMonitoringController(JobMonitoringService jobMonitoringService) {
        this.jobMonitoringService = jobMonitoringService;
    }
    
    @GetMapping("/status/{jobId}")
    public ResponseEntity<JobLog> getJobStatus(@PathVariable String jobId) {
        JobLog jobLog = jobMonitoringService.getJobStatus(jobId);
        if (jobLog != null) {
            return ResponseEntity.ok(jobLog);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/failed")
    public ResponseEntity<List<JobLog>> getFailedJobs(@RequestParam(defaultValue = "24") int hours) {
        List<JobLog> failedJobs = jobMonitoringService.getFailedJobs(hours);
        return ResponseEntity.ok(failedJobs);
    }
    
    @PostMapping("/rerun/{jobId}")
    public ResponseEntity<Map<String, String>> rerunJob(@PathVariable String jobId) {
        jobMonitoringService.rerunJob(jobId);
        return ResponseEntity.ok(Map.of("message", "Job marked for rerun", "jobId", jobId));
    }
    
    @PostMapping("/assign/{jobId}")
    public ResponseEntity<Map<String, String>> assignJob(
            @PathVariable String jobId, 
            @RequestBody Map<String, String> request) {
        String assignee = request.get("assignee");
        jobMonitoringService.assignJob(jobId, assignee);
        return ResponseEntity.ok(Map.of("message", "Job assigned", "jobId", jobId, "assignee", assignee));
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        List<JobLog> recentFailures = jobMonitoringService.getFailedJobs(1);
        
        return ResponseEntity.ok(Map.of(
            "status", recentFailures.isEmpty() ? "HEALTHY" : "DEGRADED",
            "recentFailures", recentFailures.size(),
            "timestamp", java.time.LocalDateTime.now()
        ));
    }
}