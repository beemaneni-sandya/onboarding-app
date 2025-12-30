package com.example.onboardingapp.repository;

import com.example.onboardingapp.model.Timesheet;
import com.example.onboardingapp.model.TimesheetStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimesheetRepository extends JpaRepository<Timesheet, Long> {

    // Existing method – get all timesheets for a given employee
    List<Timesheet> findByEmployeeId(Long employeeId);

    // ✅ Optional: get all timesheets for an employee by status (e.g., pending review)
    List<Timesheet> findByEmployeeIdAndStatus(Long employeeId, TimesheetStatus status);

    // ✅ Optional: get all timesheets by status (useful for manager dashboards)
    List<Timesheet> findByStatus(TimesheetStatus status);
}
