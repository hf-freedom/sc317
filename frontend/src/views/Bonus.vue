<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>奖金池分配</span>
          <div>
            <el-select v-model="selectedCycle" placeholder="选择绩效周期" style="width: 200px; margin-right: 10px;" @change="loadAllocations">
              <el-option v-for="cycle in cycles" :key="cycle.id" :label="cycle.name" :value="cycle.id" />
            </el-select>
            <el-button type="primary" @click="calculateBonus">计算奖金</el-button>
          </div>
        </div>
      </template>
      <el-table :data="allocations" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="cycleId" label="周期ID" width="80" />
        <el-table-column label="员工" width="100">
          <template #default="{ row }">{{ getEmployeeName(row.employeeId) }}</template>
        </el-table-column>
        <el-table-column prop="performanceScore" label="绩效分数" width="120">
          <template #default="{ row }">
            <span :style="{ color: row.performanceScore < 60 ? '#f56c6c' : '#67c23a' }">{{ row.performanceScore }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="bonusPercentage" label="奖金占比(%)" width="120" />
        <el-table-column prop="bonusAmount" label="奖金金额" width="150">
          <template #default="{ row }">¥ {{ row.bonusAmount }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'APPROVED' ? 'success' : 'warning'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="计算时间" width="170">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button size="small" type="success" v-if="row.status !== 'APPROVED'" @click="approveBonus(row.id)">批准</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card style="margin-top: 20px;">
      <template #header>
        <span>奖金池信息</span>
      </template>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="周期名称">{{ currentCycle?.name }}</el-descriptions-item>
        <el-descriptions-item label="奖金池总额">¥ {{ currentCycle?.bonusPool }}</el-descriptions-item>
        <el-descriptions-item label="周期状态">
          <el-tag :type="currentCycle?.status === 'ACTIVE' ? 'success' : 'info'">{{ currentCycle?.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ formatDate(currentCycle?.startDate) }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ formatDate(currentCycle?.endDate) }}</el-descriptions-item>
        <el-descriptions-item label="已分配总额">¥ {{ totalAllocated }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script>
import api from '../api'
import { ElMessage } from 'element-plus'

export default {
  data() {
    return {
      cycles: [],
      employees: [],
      selectedCycle: null,
      allocations: []
    }
  },
  computed: {
    currentCycle() {
      return this.cycles.find(c => c.id === this.selectedCycle)
    },
    totalAllocated() {
      return this.allocations.reduce((sum, a) => sum + (a.bonusAmount || 0), 0)
    }
  },
  mounted() {
    this.loadCycles()
    this.loadEmployees()
  },
  methods: {
    async loadCycles() {
      this.cycles = await api.get('/cycles')
      if (this.cycles.length > 0) {
        this.selectedCycle = this.cycles[0].id
        this.loadAllocations()
      }
    },
    async loadEmployees() {
      this.employees = await api.get('/employees')
    },
    async loadAllocations() {
      this.allocations = await api.get(`/bonus/cycle/${this.selectedCycle}`)
    },
    async calculateBonus() {
      this.allocations = await api.post(`/bonus/calculate/${this.selectedCycle}`)
      ElMessage.success('奖金计算完成')
    },
    async approveBonus(id) {
      await api.post(`/bonus/${id}/approve`)
      this.loadAllocations()
      ElMessage.success('已批准')
    },
    getEmployeeName(id) {
      if (!id) return '-'
      const emp = this.employees.find(e => e.id === id)
      return emp ? emp.name : '-'
    },
    formatDate(date) {
      return date ? new Date(date).toLocaleString() : '-'
    }
  }
}
</script>
