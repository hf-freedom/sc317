package com.performance.controller;

import com.performance.common.Result;
import com.performance.entity.BonusAllocation;
import com.performance.service.BonusService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/bonus")
public class BonusController {

    @Resource
    private BonusService bonusService;

    @PostMapping("/calculate/{cycleId}")
    public Result<List<BonusAllocation>> calculateBonusAllocations(@PathVariable Long cycleId) {
        return Result.success(bonusService.calculateBonusAllocations(cycleId));
    }

    @GetMapping("/cycle/{cycleId}")
    public Result<List<BonusAllocation>> getBonusAllocationsByCycleId(@PathVariable Long cycleId) {
        return Result.success(bonusService.getBonusAllocationsByCycleId(cycleId));
    }

    @GetMapping("/{id}")
    public Result<BonusAllocation> getBonusAllocationById(@PathVariable Long id) {
        BonusAllocation allocation = bonusService.getBonusAllocationById(id);
        if (allocation == null) {
            return Result.error("奖金分配记录不存在");
        }
        return Result.success(allocation);
    }

    @GetMapping
    public Result<List<BonusAllocation>> getAllBonusAllocations() {
        return Result.success(bonusService.getAllBonusAllocations());
    }

    @PostMapping("/{id}/approve")
    public Result<BonusAllocation> approveBonus(@PathVariable Long id) {
        BonusAllocation allocation = bonusService.approveBonus(id);
        if (allocation == null) {
            return Result.error("奖金分配记录不存在");
        }
        return Result.success(allocation);
    }
}
