package com.performance.service;

import com.performance.entity.Appeal;
import com.performance.storage.DataStorage;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppealService {

    @Resource
    private DataStorage dataStorage;

    public List<Appeal> getAllAppeals() {
        return new ArrayList<>(dataStorage.appeals.values());
    }

    public Appeal getAppealById(Long id) {
        return dataStorage.appeals.get(id);
    }

    public List<Appeal> getAppealsByEmployeeId(Long employeeId) {
        return dataStorage.appeals.values().stream()
                .filter(a -> employeeId.equals(a.getEmployeeId()))
                .collect(Collectors.toList());
    }

    public List<Appeal> getPendingAppeals() {
        return dataStorage.appeals.values().stream()
                .filter(a -> "PENDING".equals(a.getStatus()))
                .collect(Collectors.toList());
    }

    public Appeal createAppeal(Appeal appeal) {
        appeal.setId(dataStorage.appealIdGenerator.getAndIncrement());
        appeal.setStatus("PENDING");
        appeal.setCreateTime(LocalDateTime.now());
        appeal.setUpdateTime(LocalDateTime.now());
        dataStorage.appeals.put(appeal.getId(), appeal);
        return appeal;
    }

    public Appeal reviewAppeal(Long id, String status, String comment, Long reviewerId) {
        Appeal appeal = dataStorage.appeals.get(id);
        if (appeal == null) {
            return null;
        }
        appeal.setStatus(status);
        appeal.setReviewerComment(comment);
        appeal.setReviewerId(reviewerId);
        appeal.setReviewTime(LocalDateTime.now());
        appeal.setUpdateTime(LocalDateTime.now());
        dataStorage.appeals.put(id, appeal);
        return appeal;
    }
}
