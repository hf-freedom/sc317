package com.performance.service;

import com.performance.entity.BonusAllocation;
import com.performance.entity.CycleStatistics;
import com.performance.entity.Employee;
import com.performance.entity.PerformanceCycle;
import com.performance.storage.DataStorage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BonusService {

    @Resource
    private DataStorage dataStorage;

    @Resource
    private StatisticsService statisticsService;

    public List<BonusAllocation> calculateBonusAllocations(Long cycleId) {
        PerformanceCycle cycle = dataStorage.cycles.get(cycleId);
        if (cycle == null || cycle.getBonusPool() == null) {
            return new ArrayList<>();
        }

        List<Employee> employees = new ArrayList<>(dataStorage.employees.values());
        List<CycleStatistics> allStats = new ArrayList<>();
        BigDecimal totalScore = BigDecimal.ZERO;

        for (Employee emp : employees) {
            if ("EMPLOYEE".equals(emp.getRole())) {
                CycleStatistics stats = statisticsService.calculateEmployeePerformance(cycleId, emp.getId());
                allStats.add(stats);
                totalScore = totalScore.add(stats.getWeightedScore());
            }
        }

        List<BonusAllocation> allocations = new ArrayList<>();
        BigDecimal bonusPool = cycle.getBonusPool();

        for (CycleStatistics stats : allStats) {
            BigDecimal percentage = totalScore.compareTo(BigDecimal.ZERO) > 0 ?
                    stats.getWeightedScore().divide(totalScore, 4, RoundingMode.HALF_UP) : BigDecimal.ZERO;
            BigDecimal amount = bonusPool.multiply(percentage);

            BonusAllocation allocation = new BonusAllocation();
            allocation.setId(dataStorage.bonusIdGenerator.getAndIncrement());
            allocation.setCycleId(cycleId);
            allocation.setEmployeeId(stats.getEmployeeId());
            allocation.setPerformanceScore(stats.getWeightedScore());
            allocation.setBonusAmount(amount);
            allocation.setBonusPercentage(percentage.multiply(BigDecimal.valueOf(100)));
            allocation.setStatus("CALCULATED");
            allocation.setCreateTime(LocalDateTime.now());
            dataStorage.bonusAllocations.put(allocation.getId(), allocation);
            allocations.add(allocation);
        }

        return allocations;
    }

    public List<BonusAllocation> getBonusAllocationsByCycleId(Long cycleId) {
        List<BonusAllocation> result = new ArrayList<>();
        for (BonusAllocation allocation : dataStorage.bonusAllocations.values()) {
            if (cycleId.equals(allocation.getCycleId())) {
                result.add(allocation);
            }
        }
        return result;
    }

    public BonusAllocation getBonusAllocationById(Long id) {
        return dataStorage.bonusAllocations.get(id);
    }

    public BonusAllocation approveBonus(Long id) {
        BonusAllocation allocation = dataStorage.bonusAllocations.get(id);
        if (allocation == null) {
            return null;
        }
        allocation.setStatus("APPROVED");
        dataStorage.bonusAllocations.put(id, allocation);
        return allocation;
    }

    public List<BonusAllocation> getAllBonusAllocations() {
        return new ArrayList<>(dataStorage.bonusAllocations.values());
    }
}
