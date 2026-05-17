package com.performance.controller;

import com.performance.common.Result;
import com.performance.entity.PerformanceCycle;
import com.performance.service.PerformanceCycleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/cycles")
public class PerformanceCycleController {

    @Resource
    private PerformanceCycleService cycleService;

    @GetMapping
    public Result<List<PerformanceCycle>> getAllCycles() {
        return Result.success(cycleService.getAllCycles());
    }

    @GetMapping("/{id}")
    public Result<PerformanceCycle> getCycleById(@PathVariable Long id) {
        PerformanceCycle cycle = cycleService.getCycleById(id);
        if (cycle == null) {
            return Result.error("绩效周期不存在");
        }
        return Result.success(cycle);
    }

    @PostMapping
    public Result<PerformanceCycle> createCycle(@RequestBody PerformanceCycle cycle) {
        return Result.success(cycleService.createCycle(cycle));
    }

    @PutMapping("/{id}")
    public Result<PerformanceCycle> updateCycle(@PathVariable Long id, @RequestBody PerformanceCycle cycle) {
        PerformanceCycle updated = cycleService.updateCycle(id, cycle);
        if (updated == null) {
            return Result.error("绩效周期不存在");
        }
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCycle(@PathVariable Long id) {
        boolean deleted = cycleService.deleteCycle(id);
        if (!deleted) {
            return Result.error("绩效周期不存在");
        }
        return Result.success();
    }

    @PostMapping("/{id}/start")
    public Result<PerformanceCycle> startCycle(@PathVariable Long id) {
        PerformanceCycle cycle = cycleService.startCycle(id);
        if (cycle == null) {
            return Result.error("绩效周期不存在");
        }
        return Result.success(cycle);
    }

    @PostMapping("/{id}/close")
    public Result<PerformanceCycle> closeCycle(@PathVariable Long id) {
        PerformanceCycle cycle = cycleService.closeCycle(id);
        if (cycle == null) {
            return Result.error("绩效周期不存在");
        }
        return Result.success(cycle);
    }
}
