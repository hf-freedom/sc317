package com.performance.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Objective {
    private Long id;
    private Long cycleId;
    private Long employeeId;
    private String name;
    private String description;
    private BigDecimal weight;
    private BigDecimal targetScore;
    private BigDecimal actualScore;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCycleId() { return cycleId; }
    public void setCycleId(Long cycleId) { this.cycleId = cycleId; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
    public BigDecimal getTargetScore() { return targetScore; }
    public void setTargetScore(BigDecimal targetScore) { this.targetScore = targetScore; }
    public BigDecimal getActualScore() { return actualScore; }
    public void setActualScore(BigDecimal actualScore) { this.actualScore = actualScore; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
