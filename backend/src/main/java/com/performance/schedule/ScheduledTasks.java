package com.performance.schedule;

import com.performance.entity.CycleStatistics;
import com.performance.entity.Employee;
import com.performance.entity.PerformanceCycle;
import com.performance.entity.Task;
import com.performance.service.StatisticsService;
import com.performance.storage.DataStorage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScheduledTasks {

    @Resource
    private DataStorage dataStorage;

    @Resource
    private StatisticsService statisticsService;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ScheduledTasks.class);

    @Scheduled(fixedRate = 60000)
    public void calculateCycleProgress() {
        List<PerformanceCycle> activeCycles = dataStorage.cycles.values().stream()
                .filter(c -> "ACTIVE".equals(c.getStatus()))
                .collect(Collectors.toList());

        for (PerformanceCycle cycle : activeCycles) {
            List<CycleStatistics> allStats = new ArrayList<>();
            List<Employee> employees = new ArrayList<>(dataStorage.employees.values());

            for (Employee emp : employees) {
                if ("EMPLOYEE".equals(emp.getRole())) {
                    CycleStatistics stats = statisticsService.calculateEmployeePerformance(cycle.getId(), emp.getId());
                    dataStorage.statistics.put(stats.getId(), stats);
                    allStats.add(stats);
                }
            }
            log.info("周期 {} 进度统计完成，共 {} 名员工", cycle.getName(), allStats.size());
        }
    }

    @Scheduled(fixedRate = 120000)
    public void detectRiskEmployees() {
        List<PerformanceCycle> activeCycles = dataStorage.cycles.values().stream()
                .filter(c -> "ACTIVE".equals(c.getStatus()))
                .collect(Collectors.toList());

        for (PerformanceCycle cycle : activeCycles) {
            List<CycleStatistics> riskEmployees = statisticsService.getRiskEmployees(cycle.getId());
            if (!riskEmployees.isEmpty()) {
                log.warn("检测到周期 {} 有 {} 名风险员工", cycle.getName(), riskEmployees.size());
                for (CycleStatistics stats : riskEmployees) {
                    Employee emp = dataStorage.employees.get(stats.getEmployeeId());
                    if (emp != null) {
                        log.warn("  - 员工: {}, 风险等级: {}, 加权分数: {}",
                                emp.getName(), stats.getRiskLevel(), stats.getWeightedScore());
                    }
                }
            }
        }
    }

    @Scheduled(fixedRate = 90000)
    public void checkPendingConfirmationTasks() {
        List<Task> pendingTasks = statisticsService.getTasksPendingConfirmation();
        if (!pendingTasks.isEmpty()) {
            log.info("当前有 {} 个任务等待确认", pendingTasks.size());
            for (Task task : pendingTasks) {
                log.info("  - 任务: {}, 负责人ID: {}, 是否跨部门: {}",
                        task.getName(), task.getOwnerId(), task.getIsCrossDepartment());
            }
        }
    }
}
