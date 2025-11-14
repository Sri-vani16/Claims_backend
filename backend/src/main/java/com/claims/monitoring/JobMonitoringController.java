package com.claims.monitoring;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
@CrossOrigin(origins = "*")
@Tag(name = "Job Monitoring", description = "Job monitoring and management API")
public class JobMonitoringController {
    
    private final JobMonitoringService jobMonitoringService;
    
    public JobMonitoringController(JobMonitoringService jobMonitoringService) {
        this.jobMonitoringService = jobMonitoringService;
    }
    
    @GetMapping("/status/{jobId}")
    @Operation(summary = "Get job status", description = "Retrieve the status of a specific job")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Job status retrieved"),
        @ApiResponse(responseCode = "404", description = "Job not found")
    })
    public ResponseEntity<JobLog> getJobStatus(
            @Parameter(description = "Job ID") @PathVariable String jobId) {
        JobLog jobLog = jobMonitoringService.getJobStatus(jobId);
        if (jobLog != null) {
            return ResponseEntity.ok(jobLog);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/failed")
    @Operation(summary = "Get failed jobs", description = "Retrieve all failed jobs within specified hours")
    @ApiResponse(responseCode = "200", description = "Failed jobs retrieved")
    public ResponseEntity<List<JobLog>> getFailedJobs(
            @Parameter(description = "Hours to look back") @RequestParam(defaultValue = "24") int hours) {
        List<JobLog> failedJobs = jobMonitoringService.getFailedJobs(hours);
        return ResponseEntity.ok(failedJobs);
    }
    
    @PostMapping("/rerun/{jobId}")
    @Operation(summary = "Rerun job", description = "Mark a job for rerun")
    @ApiResponse(responseCode = "200", description = "Job marked for rerun")
    public ResponseEntity<Map<String, String>> rerunJob(
            @Parameter(description = "Job ID") @PathVariable String jobId) {
        jobMonitoringService.rerunJob(jobId);
        return ResponseEntity.ok(Map.of("message", "Job marked for rerun", "jobId", jobId));
    }
    
    @PostMapping("/assign/{jobId}")
    @Operation(summary = "Assign job", description = "Assign a job to a specific person")
    @ApiResponse(responseCode = "200", description = "Job assigned successfully")
    public ResponseEntity<Map<String, String>> assignJob(
            @Parameter(description = "Job ID") @PathVariable String jobId, 
            @RequestBody Map<String, String> request) {
        String assignee = request.get("assignee");
        jobMonitoringService.assignJob(jobId, assignee);
        return ResponseEntity.ok(Map.of("message", "Job assigned", "jobId", jobId, "assignee", assignee));
    }
    
    @GetMapping("/health")
    @Operation(summary = "System health", description = "Get overall system health status")
    @ApiResponse(responseCode = "200", description = "System health retrieved")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        List<JobLog> recentFailures = jobMonitoringService.getFailedJobs(1);
        
        return ResponseEntity.ok(Map.of(
            "status", recentFailures.isEmpty() ? "HEALTHY" : "DEGRADED",
            "recentFailures", recentFailures.size(),
            "timestamp", java.time.LocalDateTime.now()
        ));
    }
    
    @GetMapping("/jobs")
    @Operation(summary = "Get all jobs", description = "Retrieve all job logs")
    @ApiResponse(responseCode = "200", description = "All jobs retrieved")
    public ResponseEntity<List<JobLog>> getAllJobs() {
        List<JobLog> allJobs = jobMonitoringService.getAllJobs();
        return ResponseEntity.ok(allJobs);
    }
}