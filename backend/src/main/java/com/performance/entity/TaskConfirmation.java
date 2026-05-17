package com.performance.entity;

import java.time.LocalDateTime;

public class TaskConfirmation {
    private Long id;
    private Long taskId;
    private Long confirmerId;
    private String status;
    private String comment;
    private LocalDateTime confirmTime;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public Long getConfirmerId() { return confirmerId; }
    public void setConfirmerId(Long confirmerId) { this.confirmerId = confirmerId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getConfirmTime() { return confirmTime; }
    public void setConfirmTime(LocalDateTime confirmTime) { this.confirmTime = confirmTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
