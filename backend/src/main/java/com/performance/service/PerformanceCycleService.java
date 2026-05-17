package com.performance.service;

import com.performance.entity.PerformanceCycle;
import com.performance.storage.DataStorage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PerformanceCycleService {

    @Resource
    private DataStorage dataStorage;

    public List<PerformanceCycle> getAllCycles() {
        return new ArrayList<>(dataStorage.cycles.values());
    }

    public PerformanceCycle getCycleById(Long id) {
        return dataStorage.cycles.get(id);
    }

    public PerformanceCycle createCycle(PerformanceCycle cycle) {
        cycle.setId(dataStorage.cycleIdGenerator.getAndIncrement());
        cycle.setCreateTime(LocalDateTime.now());
        cycle.setUpdateTime(LocalDateTime.now());
        if (cycle.getStatus() == null) {
            cycle.setStatus("DRAFT");
        }
        dataStorage.cycles.put(cycle.getId(), cycle);
        return cycle;
    }

    public PerformanceCycle updateCycle(Long id, PerformanceCycle cycle) {
        PerformanceCycle existing = dataStorage.cycles.get(id);
        if (existing == null) {
            return null;
        }
        existing.setName(cycle.getName());
        existing.setDescription(cycle.getDescription());
        existing.setStartDate(cycle.getStartDate());
        existing.setEndDate(cycle.getEndDate());
        existing.setStatus(cycle.getStatus());
        existing.setBonusPool(cycle.getBonusPool());
        existing.setUpdateTime(LocalDateTime.now());
        dataStorage.cycles.put(id, existing);
        return existing;
    }

    public boolean deleteCycle(Long id) {
        return dataStorage.cycles.remove(id) != null;
    }

    public PerformanceCycle startCycle(Long id) {
        PerformanceCycle cycle = dataStorage.cycles.get(id);
        if (cycle == null) {
            return null;
        }
        cycle.setStatus("ACTIVE");
        cycle.setUpdateTime(LocalDateTime.now());
        dataStorage.cycles.put(id, cycle);
        return cycle;
    }

    public PerformanceCycle closeCycle(Long id) {
        PerformanceCycle cycle = dataStorage.cycles.get(id);
        if (cycle == null) {
            return null;
        }
        cycle.setStatus("CLOSED");
        cycle.setUpdateTime(LocalDateTime.now());
        dataStorage.cycles.put(id, cycle);
        return cycle;
    }
}
