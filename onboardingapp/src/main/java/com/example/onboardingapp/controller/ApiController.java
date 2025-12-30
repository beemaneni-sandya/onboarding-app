package com.example.onboardingapp.controller;

import com.example.onboardingapp.model.Employee;
import com.example.onboardingapp.model.Timesheet;
import com.example.onboardingapp.repository.EmployeeRepository;
import com.example.onboardingapp.repository.TimesheetRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final EmployeeRepository employeeRepository;
    private final TimesheetRepository timesheetRepository;

    public ApiController(EmployeeRepository employeeRepository, TimesheetRepository timesheetRepository) {
        this.employeeRepository = employeeRepository;
        this.timesheetRepository = timesheetRepository;
    }

    @GetMapping("/employees")
    public List<Map<String, Object>> getEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream().map(emp -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", emp.getId());
            m.put("firstName", emp.getFirstName());
            m.put("lastName", emp.getLastName());
            m.put("email", emp.getEmail());
            m.put("phone", emp.getPhone());
            m.put("department", emp.getDepartment());
            return m;
        }).collect(Collectors.toList());
    }

    @GetMapping("/employees/{employeeId}/timesheets")
    public ResponseEntity<?> getTimesheets(@PathVariable Long employeeId) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) return ResponseEntity.notFound().build();
        List<Timesheet> timesheets = timesheetRepository.findByEmployeeId(employeeId);
        List<Map<String, Object>> out = timesheets.stream().map(ts -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", ts.getId());
            m.put("date", ts.getDate());
            m.put("hours", ts.getHours());
            m.put("description", ts.getDescription());
            m.put("employeeId", ts.getEmployee().getId());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    @PostMapping("/employees/{employeeId}/timesheets")
    public ResponseEntity<?> createTimesheet(@PathVariable Long employeeId,
                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                                             @RequestParam Double hours,
                                             @RequestParam(required = false) String description) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) return ResponseEntity.notFound().build();
        Timesheet ts = new Timesheet();
        ts.setEmployee(empOpt.get());
        ts.setDate(date != null ? date : LocalDate.now());
        ts.setHours(hours);
        ts.setDescription(description);
        timesheetRepository.save(ts);
        Map<String, Object> m = new HashMap<>();
        m.put("id", ts.getId());
        m.put("date", ts.getDate());
        m.put("hours", ts.getHours());
        m.put("description", ts.getDescription());
        return ResponseEntity.ok(m);
    }

    @DeleteMapping("/timesheets/{id}")
    public ResponseEntity<?> deleteTimesheetApi(@PathVariable Long id) {
        Optional<Timesheet> tsOpt = timesheetRepository.findById(id);
        if (tsOpt.isEmpty()) return ResponseEntity.notFound().build();
        timesheetRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

