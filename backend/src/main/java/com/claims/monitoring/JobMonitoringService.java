package com.claims.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class JobMonitoringService {
    
    private static final Logger log = LoggerFactory.getLogger(JobMonitoringService.class);
    
    private final JobLogRepository jobLogRepository;
    
    public JobMonitoringService(JobLogRepository jobLogRepository) {
        this.jobLogRepository = jobLogRepository;
    }
    
    public String startJob(String jobType, String message) {
        String jobId = UUID.randomUUID().toString();
        JobLog jobLog = new JobLog(jobId, jobType, JobStatus.RUNNING, message);
        jobLogRepository.save(jobLog);
        
        log.info("Job started - ID: {}, Type: {}, Message: {}", jobId, jobType, message);
        return jobId;
    }
    
    public void completeJob(String jobId, String message) {
        JobLog jobLog = jobLogRepository.findByJobId(jobId).orElse(null);
        if (jobLog != null) {
            jobLog.setStatus(JobStatus.COMPLETED);
            jobLog.setEndTime(LocalDateTime.now());
            jobLog.setMessage(message);
            jobLogRepository.save(jobLog);
            
            log.info("Job completed - ID: {}, Message: {}", jobId, message);
        }
    }
    
    public void failJob(String jobId, String errorMessage, Exception exception) {
        JobLog jobLog = jobLogRepository.findByJobId(jobId).orElse(null);
        if (jobLog != null) {
            jobLog.setStatus(JobStatus.FAILED);
            jobLog.setEndTime(LocalDateTime.now());
            jobLog.setMessage(errorMessage);
            jobLog.setErrorDetails(exception != null ? exception.getMessage() : null);
            jobLogRepository.save(jobLog);
            
            log.error("Job failed - ID: {}, Error: {}", jobId, errorMessage, exception);
        }
    }
    
    public JobLog getJobStatus(String jobId) {
        return jobLogRepository.findByJobId(jobId).orElse(null);
    }
    
    public List<JobLog> getFailedJobs(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return jobLogRepository.findFailedJobsSince(since);
    }
    
    public void assignJob(String jobId, String assignee) {
        JobLog jobLog = jobLogRepository.findByJobId(jobId).orElse(null);
        if (jobLog != null) {
            jobLog.setAssignedTo(assignee);
            jobLogRepository.save(jobLog);
            
            log.info("Job assigned - ID: {}, Assignee: {}", jobId, assignee);
        }
    }
    
    public void rerunJob(String jobId) {
        JobLog jobLog = jobLogRepository.findByJobId(jobId).orElse(null);
        if (jobLog != null) {
            jobLog.setStatus(JobStatus.PENDING);
            jobLog.setEndTime(null);
            jobLog.setErrorDetails(null);
            jobLogRepository.save(jobLog);
            
            log.info("Job marked for rerun - ID: {}", jobId);
        }
    }
    
    public void simulateFraudDetectionFailure() {
        String jobId = startJob("FRAUD_DETECTION_FAILURE", "Testing fraud detection engine failure");
        
        try {
            Thread.sleep(200);
            throw new RuntimeException("Fraud rules engine timeout - Rule evaluation exceeded 30 seconds");
        } catch (Exception e) {
            failJob(jobId, "Fraud detection engine failure", e);
        }
    }
}