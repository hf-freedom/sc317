package com.performance.controller;

import com.performance.common.Result;
import com.performance.entity.Employee;
import com.performance.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;

    @GetMapping
    public Result<List<Employee>> getAllEmployees() {
        return Result.success(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public Result<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            return Result.error("员工不存在");
        }
        return Result.success(employee);
    }

    @PostMapping
    public Result<Employee> createEmployee(@RequestBody Employee employee) {
        return Result.success(employeeService.createEmployee(employee));
    }

    @PutMapping("/{id}")
    public Result<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        Employee updated = employeeService.updateEmployee(id, employee);
        if (updated == null) {
            return Result.error("员工不存在");
        }
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteEmployee(@PathVariable Long id) {
        boolean deleted = employeeService.deleteEmployee(id);
        if (!deleted) {
            return Result.error("员工不存在");
        }
        return Result.success();
    }

    @GetMapping("/department/{department}")
    public Result<List<Employee>> getEmployeesByDepartment(@PathVariable String department) {
        return Result.success(employeeService.getEmployeesByDepartment(department));
    }

    @GetMapping("/supervisor/{supervisorId}")
    public Result<List<Employee>> getEmployeesBySupervisor(@PathVariable Long supervisorId) {
        return Result.success(employeeService.getEmployeesBySupervisor(supervisorId));
    }
}
