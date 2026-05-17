package com.performance.config;

import com.performance.entity.Employee;
import com.performance.entity.PerformanceCycle;
import com.performance.storage.DataStorage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Resource
    private DataStorage dataStorage;

    @Override
    public void run(String... args) {
        initEmployees();
        initCycles();
    }

    private void initEmployees() {
        if (!dataStorage.employees.isEmpty()) {
            return;
        }

        Employee admin = new Employee();
        admin.setId(dataStorage.employeeIdGenerator.getAndIncrement());
        admin.setName("张经理");
        admin.setDepartment("技术部");
        admin.setRole("MANAGER");
        admin.setSupervisorId(null);
        admin.setCreateTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        dataStorage.employees.put(admin.getId(), admin);

        String[] names = {"李四", "王五", "赵六", "钱七", "孙八"};
        String[] depts = {"技术部", "产品部", "技术部", "运营部", "市场部"};
        for (int i = 0; i < names.length; i++) {
            Employee emp = new Employee();
            emp.setId(dataStorage.employeeIdGenerator.getAndIncrement());
            emp.setName(names[i]);
            emp.setDepartment(depts[i]);
            emp.setRole("EMPLOYEE");
            emp.setSupervisorId(admin.getId());
            emp.setCreateTime(LocalDateTime.now());
            emp.setUpdateTime(LocalDateTime.now());
            dataStorage.employees.put(emp.getId(), emp);
        }
    }

    private void initCycles() {
        if (!dataStorage.cycles.isEmpty()) {
            return;
        }

        PerformanceCycle cycle = new PerformanceCycle();
        cycle.setId(dataStorage.cycleIdGenerator.getAndIncrement());
        cycle.setName("2024年Q2绩效周期");
        cycle.setDescription("2024年第二季度绩效考核");
        cycle.setStartDate(LocalDateTime.of(2024, 4, 1, 0, 0));
        cycle.setEndDate(LocalDateTime.of(2024, 6, 30, 23, 59));
        cycle.setStatus("ACTIVE");
        cycle.setCreatedBy(1L);
        cycle.setBonusPool(new BigDecimal("100000"));
        cycle.setCreateTime(LocalDateTime.now());
        cycle.setUpdateTime(LocalDateTime.now());
        dataStorage.cycles.put(cycle.getId(), cycle);
    }
}
