package com.performance.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BonusAllocation {
    private Long id;
    private Long cycleId;
    private Long employeeId;
    private BigDecimal performanceScore;
    private BigDecimal bonusAmount;
    private BigDecimal bonusPercentage;
    private String status;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCycleId() { return cycleId; }
    public void setCycleId(Long cycleId) { this.cycleId = cycleId; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public BigDecimal getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(BigDecimal performanceScore) { this.performanceScore = performanceScore; }
    public BigDecimal getBonusAmount() { return bonusAmount; }
    public void setBonusAmount(BigDecimal bonusAmount) { this.bonusAmount = bonusAmount; }
    public BigDecimal getBonusPercentage() { return bonusPercentage; }
    public void setBonusPercentage(BigDecimal bonusPercentage) { this.bonusPercentage = bonusPercentage; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
