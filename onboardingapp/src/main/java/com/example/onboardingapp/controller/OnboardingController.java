package com.example.onboardingapp.controller;

import com.example.onboardingapp.model.Employee;
import com.example.onboardingapp.repository.EmployeeRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class OnboardingController {

    private final EmployeeRepository employeeRepository;

    public OnboardingController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // 🏠 Home form
    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "index";
    }

    // ➕ Create / Submit form
    @PostMapping("/submit")
    public String submitForm(@ModelAttribute("employee") Employee employee,
                             RedirectAttributes redirectAttributes) {
        employeeRepository.save(employee);
        redirectAttributes.addFlashAttribute("message", "Employee added successfully!");
        return "redirect:/employees";
    }

    // 📋 View all employees
    @GetMapping("/employees")
    public String listEmployees(Model model) {
        List<Employee> employees = employeeRepository.findAll();
        model.addAttribute("employees", employees);
        return "employees";
    }

    // ✏️ Edit employee
    @GetMapping("/edit/{id}")
    public String editEmployee(@PathVariable("id") Long id, Model model) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isPresent()) {
            model.addAttribute("employee", employeeOpt.get());
            return "edit";
        } else {
            return "redirect:/employees";
        }
    }

    // 💾 Update employee
    @PostMapping("/update/{id}")
    public String updateEmployee(@PathVariable("id") Long id,
                                 @ModelAttribute("employee") Employee updatedEmployee,
                                 RedirectAttributes redirectAttributes) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isPresent()) {
            Employee existing = employeeOpt.get();
            existing.setFirstName(updatedEmployee.getFirstName());
            existing.setLastName(updatedEmployee.getLastName());
            existing.setEmail(updatedEmployee.getEmail());
            existing.setPhone(updatedEmployee.getPhone());
            existing.setDepartment(updatedEmployee.getDepartment());
            employeeRepository.save(existing);
            redirectAttributes.addFlashAttribute("message", "Employee updated successfully!");
        }
        return "redirect:/employees";
    }

    // ❌ Delete employee
    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable("id") Long id,
                                 RedirectAttributes redirectAttributes) {
        employeeRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Employee deleted successfully!");
        return "redirect:/employees";
    }
}