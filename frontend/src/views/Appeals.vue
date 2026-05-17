<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>申诉管理</span>
          <el-button type="primary" @click="showAddDialog">发起申诉</el-button>
        </div>
      </template>
      <el-table :data="appeals" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="申诉人" width="100">
          <template #default="{ row }">{{ getEmployeeName(row.employeeId) }}</template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="objectiveId" label="目标ID" width="100" />
        <el-table-column prop="taskId" label="任务ID" width="100" />
        <el-table-column prop="reason" label="申诉理由" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reviewerComment" label="审核意见" />
        <el-table-column label="审核人" width="100">
          <template #default="{ row }">{{ getEmployeeName(row.reviewerId) }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" v-if="isManager">
          <template #default="{ row }">
            <el-button size="small" type="success" v-if="row.status === 'PENDING'" @click="reviewAppeal(row, 'APPROVED')">通过</el-button>
            <el-button size="small" type="danger" v-if="row.status === 'PENDING'" @click="reviewAppeal(row, 'REJECTED')">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="发起申诉" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="申诉人">
          <el-select v-model="form.employeeId" style="width: 100%">
            <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="申诉类型">
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="目标评分" value="OBJECTIVE_SCORE" />
            <el-option label="任务评分" value="TASK_SCORE" />
            <el-option label="延期处罚" value="DELAY_PENALTY" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标ID">
          <el-input-number v-model="form.objectiveId" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="任务ID">
          <el-input-number v-model="form.taskId" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="申诉理由">
          <el-input v-model="form.reason" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="reviewDialogVisible" title="审核申诉" width="400px">
      <el-form label-width="80px">
        <el-form-item label="审核意见">
          <el-input v-model="reviewComment" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import api from '../api'
import { ElMessage } from 'element-plus'

export default {
  data() {
    return {
      appeals: [],
      employees: [],
      dialogVisible: false,
      reviewDialogVisible: false,
      isManager: true,
      reviewingAppeal: null,
      reviewStatus: '',
      reviewComment: '',
      form: {
        employeeId: null,
        type: 'OBJECTIVE_SCORE',
        objectiveId: null,
        taskId: null,
        reason: ''
      }
    }
  },
  mounted() {
    this.loadAppeals()
    this.loadEmployees()
  },
  methods: {
    async loadAppeals() {
      this.appeals = await api.get('/appeals')
    },
    async loadEmployees() {
      this.employees = await api.get('/employees')
    },
    getEmployeeName(id) {
      if (!id) return '-'
      const emp = this.employees.find(e => e.id === id)
      return emp ? emp.name : '-'
    },
    showAddDialog() {
      this.form = { employeeId: null, type: 'OBJECTIVE_SCORE', objectiveId: null, taskId: null, reason: '' }
      this.dialogVisible = true
    },
    async submitForm() {
      await api.post('/appeals', this.form)
      this.dialogVisible = false
      this.loadAppeals()
      ElMessage.success('申诉已提交')
    },
    reviewAppeal(row, status) {
      this.reviewingAppeal = row
      this.reviewStatus = status
      this.reviewComment = ''
      this.reviewDialogVisible = true
    },
    async submitReview() {
      await api.post(`/appeals/${this.reviewingAppeal.id}/review`, {
        status: this.reviewStatus,
        comment: this.reviewComment,
        reviewerId: 1
      })
      this.reviewDialogVisible = false
      this.loadAppeals()
      ElMessage.success('审核完成')
    },
    formatDate(date) {
      return date ? new Date(date).toLocaleString() : '-'
    },
    getStatusType(status) {
      const map = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger' }
      return map[status] || ''
    }
  }
}
</script>
