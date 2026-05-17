package com.performance.controller;

import com.performance.common.Result;
import com.performance.entity.CycleStatistics;
import com.performance.entity.Task;
import com.performance.schedule.ScheduledTasks;
import com.performance.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    @Resource
    private ScheduledTasks scheduledTasks;

    @GetMapping("/cycle/{cycleId}/employee/{employeeId}")
    public Result<CycleStatistics> calculateEmployeePerformance(
            @PathVariable Long cycleId,
            @PathVariable Long employeeId) {
        return Result.success(statisticsService.calculateEmployeePerformance(cycleId, employeeId));
    }

    @GetMapping("/cycle/{cycleId}/progress")
    public Result<Map<String, Object>> getCycleProgress(@PathVariable Long cycleId) {
        return Result.success(statisticsService.getCycleProgress(cycleId));
    }

    @GetMapping("/cycle/{cycleId}/risk-employees")
    public Result<List<CycleStatistics>> getRiskEmployees(@PathVariable Long cycleId) {
        return Result.success(statisticsService.getRiskEmployees(cycleId));
    }

    @GetMapping("/tasks/pending-confirmation")
    public Result<List<Task>> getTasksPendingConfirmation() {
        return Result.success(statisticsService.getTasksPendingConfirmation());
    }

    @PostMapping("/scan")
    public Result<Map<String, Object>> triggerScan() {
        Map<String, Object> result = new HashMap<>();
        try {
            scheduledTasks.calculateCycleProgress();
            scheduledTasks.detectRiskEmployees();
            scheduledTasks.checkPendingConfirmationTasks();
            
            result.put("success", true);
            result.put("message", "扫描完成");
            result.put("scanTime", System.currentTimeMillis());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "扫描失败: " + e.getMessage());
        }
        return Result.success(result);
    }

    @GetMapping("/scan/result/{cycleId}")
    public Result<Map<String, Object>> getScanResult(@PathVariable Long cycleId) {
        Map<String, Object> result = statisticsService.getCycleProgress(cycleId);
        result.put("pendingConfirmationTasks", statisticsService.getTasksPendingConfirmation());
        result.put("riskEmployees", statisticsService.getRiskEmployees(cycleId));
        return Result.success(result);
    }
}
