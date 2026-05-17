<template>
  <div>
    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>周期统计概览</span>
          <div>
            <el-select v-model="selectedCycle" placeholder="选择绩效周期" style="width: 200px; margin-right: 10px;" @change="loadStatistics">
              <el-option v-for="cycle in cycles" :key="cycle.id" :label="cycle.name" :value="cycle.id" />
            </el-select>
            <el-button type="primary" @click="triggerScan" :loading="scanning">
              <el-icon style="margin-right: 5px;"><Refresh /></el-icon>
              手动扫描
            </el-button>
          </div>
        </div>
      </template>
      <el-row :gutter="20">
        <el-col :span="4">
          <el-statistic title="平均分数" :value="overview.averageScore || 0" :precision="2" />
        </el-col>
        <el-col :span="4">
          <el-statistic title="高风险员工" :value="overview.highRiskCount || 0" value-color="#f56c6c" />
        </el-col>
        <el-col :span="4">
          <el-statistic title="中风险员工" :value="overview.mediumRiskCount || 0" value-color="#e6a23c" />
        </el-col>
        <el-col :span="4">
          <el-statistic title="正常员工" :value="overview.normalRiskCount || 0" value-color="#67c23a" />
        </el-col>
        <el-col :span="4">
          <el-statistic title="待确认任务" :value="overview.pendingConfirmCount || 0" value-color="#409EFF" />
        </el-col>
        <el-col :span="4">
          <el-statistic title="延期任务" :value="overview.delayedTaskCount || 0" value-color="#f56c6c" />
        </el-col>
      </el-row>
      <div v-if="lastScanTime" style="margin-top: 15px; color: #909399; font-size: 12px;">
        上次扫描时间: {{ lastScanTime }}
      </div>
    </el-card>

    <el-card style="margin-bottom: 20px;" v-if="riskEmployees.length > 0">
      <template #header>
        <el-tag type="danger" size="large">风险员工列表 ({{ riskEmployees.length }})</el-tag>
      </template>
      <el-table :data="riskEmployees" border size="small">
        <el-table-column label="员工姓名" width="100">
          <template #default="{ row }">{{ getEmployeeName(row.employeeId) }}</template>
        </el-table-column>
        <el-table-column prop="weightedScore" label="加权分数" width="100">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold;">{{ row.weightedScore }}</span>
          </template>
        </el-table-column>
        <el-table-column label="任务完成" width="120">
          <template #default="{ row }">{{ row.completedTasks }}/{{ row.totalTasks }}</template>
        </el-table-column>
        <el-table-column prop="delayedTasks" label="延期任务" width="100" />
        <el-table-column prop="riskLevel" label="风险等级" width="100">
          <template #default="{ row }">
            <el-tag :type="row.riskLevel === 'HIGH' ? 'danger' : 'warning'">{{ row.riskLevel }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card style="margin-bottom: 20px;" v-if="pendingConfirmationTasks.length > 0">
      <template #header>
        <el-tag type="warning" size="large">待确认任务列表 ({{ pendingConfirmationTasks.length }})</el-tag>
      </template>
      <el-table :data="pendingConfirmationTasks" border size="small">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="任务名称" />
        <el-table-column label="负责人" width="100">
          <template #default="{ row }">{{ getEmployeeName(row.ownerId) }}</template>
        </el-table-column>
        <el-table-column label="协作人" width="150">
          <template #default="{ row }">
            <span v-if="row.collaboratorIds && row.collaboratorIds.length > 0">
              {{ row.collaboratorIds.map(id => getEmployeeName(id)).join(', ') }}
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="isCrossDepartment" label="跨部门" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isCrossDepartment" type="warning">是</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="截止日期" width="170">
          <template #default="{ row }">{{ formatDate(row.dueDate) }}</template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card style="margin-bottom: 20px;">
      <template #header>
        <span>员工绩效详情</span>
      </template>
      <el-table :data="statistics" border>
        <el-table-column prop="employeeId" label="员工ID" width="80" />
        <el-table-column label="员工姓名" width="100">
          <template #default="{ row }">{{ getEmployeeName(row.employeeId) }}</template>
        </el-table-column>
        <el-table-column prop="totalScore" label="总分数" width="100" />
        <el-table-column prop="weightedScore" label="加权分数" width="100">
          <template #default="{ row }">
            <span :style="{ color: row.weightedScore < 60 ? '#f56c6c' : '#67c23a' }">{{ row.weightedScore }}</span>
          </template>
        </el-table-column>
        <el-table-column label="任务完成" width="120">
          <template #default="{ row }">{{ row.completedTasks }}/{{ row.totalTasks }}</template>
        </el-table-column>
        <el-table-column prop="delayedTasks" label="延期任务" width="100" />
        <el-table-column prop="delayPenaltyTotal" label="延期扣分" width="100" />
        <el-table-column prop="pendingConfirmations" label="待确认" width="80" />
        <el-table-column prop="riskLevel" label="风险等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getRiskType(row.riskLevel)">{{ row.riskLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="statisticsTime" label="统计时间" width="170">
          <template #default="{ row }">{{ formatDate(row.statisticsTime) }}</template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card>
      <template #header>
        <span>延期任务列表</span>
      </template>
      <el-table :data="delayedTasks" border>
        <el-table-column prop="id" label="任务ID" width="80" />
        <el-table-column prop="name" label="任务名称" />
        <el-table-column label="负责人" width="100">
          <template #default="{ row }">{{ getEmployeeName(row.ownerId) }}</template>
        </el-table-column>
        <el-table-column prop="dueDate" label="截止日期" width="170">
          <template #default="{ row }">{{ formatDate(row.dueDate) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag type="danger">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="delayPenalty" label="已扣分" width="100" />
      </el-table>
      <el-empty v-if="delayedTasks.length === 0" description="暂无延期任务" />
    </el-card>
  </div>
</template>

<script>
import api from '../api'
import { Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

export default {
  components: { Refresh },
  data() {
    return {
      cycles: [],
      employees: [],
      selectedCycle: null,
      statistics: [],
      overview: {},
      delayedTasks: [],
      riskEmployees: [],
      pendingConfirmationTasks: [],
      scanning: false,
      lastScanTime: ''
    }
  },
  mounted() {
    this.loadCycles()
    this.loadEmployees()
    this.loadDelayedTasks()
  },
  methods: {
    async loadCycles() {
      this.cycles = await api.get('/cycles')
      if (this.cycles.length > 0) {
        this.selectedCycle = this.cycles[0].id
        this.loadStatistics()
      }
    },
    async loadEmployees() {
      this.employees = await api.get('/employees')
    },
    async loadStatistics() {
      const data = await api.get(`/statistics/scan/result/${this.selectedCycle}`)
      this.statistics = data.statistics || []
      this.overview = data
      this.riskEmployees = data.riskEmployees || []
      this.pendingConfirmationTasks = data.pendingConfirmationTasks || []
    },
    async loadDelayedTasks() {
      this.delayedTasks = await api.get('/tasks/delayed')
    },
    async triggerScan() {
      this.scanning = true
      try {
        const result = await api.post('/statistics/scan')
        if (result.success) {
          ElMessage.success('扫描完成')
          this.lastScanTime = new Date().toLocaleString()
          await this.loadStatistics()
          await this.loadDelayedTasks()
        } else {
          ElMessage.error(result.message || '扫描失败')
        }
      } catch (e) {
        ElMessage.error('扫描失败: ' + e)
      } finally {
        this.scanning = false
      }
    },
    getEmployeeName(id) {
      if (!id) return '-'
      const emp = this.employees.find(e => e.id === id)
      return emp ? emp.name : '-'
    },
    getRiskType(level) {
      const map = { HIGH: 'danger', MEDIUM: 'warning', NORMAL: 'success' }
      return map[level] || ''
    },
    formatDate(date) {
      return date ? new Date(date).toLocaleString() : '-'
    }
  }
}
</script>
