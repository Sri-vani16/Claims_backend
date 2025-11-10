package com.claims.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "investigators")
public class Investigator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String department;
    
    @Column(nullable = false)
    private Boolean active;
    
    public Investigator() {}
    
    public Investigator(Long id, String name, String email, String department, Boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.department = department;
        this.active = active;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}