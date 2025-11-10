package com.claims.repository;

import com.claims.entity.Investigator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestigatorRepository extends JpaRepository<Investigator, Long> {
    List<Investigator> findByActiveTrue();
}