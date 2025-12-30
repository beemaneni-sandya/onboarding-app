package com.example.onboardingapp.controller;

import com.example.onboardingapp.model.Employee;
import com.example.onboardingapp.model.Timesheet;
import com.example.onboardingapp.model.TimesheetStatus;
import com.example.onboardingapp.repository.EmployeeRepository;
import com.example.onboardingapp.repository.TimesheetRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class TimesheetController {

    private final TimesheetRepository timesheetRepository;
    private final EmployeeRepository employeeRepository;

    public TimesheetController(TimesheetRepository timesheetRepository, EmployeeRepository employeeRepository) {
        this.timesheetRepository = timesheetRepository;
        this.employeeRepository = employeeRepository;
    }

    // List all timesheets for an employee
    @GetMapping("/employees/{employeeId}/timesheets")
    public String listTimesheets(@PathVariable("employeeId") Long employeeId,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Employee not found");
            return "redirect:/employees";
        }

        Employee employee = empOpt.get();
        List<Timesheet> timesheets = timesheetRepository.findByEmployeeId(employeeId);

        model.addAttribute("employee", employee);
        model.addAttribute("timesheets", timesheets);
        return "timesheets";
    }

    // Show form to create a new timesheet for an employee
    @GetMapping("/employees/{employeeId}/timesheets/new")
    public String newTimesheetForm(@PathVariable("employeeId") Long employeeId,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Employee not found");
            return "redirect:/employees";
        }

        Timesheet timesheet = new Timesheet();
        timesheet.setDate(LocalDate.now());

        model.addAttribute("timesheet", timesheet);
        model.addAttribute("employee", empOpt.get());
        return "timesheet_form";
    }

    // Save new timesheet
    @PostMapping("/employees/{employeeId}/timesheets")
    public String saveTimesheet(@PathVariable("employeeId") Long employeeId,
                                @ModelAttribute("timesheet") Timesheet timesheet,
                                RedirectAttributes redirectAttributes) {
        Optional<Employee> empOpt = employeeRepository.findById(employeeId);
        if (empOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Employee not found");
            return "redirect:/employees";
        }

        timesheet.setEmployee(empOpt.get());
        // Default status
        timesheet.setStatus(TimesheetStatus.PENDING);

        timesheetRepository.save(timesheet);
        redirectAttributes.addFlashAttribute("message", "Timesheet saved successfully!");
        return "redirect:/employees/" + employeeId + "/timesheets";
    }

    // Delete timesheet
    @GetMapping("/timesheets/delete/{id}")
    public String deleteTimesheet(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Optional<Timesheet> tsOpt = timesheetRepository.findById(id);
        if (tsOpt.isPresent()) {
            Long empId = tsOpt.get().getEmployee().getId();
            timesheetRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "Timesheet deleted successfully!");
            return "redirect:/employees/" + empId + "/timesheets";
        }
        redirectAttributes.addFlashAttribute("message", "Timesheet not found");
        return "redirect:/employees";
    }

    // ✅ Approve timesheet
    @PostMapping("/timesheets/{id}/approve")
    public String approveTimesheet(@PathVariable("id") Long id,
                                   @RequestParam(value = "comments", required = false) String comments,
                                   RedirectAttributes redirectAttributes) {
        Optional<Timesheet> tsOpt = timesheetRepository.findById(id);
        if (tsOpt.isPresent()) {
            Timesheet ts = tsOpt.get();
            ts.setStatus(TimesheetStatus.APPROVED);
            ts.setManagerComments(comments);
            timesheetRepository.save(ts);

            redirectAttributes.addFlashAttribute("message", "Timesheet approved successfully!");
            return "redirect:/employees/" + ts.getEmployee().getId() + "/timesheets";
        }
        redirectAttributes.addFlashAttribute("message", "Timesheet not found");
        return "redirect:/employees";
    }

    // ✅ Reject timesheet
    @PostMapping("/timesheets/{id}/reject")
    public String rejectTimesheet(@PathVariable("id") Long id,
                                  @RequestParam(value = "comments", required = false) String comments,
                                  RedirectAttributes redirectAttributes) {
        Optional<Timesheet> tsOpt = timesheetRepository.findById(id);
        if (tsOpt.isPresent()) {
            Timesheet ts = tsOpt.get();
            ts.setStatus(TimesheetStatus.REJECTED);
            ts.setManagerComments(comments);
            timesheetRepository.save(ts);

            redirectAttributes.addFlashAttribute("message", "Timesheet rejected successfully!");
            return "redirect:/employees/" + ts.getEmployee().getId() + "/timesheets";
        }
        redirectAttributes.addFlashAttribute("message", "Timesheet not found");
        return "redirect:/employees";
    }
}
