package com.performance.service;

import com.performance.entity.Task;
import com.performance.entity.TaskConfirmation;
import com.performance.storage.DataStorage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Resource
    private DataStorage dataStorage;

    public List<Task> getAllTasks() {
        return new ArrayList<>(dataStorage.tasks.values());
    }

    public Task getTaskById(Long id) {
        return dataStorage.tasks.get(id);
    }

    public List<Task> getTasksByObjectiveId(Long objectiveId) {
        return dataStorage.tasks.values().stream()
                .filter(t -> objectiveId.equals(t.getObjectiveId()))
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByOwnerId(Long ownerId) {
        return dataStorage.tasks.values().stream()
                .filter(t -> ownerId.equals(t.getOwnerId()))
                .collect(Collectors.toList());
    }

    public Task createTask(Task task) {
        task.setId(dataStorage.taskIdGenerator.getAndIncrement());
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        if (task.getStatus() == null) {
            task.setStatus("PENDING");
        }
        if (task.getProgress() == null) {
            task.setProgress(0);
        }
        if (task.getDelayPenalty() == null) {
            task.setDelayPenalty(BigDecimal.ZERO);
        }
        dataStorage.tasks.put(task.getId(), task);
        return task;
    }

    public Task updateTask(Long id, Task task) {
        Task existing = dataStorage.tasks.get(id);
        if (existing == null) {
            return null;
        }
        existing.setName(task.getName());
        existing.setDescription(task.getDescription());
        existing.setDueDate(task.getDueDate());
        existing.setWeight(task.getWeight());
        existing.setProgress(task.getProgress());
        existing.setIsCrossDepartment(task.getIsCrossDepartment());
        existing.setCollaboratorIds(task.getCollaboratorIds());
        existing.setUpdateTime(LocalDateTime.now());
        dataStorage.tasks.put(id, existing);
        return existing;
    }

    public boolean deleteTask(Long id) {
        return dataStorage.tasks.remove(id) != null;
    }

    public Task updateTaskProgress(Long id, Integer progress) {
        Task task = dataStorage.tasks.get(id);
        if (task == null) {
            return null;
        }
        task.setProgress(progress);
        task.setUpdateTime(LocalDateTime.now());

        if (progress >= 100) {
            task.setStatus("PENDING_CONFIRM");
        }
        dataStorage.tasks.put(id, task);
        return task;
    }

    public Task completeTask(Long id) {
        Task task = dataStorage.tasks.get(id);
        if (task == null) {
            return null;
        }
        task.setProgress(100);
        task.setCompleteDate(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());

        if (task.getDueDate() != null && task.getCompleteDate().isAfter(task.getDueDate())) {
            long daysDelayed = java.time.Duration.between(task.getDueDate(), task.getCompleteDate()).toDays();
            BigDecimal penalty = BigDecimal.valueOf(daysDelayed).multiply(new BigDecimal("5"));
            task.setDelayPenalty(penalty);
            task.setDelayReason("延期" + daysDelayed + "天");
        } else {
            task.setDelayPenalty(BigDecimal.ZERO);
        }

        if (task.getIsCrossDepartment() != null && task.getIsCrossDepartment()) {
            task.setStatus("PENDING_CONFIRM");
            createTaskConfirmations(task);
        } else {
            task.setStatus("COMPLETED");
        }
        dataStorage.tasks.put(id, task);
        return task;
    }

    private void createTaskConfirmations(Task task) {
        if (task.getCollaboratorIds() != null) {
            for (Long collaboratorId : task.getCollaboratorIds()) {
                TaskConfirmation confirmation = new TaskConfirmation();
                confirmation.setId(dataStorage.confirmationIdGenerator.getAndIncrement());
                confirmation.setTaskId(task.getId());
                confirmation.setConfirmerId(collaboratorId);
                confirmation.setStatus("PENDING");
                confirmation.setCreateTime(LocalDateTime.now());
                dataStorage.taskConfirmations.put(confirmation.getId(), confirmation);
            }
        }
        Long ownerId = task.getOwnerId();
        if (task.getCollaboratorIds() == null || !task.getCollaboratorIds().contains(ownerId)) {
            TaskConfirmation confirmation = new TaskConfirmation();
            confirmation.setId(dataStorage.confirmationIdGenerator.getAndIncrement());
            confirmation.setTaskId(task.getId());
            confirmation.setConfirmerId(ownerId);
            confirmation.setStatus("PENDING");
            confirmation.setCreateTime(LocalDateTime.now());
            dataStorage.taskConfirmations.put(confirmation.getId(), confirmation);
        }
    }

    public TaskConfirmation confirmTask(Long confirmationId, String status, String comment) {
        TaskConfirmation confirmation = dataStorage.taskConfirmations.get(confirmationId);
        if (confirmation == null) {
            return null;
        }
        confirmation.setStatus(status);
        confirmation.setComment(comment);
        confirmation.setConfirmTime(LocalDateTime.now());
        dataStorage.taskConfirmations.put(confirmationId, confirmation);

        checkAllConfirmationsDone(confirmation.getTaskId());
        return confirmation;
    }

    private void checkAllConfirmationsDone(Long taskId) {
        List<TaskConfirmation> confirmations = dataStorage.taskConfirmations.values().stream()
                .filter(c -> taskId.equals(c.getTaskId()))
                .collect(Collectors.toList());

        boolean allConfirmed = confirmations.stream()
                .allMatch(c -> "CONFIRMED".equals(c.getStatus()));

        if (allConfirmed) {
            Task task = dataStorage.tasks.get(taskId);
            if (task != null) {
                task.setStatus("COMPLETED");
                task.setUpdateTime(LocalDateTime.now());
                dataStorage.tasks.put(taskId, task);
            }
        }
    }

    public List<TaskConfirmation> getTaskConfirmations(Long taskId) {
        return dataStorage.taskConfirmations.values().stream()
                .filter(c -> taskId.equals(c.getTaskId()))
                .collect(Collectors.toList());
    }

    public List<TaskConfirmation> getPendingConfirmationsByConfirmer(Long confirmerId) {
        return dataStorage.taskConfirmations.values().stream()
                .filter(c -> confirmerId.equals(c.getConfirmerId()) && "PENDING".equals(c.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Task> getDelayedTasks() {
        LocalDateTime now = LocalDateTime.now();
        return dataStorage.tasks.values().stream()
                .filter(t -> !"COMPLETED".equals(t.getStatus()) && t.getDueDate() != null && now.isAfter(t.getDueDate()))
                .collect(Collectors.toList());
    }
}
