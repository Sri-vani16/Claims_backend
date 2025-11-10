package com.claims.repository;

import com.claims.entity.FraudFlag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudFlagRepository extends JpaRepository<FraudFlag, Long> {
}