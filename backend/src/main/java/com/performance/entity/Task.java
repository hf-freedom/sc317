package com.performance.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Task {
    private Long id;
    private Long objectiveId;
    private String name;
    private String description;
    private Long ownerId;
    private List<Long> collaboratorIds;
    private LocalDateTime dueDate;
    private LocalDateTime completeDate;
    private BigDecimal weight;
    private String status;
    private Integer progress;
    private Boolean isCrossDepartment;
    private String delayReason;
    private BigDecimal delayPenalty;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getObjectiveId() { return objectiveId; }
    public void setObjectiveId(Long objectiveId) { this.objectiveId = objectiveId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public List<Long> getCollaboratorIds() { return collaboratorIds; }
    public void setCollaboratorIds(List<Long> collaboratorIds) { this.collaboratorIds = collaboratorIds; }
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public LocalDateTime getCompleteDate() { return completeDate; }
    public void setCompleteDate(LocalDateTime completeDate) { this.completeDate = completeDate; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }
    public Boolean getIsCrossDepartment() { return isCrossDepartment; }
    public void setIsCrossDepartment(Boolean crossDepartment) { isCrossDepartment = crossDepartment; }
    public String getDelayReason() { return delayReason; }
    public void setDelayReason(String delayReason) { this.delayReason = delayReason; }
    public BigDecimal getDelayPenalty() { return delayPenalty; }
    public void setDelayPenalty(BigDecimal delayPenalty) { this.delayPenalty = delayPenalty; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
