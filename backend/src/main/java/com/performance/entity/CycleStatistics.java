package com.performance.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CycleStatistics {
    private Long id;
    private Long cycleId;
    private Long employeeId;
    private BigDecimal totalScore;
    private BigDecimal weightedScore;
    private Integer completedTasks;
    private Integer totalTasks;
    private Integer delayedTasks;
    private BigDecimal delayPenaltyTotal;
    private Integer pendingConfirmations;
    private String riskLevel;
    private LocalDateTime statisticsTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCycleId() { return cycleId; }
    public void setCycleId(Long cycleId) { this.cycleId = cycleId; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public BigDecimal getTotalScore() { return totalScore; }
    public void setTotalScore(BigDecimal totalScore) { this.totalScore = totalScore; }
    public BigDecimal getWeightedScore() { return weightedScore; }
    public void setWeightedScore(BigDecimal weightedScore) { this.weightedScore = weightedScore; }
    public Integer getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(Integer completedTasks) { this.completedTasks = completedTasks; }
    public Integer getTotalTasks() { return totalTasks; }
    public void setTotalTasks(Integer totalTasks) { this.totalTasks = totalTasks; }
    public Integer getDelayedTasks() { return delayedTasks; }
    public void setDelayedTasks(Integer delayedTasks) { this.delayedTasks = delayedTasks; }
    public BigDecimal getDelayPenaltyTotal() { return delayPenaltyTotal; }
    public void setDelayPenaltyTotal(BigDecimal delayPenaltyTotal) { this.delayPenaltyTotal = delayPenaltyTotal; }
    public Integer getPendingConfirmations() { return pendingConfirmations; }
    public void setPendingConfirmations(Integer pendingConfirmations) { this.pendingConfirmations = pendingConfirmations; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public LocalDateTime getStatisticsTime() { return statisticsTime; }
    public void setStatisticsTime(LocalDateTime statisticsTime) { this.statisticsTime = statisticsTime; }
}
