package com.performance.service;

import com.performance.entity.Objective;
import com.performance.storage.DataStorage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ObjectiveService {

    @Resource
    private DataStorage dataStorage;

    public List<Objective> getAllObjectives() {
        return new ArrayList<>(dataStorage.objectives.values());
    }

    public Objective getObjectiveById(Long id) {
        return dataStorage.objectives.get(id);
    }

    public List<Objective> getObjectivesByCycleId(Long cycleId) {
        return dataStorage.objectives.values().stream()
                .filter(o -> cycleId.equals(o.getCycleId()))
                .collect(Collectors.toList());
    }

    public List<Objective> getObjectivesByEmployeeId(Long employeeId) {
        return dataStorage.objectives.values().stream()
                .filter(o -> employeeId.equals(o.getEmployeeId()))
                .collect(Collectors.toList());
    }

    public List<Objective> getObjectivesByCycleAndEmployee(Long cycleId, Long employeeId) {
        return dataStorage.objectives.values().stream()
                .filter(o -> cycleId.equals(o.getCycleId()) && employeeId.equals(o.getEmployeeId()))
                .collect(Collectors.toList());
    }

    public Objective createObjective(Objective objective) {
        objective.setId(dataStorage.objectiveIdGenerator.getAndIncrement());
        objective.setCreateTime(LocalDateTime.now());
        objective.setUpdateTime(LocalDateTime.now());
        if (objective.getStatus() == null) {
            objective.setStatus("PENDING");
        }
        if (objective.getActualScore() == null) {
            objective.setActualScore(BigDecimal.ZERO);
        }
        dataStorage.objectives.put(objective.getId(), objective);
        return objective;
    }

    public Objective updateObjective(Long id, Objective objective) {
        Objective existing = dataStorage.objectives.get(id);
        if (existing == null) {
            return null;
        }
        existing.setName(objective.getName());
        existing.setDescription(objective.getDescription());
        existing.setWeight(objective.getWeight());
        existing.setTargetScore(objective.getTargetScore());
        existing.setActualScore(objective.getActualScore());
        existing.setStatus(objective.getStatus());
        existing.setUpdateTime(LocalDateTime.now());
        dataStorage.objectives.put(id, existing);
        return existing;
    }

    public boolean deleteObjective(Long id) {
        return dataStorage.objectives.remove(id) != null;
    }

    public Objective updateActualScore(Long id, BigDecimal score) {
        Objective objective = dataStorage.objectives.get(id);
        if (objective == null) {
            return null;
        }
        objective.setActualScore(score);
        objective.setUpdateTime(LocalDateTime.now());
        dataStorage.objectives.put(id, objective);
        return objective;
    }
}
