package com.performance.storage;

import com.performance.entity.*;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class DataStorage {

    public final ConcurrentHashMap<Long, Employee> employees = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Long, PerformanceCycle> cycles = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Long, Objective> objectives = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Long, Task> tasks = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Long, TaskConfirmation> taskConfirmations = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Long, Appeal> appeals = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Long, CycleStatistics> statistics = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Long, BonusAllocation> bonusAllocations = new ConcurrentHashMap<>();

    public final AtomicLong employeeIdGenerator = new AtomicLong(1);
    public final AtomicLong cycleIdGenerator = new AtomicLong(1);
    public final AtomicLong objectiveIdGenerator = new AtomicLong(1);
    public final AtomicLong taskIdGenerator = new AtomicLong(1);
    public final AtomicLong confirmationIdGenerator = new AtomicLong(1);
    public final AtomicLong appealIdGenerator = new AtomicLong(1);
    public final AtomicLong statisticsIdGenerator = new AtomicLong(1);
    public final AtomicLong bonusIdGenerator = new AtomicLong(1);
}
