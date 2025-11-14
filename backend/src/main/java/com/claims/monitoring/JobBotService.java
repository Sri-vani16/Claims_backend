package com.claims.monitoring;

import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobBotService {
    
    private final JobMonitoringService jobMonitoringService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public JobBotService(JobMonitoringService jobMonitoringService) {
        this.jobMonitoringService = jobMonitoringService;
    }
    
    public String processQuery(String query) {
        String lowerQuery = query.toLowerCase();
        
        if (lowerQuery.contains("failed") && (lowerQuery.contains("24") || lowerQuery.contains("last"))) {
            return getFailedJobsResponse(24);
        } else if (lowerQuery.contains("failed")) {
            return getFailedJobsResponse(24);
        } else if (lowerQuery.contains("status") && extractJobId(query) != null) {
            return getJobStatusResponse(extractJobId(query));
        } else if (lowerQuery.contains("rerun") && extractJobId(query) != null) {
            return rerunJobResponse(extractJobId(query));
        } else {
            return "I can help you with:\n" +
                   "• 'List all failed jobs in last 24 hours'\n" +
                   "• 'Get status of job JOB-001'\n" +
                   "• 'Rerun job JOB-002'";
        }
    }
    
    private String getFailedJobsResponse(int hours) {
        List<JobLog> failedJobs = jobMonitoringService.getFailedJobs(hours);
        
        if (failedJobs.isEmpty()) {
            return "✅ No failed jobs found in the last " + hours + " hours.";
        }
        
        StringBuilder response = new StringBuilder();
        response.append("❌ Found ").append(failedJobs.size()).append(" failed jobs in the last ").append(hours).append(" hours:\n\n");
        
        for (JobLog job : failedJobs) {
            response.append("🔸 **Job ID:** ").append(job.getJobId()).append("\n");
            response.append("   **Type:** ").append(job.getJobType()).append("\n");
            response.append("   **Failed At:** ").append(job.getEndTime().format(formatter)).append("\n");
            response.append("   **Reason:** ").append(job.getMessage()).append("\n");
            if (job.getErrorDetails() != null) {
                response.append("   **Details:** ").append(job.getErrorDetails()).append("\n");
            }
            response.append("\n");
        }
        
        return response.toString();
    }
    
    private String getJobStatusResponse(String jobId) {
        JobLog job = jobMonitoringService.getJobStatus(jobId);
        
        if (job == null) {
            return "❌ Job not found: " + jobId;
        }
        
        String statusIcon = getStatusIcon(job.getStatus());
        StringBuilder response = new StringBuilder();
        response.append(statusIcon).append(" **Job Status for ").append(jobId).append("**\n\n");
        response.append("**Type:** ").append(job.getJobType()).append("\n");
        response.append("**Status:** ").append(job.getStatus()).append("\n");
        response.append("**Started:** ").append(job.getStartTime().format(formatter)).append("\n");
        
        if (job.getEndTime() != null) {
            response.append("**Ended:** ").append(job.getEndTime().format(formatter)).append("\n");
        }
        
        response.append("**Message:** ").append(job.getMessage()).append("\n");
        
        if (job.getErrorDetails() != null) {
            response.append("**Error Details:** ").append(job.getErrorDetails()).append("\n");
        }
        
        if (job.getAssignedTo() != null) {
            response.append("**Assigned To:** ").append(job.getAssignedTo()).append("\n");
        }
        
        return response.toString();
    }
    
    private String rerunJobResponse(String jobId) {
        JobLog job = jobMonitoringService.getJobStatus(jobId);
        
        if (job == null) {
            return "❌ Job not found: " + jobId;
        }
        
        jobMonitoringService.rerunJob(jobId);
        return "✅ Job " + jobId + " has been marked for rerun.";
    }
    
    private String getStatusIcon(JobStatus status) {
        switch (status) {
            case COMPLETED: return "✅";
            case FAILED: return "❌";
            case RUNNING: return "🔄";
            case PENDING: return "⏳";
            case CANCELLED: return "🚫";
            default: return "❓";
        }
    }
    
    private String extractJobId(String query) {
        String[] words = query.split("\\s+");
        for (String word : words) {
            // Match UUID pattern or JOB-xxx pattern
            if (word.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}") || 
                word.matches("JOB-\\d+")) {
                return word;
            }
        }
        return null;
    }
}