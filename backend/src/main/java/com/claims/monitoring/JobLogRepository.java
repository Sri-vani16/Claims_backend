package com.claims.monitoring;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobLogRepository extends JpaRepository<JobLog, Long> {
    
    Optional<JobLog> findByJobId(String jobId);
    
    @Query("SELECT j FROM JobLog j WHERE j.status = 'FAILED' AND j.startTime >= :since ORDER BY j.startTime DESC")
    List<JobLog> findFailedJobsSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT j FROM JobLog j WHERE j.jobType = :jobType ORDER BY j.startTime DESC")
    List<JobLog> findByJobType(@Param("jobType") String jobType);
    
    @Query("SELECT j FROM JobLog j WHERE j.assignedTo = :assignee AND j.status IN ('FAILED', 'PENDING')")
    List<JobLog> findActiveJobsByAssignee(@Param("assignee") String assignee);
}