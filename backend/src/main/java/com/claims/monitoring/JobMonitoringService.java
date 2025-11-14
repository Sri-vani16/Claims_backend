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
    private final LogReaderService logReaderService;
    
    public JobMonitoringService(JobLogRepository jobLogRepository, LogReaderService logReaderService) {
        this.jobLogRepository = jobLogRepository;
        this.logReaderService = logReaderService;
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
            
            log.info("Job completed - ID: {}, Type: {}, Message: {}", jobId, jobLog.getJobType(), message);
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
            
            log.error("Job failed - ID: {}, Type: {}, Error: {}", jobId, jobLog.getJobType(), errorMessage, exception);
            logReaderService.appendJobLog(jobId, jobLog.getJobType(), "ERROR", errorMessage, exception);
        }
    }
    
    public JobLog getJobStatus(String jobId) {
        JobLog jobLog = jobLogRepository.findByJobId(jobId).orElse(null);
        if (jobLog != null && jobLog.getStatus() == JobStatus.FAILED) {
            String logDetails = logReaderService.getErrorLogs(jobId, jobLog.getStartTime(), jobLog.getEndTime());
            if (logDetails != null) {
                jobLog.setErrorDetails(logDetails);
            }
        }
        return jobLog;
    }
    
    public List<JobLog> getFailedJobs(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        List<JobLog> failedJobs = jobLogRepository.findFailedJobsSince(since);
        
        for (JobLog job : failedJobs) {
            String logDetails = logReaderService.getErrorLogs(job.getJobId(), job.getStartTime(), job.getEndTime());
            if (logDetails != null) {
                job.setErrorDetails(logDetails);
            }
        }
        return failedJobs;
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
    
    public List<JobLog> getAllJobs() {
        return jobLogRepository.findAll();
    }
}