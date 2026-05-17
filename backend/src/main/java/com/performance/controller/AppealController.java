package com.performance.controller;

import com.performance.common.Result;
import com.performance.entity.Appeal;
import com.performance.service.AppealService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appeals")
public class AppealController {

    @Resource
    private AppealService appealService;

    @GetMapping
    public Result<List<Appeal>> getAllAppeals() {
        return Result.success(appealService.getAllAppeals());
    }

    @GetMapping("/{id}")
    public Result<Appeal> getAppealById(@PathVariable Long id) {
        Appeal appeal = appealService.getAppealById(id);
        if (appeal == null) {
            return Result.error("申诉不存在");
        }
        return Result.success(appeal);
    }

    @GetMapping("/employee/{employeeId}")
    public Result<List<Appeal>> getAppealsByEmployeeId(@PathVariable Long employeeId) {
        return Result.success(appealService.getAppealsByEmployeeId(employeeId));
    }

    @GetMapping("/pending")
    public Result<List<Appeal>> getPendingAppeals() {
        return Result.success(appealService.getPendingAppeals());
    }

    @PostMapping
    public Result<Appeal> createAppeal(@RequestBody Appeal appeal) {
        return Result.success(appealService.createAppeal(appeal));
    }

    @PostMapping("/{id}/review")
    public Result<Appeal> reviewAppeal(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        String status = (String) body.get("status");
        String comment = (String) body.get("comment");
        Long reviewerId = Long.valueOf(body.get("reviewerId").toString());
        Appeal appeal = appealService.reviewAppeal(id, status, comment, reviewerId);
        if (appeal == null) {
            return Result.error("申诉不存在");
        }
        return Result.success(appeal);
    }
}
