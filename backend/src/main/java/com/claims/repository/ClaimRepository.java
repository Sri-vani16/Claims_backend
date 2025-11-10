package com.claims.repository;

import com.claims.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    
    @Query("SELECT c FROM Claim c WHERE c.policy.policyNumber = :policyNumber AND c.dateOfIncident = :incidentDate")
    List<Claim> findDuplicateClaims(@Param("policyNumber") String policyNumber, @Param("incidentDate") LocalDate incidentDate);
    
    @Query("SELECT COUNT(c) FROM Claim c WHERE c.policy.policyNumber = :policyNumber AND c.submissionDate >= :fromDate")
    Long countClaimsByPolicyInPeriod(@Param("policyNumber") String policyNumber, @Param("fromDate") java.time.LocalDateTime fromDate);
    
    @Query("SELECT c FROM Claim c WHERE c.email = :email OR c.phone = :phone")
    List<Claim> findClaimsByEmailOrPhone(@Param("email") String email, @Param("phone") String phone);
}