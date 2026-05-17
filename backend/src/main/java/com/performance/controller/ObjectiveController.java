package com.performance.controller;

import com.performance.common.Result;
import com.performance.entity.Objective;
import com.performance.service.ObjectiveService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/objectives")
public class ObjectiveController {

    @Resource
    private ObjectiveService objectiveService;

    @GetMapping
    public Result<List<Objective>> getAllObjectives() {
        return Result.success(objectiveService.getAllObjectives());
    }

    @GetMapping("/{id}")
    public Result<Objective> getObjectiveById(@PathVariable Long id) {
        Objective objective = objectiveService.getObjectiveById(id);
        if (objective == null) {
            return Result.error("目标不存在");
        }
        return Result.success(objective);
    }

    @GetMapping("/cycle/{cycleId}")
    public Result<List<Objective>> getObjectivesByCycleId(@PathVariable Long cycleId) {
        return Result.success(objectiveService.getObjectivesByCycleId(cycleId));
    }

    @GetMapping("/employee/{employeeId}")
    public Result<List<Objective>> getObjectivesByEmployeeId(@PathVariable Long employeeId) {
        return Result.success(objectiveService.getObjectivesByEmployeeId(employeeId));
    }

    @GetMapping("/cycle/{cycleId}/employee/{employeeId}")
    public Result<List<Objective>> getObjectivesByCycleAndEmployee(
            @PathVariable Long cycleId, @PathVariable Long employeeId) {
        return Result.success(objectiveService.getObjectivesByCycleAndEmployee(cycleId, employeeId));
    }

    @PostMapping
    public Result<Objective> createObjective(@RequestBody Objective objective) {
        return Result.success(objectiveService.createObjective(objective));
    }

    @PutMapping("/{id}")
    public Result<Objective> updateObjective(@PathVariable Long id, @RequestBody Objective objective) {
        Objective updated = objectiveService.updateObjective(id, objective);
        if (updated == null) {
            return Result.error("目标不存在");
        }
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteObjective(@PathVariable Long id) {
        boolean deleted = objectiveService.deleteObjective(id);
        if (!deleted) {
            return Result.error("目标不存在");
        }
        return Result.success();
    }

    @PutMapping("/{id}/score")
    public Result<Objective> updateActualScore(@PathVariable Long id, @RequestParam BigDecimal score) {
        Objective updated = objectiveService.updateActualScore(id, score);
        if (updated == null) {
            return Result.error("目标不存在");
        }
        return Result.success(updated);
    }
}
