package com.example.onboardingapp.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "timesheets")
public class Timesheet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private LocalDate date;
    private Double hours;
    private String description;

    // ✅ New fields for approval workflow
    @Enumerated(EnumType.STRING)
    @Column(length = 500)
    private TimesheetStatus status = TimesheetStatus.PENDING; // Default to pending

    private String managerComments;

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getHours() {
        return hours;
    }
    public void setHours(Double hours) {
        this.hours = hours;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public TimesheetStatus getStatus() {
        return status;
    }
    public void setStatus(TimesheetStatus status) {
        this.status = status;
    }

    public String getManagerComments() {
        return managerComments;
    }
    public void setManagerComments(String managerComments) {
        this.managerComments = managerComments;
    }
}
