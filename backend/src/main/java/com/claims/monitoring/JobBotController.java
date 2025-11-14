package com.claims.monitoring;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bot")
@CrossOrigin(origins = "*")
public class JobBotController {
    
    private final JobBotService jobBotService;
    
    public JobBotController(JobBotService jobBotService) {
        this.jobBotService = jobBotService;
    }
    
    @PostMapping("/query")
    public ResponseEntity<Map<String, String>> processQuery(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        String response = jobBotService.processQuery(query);
        
        return ResponseEntity.ok(Map.of(
            "query", query,
            "response", response
        ));
    }
    
    @GetMapping("/help")
    public ResponseEntity<Map<String, Object>> getHelp() {
        return ResponseEntity.ok(Map.of(
            "commands", new String[]{
                "List all failed jobs in last 24 hours",
                "Get status of job JOB-001", 
                "Rerun job JOB-002"
            },
            "examples", Map.of(
                "failed_jobs", "Show me all failed jobs",
                "job_status", "What's the status of JOB-003?",
                "rerun", "Rerun job JOB-004"
            )
        ));
    }
}