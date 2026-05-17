package com.performance.service;

import com.performance.entity.Employee;
import com.performance.storage.DataStorage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Resource
    private DataStorage dataStorage;

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(dataStorage.employees.values());
    }

    public Employee getEmployeeById(Long id) {
        return dataStorage.employees.get(id);
    }

    public Employee createEmployee(Employee employee) {
        employee.setId(dataStorage.employeeIdGenerator.getAndIncrement());
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        dataStorage.employees.put(employee.getId(), employee);
        return employee;
    }

    public Employee updateEmployee(Long id, Employee employee) {
        Employee existing = dataStorage.employees.get(id);
        if (existing == null) {
            return null;
        }
        existing.setName(employee.getName());
        existing.setDepartment(employee.getDepartment());
        existing.setRole(employee.getRole());
        existing.setSupervisorId(employee.getSupervisorId());
        existing.setUpdateTime(LocalDateTime.now());
        dataStorage.employees.put(id, existing);
        return existing;
    }

    public boolean deleteEmployee(Long id) {
        return dataStorage.employees.remove(id) != null;
    }

    public List<Employee> getEmployeesByDepartment(String department) {
        return dataStorage.employees.values().stream()
                .filter(e -> department.equals(e.getDepartment()))
                .collect(Collectors.toList());
    }

    public List<Employee> getEmployeesBySupervisor(Long supervisorId) {
        return dataStorage.employees.values().stream()
                .filter(e -> supervisorId.equals(e.getSupervisorId()))
                .collect(Collectors.toList());
    }
}
