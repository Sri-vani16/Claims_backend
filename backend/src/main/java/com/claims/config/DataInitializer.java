package com.claims.config;

import com.claims.entity.Investigator;
import com.claims.entity.Policy;
import com.claims.repository.InvestigatorRepository;
import com.claims.repository.PolicyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final PolicyRepository policyRepository;
    private final InvestigatorRepository investigatorRepository;
    
    public DataInitializer(PolicyRepository policyRepository, InvestigatorRepository investigatorRepository) {
        this.policyRepository = policyRepository;
        this.investigatorRepository = investigatorRepository;
    }
    
    @Override
    public void run(String... args) {
        initializePolicies();
        initializeInvestigators();
    }
    
    private void initializePolicies() {
        if (policyRepository.count() == 0) {
            // Health Insurance Policy (matching use case sample)
            Policy healthPolicy1 = new Policy();
            healthPolicy1.setPolicyNumber("INS-123456789");
            healthPolicy1.setPolicyType("Health");
            healthPolicy1.setStartDate(LocalDate.now().minusYears(1));
            healthPolicy1.setEndDate(LocalDate.now().plusYears(2));
            healthPolicy1.setSumInsured(new BigDecimal("500000"));
            healthPolicy1.setStatus("ACTIVE");
            
            // Auto Insurance Policies
            Policy autoPolicy1 = new Policy();
            autoPolicy1.setPolicyNumber("POL12345678");
            autoPolicy1.setPolicyType("Auto");
            autoPolicy1.setStartDate(LocalDate.now().minusYears(1));
            autoPolicy1.setEndDate(LocalDate.now().plusYears(1));
            autoPolicy1.setSumInsured(new BigDecimal("750000"));
            autoPolicy1.setStatus("ACTIVE");
            autoPolicy1.setVehicleRegistration("ABC123");
            autoPolicy1.setVehicleModel("Honda Civic");
            autoPolicy1.setVehicleMake("Honda");
            autoPolicy1.setVehicleYear(2020);
            
            Policy autoPolicy2 = new Policy();
            autoPolicy2.setPolicyNumber("POL87654321");
            autoPolicy2.setPolicyType("Auto");
            autoPolicy2.setStartDate(LocalDate.now().minusMonths(1));
            autoPolicy2.setEndDate(LocalDate.now().plusMonths(23));
            autoPolicy2.setSumInsured(new BigDecimal("800000"));
            autoPolicy2.setStatus("ACTIVE");
            autoPolicy2.setVehicleRegistration("XYZ789");
            autoPolicy2.setVehicleModel("Toyota Camry");
            autoPolicy2.setVehicleMake("Toyota");
            autoPolicy2.setVehicleYear(2021);
            
            // Home Insurance Policy
            Policy homePolicy1 = new Policy();
            homePolicy1.setPolicyNumber("POL11111111");
            homePolicy1.setPolicyType("Home");
            homePolicy1.setStartDate(LocalDate.now().minusMonths(6));
            homePolicy1.setEndDate(LocalDate.now().plusMonths(18));
            homePolicy1.setSumInsured(new BigDecimal("1500000"));
            homePolicy1.setStatus("ACTIVE");
            
            policyRepository.save(healthPolicy1);
            policyRepository.save(autoPolicy1);
            policyRepository.save(autoPolicy2);
            policyRepository.save(homePolicy1);
        }
    }
    
    private void initializeInvestigators() {
        if (investigatorRepository.count() == 0) {
            Investigator inv1 = new Investigator();
            inv1.setName("John Smith");
            inv1.setEmail("john.smith@company.com");
            inv1.setDepartment("Fraud Investigation");
            inv1.setActive(true);
            
            Investigator inv2 = new Investigator();
            inv2.setName("Sarah Johnson");
            inv2.setEmail("sarah.johnson@company.com");
            inv2.setDepartment("Claims Review");
            inv2.setActive(true);
            
            Investigator inv3 = new Investigator();
            inv3.setName("Mike Davis");
            inv3.setEmail("mike.davis@company.com");
            inv3.setDepartment("Risk Assessment");
            inv3.setActive(true);
            
            Investigator inv4 = new Investigator();
            inv4.setName("Srivani Vadthya");
            inv4.setEmail("srivanivadthya@gmail.com");
            inv4.setDepartment("Fraud Investigation");
            inv4.setActive(true);
            
            investigatorRepository.save(inv1);
            investigatorRepository.save(inv2);
            investigatorRepository.save(inv3);
            investigatorRepository.save(inv4);
        }
    }
}