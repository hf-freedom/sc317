package com.performance.service;

import com.performance.entity.*;
import com.performance.storage.DataStorage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatisticsService {

    @Resource
    private DataStorage dataStorage;

    @Resource
    private ObjectiveService objectiveService;

    @Resource
    private TaskService taskService;

    public CycleStatistics calculateEmployeePerformance(Long cycleId, Long employeeId) {
        List<Objective> objectives = objectiveService.getObjectivesByCycleAndEmployee(cycleId, employeeId);
        List<Task> allTasks = new ArrayList<>();
        for (Objective obj : objectives) {
            allTasks.addAll(taskService.getTasksByObjectiveId(obj.getId()));
        }

        BigDecimal totalWeightedScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;
        int completedTasks = 0;
        int delayedTasks = 0;
        BigDecimal totalDelayPenalty = BigDecimal.ZERO;

        for (Objective obj : objectives) {
            BigDecimal objProgress = calculateObjectiveProgress(obj.getId());
            BigDecimal weightedScore = obj.getTargetScore() != null ?
                    obj.getTargetScore().multiply(objProgress).multiply(obj.getWeight()) : BigDecimal.ZERO;
            totalWeightedScore = totalWeightedScore.add(weightedScore);
            totalWeight = totalWeight.add(obj.getWeight());
        }

        for (Task task : allTasks) {
            if ("COMPLETED".equals(task.getStatus())) {
                completedTasks++;
            }
            if (task.getDelayPenalty() != null && task.getDelayPenalty().compareTo(BigDecimal.ZERO) > 0) {
                delayedTasks++;
                totalDelayPenalty = totalDelayPenalty.add(task.getDelayPenalty());
            }
        }

        int pendingConfirmations = dataStorage.taskConfirmations.values().stream()
                .filter(c -> "PENDING".equals(c.getStatus()))
                .filter(c -> {
                    Task t = dataStorage.tasks.get(c.getTaskId());
                    if (t == null) return false;
                    Objective o = dataStorage.objectives.get(t.getObjectiveId());
                    return o != null && cycleId.equals(o.getCycleId()) && employeeId.equals(o.getEmployeeId());
                })
                .collect(Collectors.toList()).size();

        BigDecimal finalScore = totalWeight.compareTo(BigDecimal.ZERO) > 0 ?
                totalWeightedScore.divide(totalWeight, 2, RoundingMode.HALF_UP).subtract(totalDelayPenalty) :
                BigDecimal.ZERO;
        if (finalScore.compareTo(BigDecimal.ZERO) < 0) {
            finalScore = BigDecimal.ZERO;
        }

        String riskLevel = "NORMAL";
        if (allTasks.size() > 0 && (double) completedTasks / allTasks.size() < 0.5) {
            riskLevel = "HIGH";
        } else if (delayedTasks > 0) {
            riskLevel = "MEDIUM";
        }

        CycleStatistics stats = new CycleStatistics();
        stats.setId(dataStorage.statisticsIdGenerator.getAndIncrement());
        stats.setCycleId(cycleId);
        stats.setEmployeeId(employeeId);
        stats.setTotalScore(totalWeightedScore);
        stats.setWeightedScore(finalScore);
        stats.setCompletedTasks(completedTasks);
        stats.setTotalTasks(allTasks.size());
        stats.setDelayedTasks(delayedTasks);
        stats.setDelayPenaltyTotal(totalDelayPenalty);
        stats.setPendingConfirmations(pendingConfirmations);
        stats.setRiskLevel(riskLevel);
        stats.setStatisticsTime(LocalDateTime.now());

        return stats;
    }

    private BigDecimal calculateObjectiveProgress(Long objectiveId) {
        List<Task> tasks = taskService.getTasksByObjectiveId(objectiveId);
        if (tasks.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalWeight = tasks.stream()
                .map(Task::getWeight)
                .filter(w -> w != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalWeight.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal weightedProgress = BigDecimal.ZERO;
        for (Task task : tasks) {
            BigDecimal progress = BigDecimal.valueOf(task.getProgress() != null ? task.getProgress() : 0)
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal weight = task.getWeight() != null ? task.getWeight() : BigDecimal.ZERO;
            weightedProgress = weightedProgress.add(progress.multiply(weight));
        }

        return weightedProgress.divide(totalWeight, 4, RoundingMode.HALF_UP);
    }

    public Map<String, Object> getCycleProgress(Long cycleId) {
        List<Employee> employees = new ArrayList<>(dataStorage.employees.values());
        List<CycleStatistics> allStats = new ArrayList<>();

        for (Employee emp : employees) {
            if ("EMPLOYEE".equals(emp.getRole())) {
                allStats.add(calculateEmployeePerformance(cycleId, emp.getId()));
            }
        }

        int highRisk = 0;
        int mediumRisk = 0;
        int normalRisk = 0;
        BigDecimal totalScore = BigDecimal.ZERO;

        for (CycleStatistics s : allStats) {
            totalScore = totalScore.add(s.getWeightedScore());
            switch (s.getRiskLevel()) {
                case "HIGH":
                    highRisk++;
                    break;
                case "MEDIUM":
                    mediumRisk++;
                    break;
                default:
                    normalRisk++;
                    break;
            }
        }

        BigDecimal avgScore = allStats.size() > 0 ?
                totalScore.divide(BigDecimal.valueOf(allStats.size()), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        List<Task> pendingTasks = dataStorage.tasks.values().stream()
                .filter(t -> "PENDING_CONFIRM".equals(t.getStatus()))
                .collect(Collectors.toList());

        List<Task> delayedTasks = taskService.getDelayedTasks();

        Map<String, Object> result = new HashMap<>();
        result.put("statistics", allStats);
        result.put("highRiskCount", highRisk);
        result.put("mediumRiskCount", mediumRisk);
        result.put("normalRiskCount", normalRisk);
        result.put("averageScore", avgScore);
        result.put("pendingConfirmCount", pendingTasks.size());
        result.put("delayedTaskCount", delayedTasks.size());

        return result;
    }

    public List<CycleStatistics> getRiskEmployees(Long cycleId) {
        List<Employee> employees = new ArrayList<>(dataStorage.employees.values());
        List<CycleStatistics> result = new ArrayList<>();

        for (Employee emp : employees) {
            if ("EMPLOYEE".equals(emp.getRole())) {
                CycleStatistics stats = calculateEmployeePerformance(cycleId, emp.getId());
                if (!"NORMAL".equals(stats.getRiskLevel())) {
                    result.add(stats);
                }
            }
        }

        return result;
    }

    public List<Task> getTasksPendingConfirmation() {
        return dataStorage.tasks.values().stream()
                .filter(t -> "PENDING_CONFIRM".equals(t.getStatus()))
                .collect(Collectors.toList());
    }
}
