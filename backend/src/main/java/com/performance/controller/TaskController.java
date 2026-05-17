package com.performance.controller;

import com.performance.common.Result;
import com.performance.entity.Task;
import com.performance.entity.TaskConfirmation;
import com.performance.service.TaskService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Resource
    private TaskService taskService;

    @GetMapping
    public Result<List<Task>> getAllTasks() {
        return Result.success(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public Result<Task> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        if (task == null) {
            return Result.error("任务不存在");
        }
        return Result.success(task);
    }

    @GetMapping("/objective/{objectiveId}")
    public Result<List<Task>> getTasksByObjectiveId(@PathVariable Long objectiveId) {
        return Result.success(taskService.getTasksByObjectiveId(objectiveId));
    }

    @GetMapping("/owner/{ownerId}")
    public Result<List<Task>> getTasksByOwnerId(@PathVariable Long ownerId) {
        return Result.success(taskService.getTasksByOwnerId(ownerId));
    }

    @GetMapping("/delayed")
    public Result<List<Task>> getDelayedTasks() {
        return Result.success(taskService.getDelayedTasks());
    }

    @PostMapping
    public Result<Task> createTask(@RequestBody Task task) {
        return Result.success(taskService.createTask(task));
    }

    @PutMapping("/{id}")
    public Result<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
        Task updated = taskService.updateTask(id, task);
        if (updated == null) {
            return Result.error("任务不存在");
        }
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteTask(@PathVariable Long id) {
        boolean deleted = taskService.deleteTask(id);
        if (!deleted) {
            return Result.error("任务不存在");
        }
        return Result.success();
    }

    @PutMapping("/{id}/progress")
    public Result<Task> updateTaskProgress(@PathVariable Long id, @RequestParam Integer progress) {
        Task updated = taskService.updateTaskProgress(id, progress);
        if (updated == null) {
            return Result.error("任务不存在");
        }
        return Result.success(updated);
    }

    @PostMapping("/{id}/complete")
    public Result<Task> completeTask(@PathVariable Long id) {
        Task updated = taskService.completeTask(id);
        if (updated == null) {
            return Result.error("任务不存在");
        }
        return Result.success(updated);
    }

    @GetMapping("/{taskId}/confirmations")
    public Result<List<TaskConfirmation>> getTaskConfirmations(@PathVariable Long taskId) {
        return Result.success(taskService.getTaskConfirmations(taskId));
    }

    @PostMapping("/confirmations/{confirmationId}/confirm")
    public Result<TaskConfirmation> confirmTask(
            @PathVariable Long confirmationId,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        String comment = body.get("comment");
        TaskConfirmation confirmation = taskService.confirmTask(confirmationId, status, comment);
        if (confirmation == null) {
            return Result.error("确认记录不存在");
        }
        return Result.success(confirmation);
    }

    @GetMapping("/confirmations/pending/{confirmerId}")
    public Result<List<TaskConfirmation>> getPendingConfirmations(@PathVariable Long confirmerId) {
        return Result.success(taskService.getPendingConfirmationsByConfirmer(confirmerId));
    }
}
