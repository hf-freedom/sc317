<template>
  <div>
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>绩效周期管理</span>
          <el-button type="primary" @click="showAddDialog">新建周期</el-button>
        </div>
      </template>
      <el-table :data="cycles" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="周期名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="startDate" label="开始日期">
          <template #default="{ row }">{{ formatDate(row.startDate) }}</template>
        </el-table-column>
        <el-table-column prop="endDate" label="结束日期">
          <template #default="{ row }">{{ formatDate(row.endDate) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="bonusPool" label="奖金池" width="120" />
        <el-table-column label="操作" width="320">
          <template #default="{ row }">
            <el-button size="small" type="success" @click="openSplitObjective(row)">拆分目标</el-button>
            <el-button size="small" type="primary" v-if="row.status === 'DRAFT'" @click="startCycle(row.id)">启动</el-button>
            <el-button size="small" type="warning" v-if="row.status === 'ACTIVE'" @click="closeCycle(row.id)">关闭</el-button>
            <el-button size="small" type="danger" @click="deleteCycle(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="新建绩效周期" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="周期名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="form.startDate" type="datetime" placeholder="选择开始日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="form.endDate" type="datetime" placeholder="选择结束日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="奖金池">
          <el-input-number v-model="form.bonusPool" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="splitDialogVisible" :title="'拆分目标 - ' + currentCycleName" width="900px">
      <div style="margin-bottom: 15px;">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-select v-model="selectedEmployee" placeholder="选择员工" style="width: 100%" @change="loadEmployeeObjectives">
              <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id" />
            </el-select>
          </el-col>
          <el-col :span="8">
            <el-button type="primary" @click="showAddObjective">为该员工添加目标</el-button>
          </el-col>
          <el-col :span="8" style="text-align: right;">
            <el-tag>总权重: {{ totalWeight }}</el-tag>
          </el-col>
        </el-row>
      </div>
      <el-table :data="employeeObjectives" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="目标名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="weight" label="权重" width="100">
          <template #default="{ row }">
            <el-tag>{{ (row.weight * 100).toFixed(0) }}%</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="targetScore" label="目标分" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="editObjective(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteObjective(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="employeeObjectives.length === 0" description="该员工暂无目标，请点击上方按钮添加" />
      <template #footer>
        <el-button @click="splitDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="objectiveDialogVisible" :title="editingObjectiveId ? '编辑目标' : '添加目标'" width="500px">
      <el-form :model="objectiveForm" label-width="100px">
        <el-form-item label="目标名称">
          <el-input v-model="objectiveForm.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="objectiveForm.description" type="textarea" />
        </el-form-item>
        <el-form-item label="权重(0-1)">
          <el-input-number v-model="objectiveForm.weight" :min="0" :max="1" :step="0.1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="目标分数">
          <el-input-number v-model="objectiveForm.targetScore" :min="0" :max="100" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="objectiveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitObjective">确定</el-button>
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
      cycles: [],
      employees: [],
      dialogVisible: false,
      splitDialogVisible: false,
      objectiveDialogVisible: false,
      currentCycleId: null,
      currentCycleName: '',
      selectedEmployee: null,
      employeeObjectives: [],
      editingObjectiveId: null,
      form: {
        name: '',
        description: '',
        startDate: '',
        endDate: '',
        bonusPool: 0,
        createdBy: 1
      },
      objectiveForm: {
        name: '',
        description: '',
        weight: 0.5,
        targetScore: 100
      }
    }
  },
  computed: {
    totalWeight() {
      const sum = this.employeeObjectives.reduce((acc, obj) => acc + (obj.weight || 0), 0)
      return (sum * 100).toFixed(0) + '%'
    }
  },
  mounted() {
    this.loadCycles()
    this.loadEmployees()
  },
  methods: {
    async loadCycles() {
      this.cycles = await api.get('/cycles')
    },
    async loadEmployees() {
      this.employees = (await api.get('/employees')).filter(e => e.role === 'EMPLOYEE')
    },
    showAddDialog() {
      this.dialogVisible = true
      this.form = { name: '', description: '', startDate: '', endDate: '', bonusPool: 0, createdBy: 1 }
    },
    async submitForm() {
      const created = await api.post('/cycles', this.form)
      this.dialogVisible = false
      this.loadCycles()
      ElMessage.success('创建成功，现在可以为员工拆分目标')
    },
    openSplitObjective(row) {
      this.currentCycleId = row.id
      this.currentCycleName = row.name
      this.selectedEmployee = null
      this.employeeObjectives = []
      this.splitDialogVisible = true
    },
    async loadEmployeeObjectives() {
      if (!this.selectedEmployee || !this.currentCycleId) return
      this.employeeObjectives = await api.get(`/objectives/cycle/${this.currentCycleId}/employee/${this.selectedEmployee}`)
    },
    showAddObjective() {
      if (!this.selectedEmployee) {
        ElMessage.warning('请先选择员工')
        return
      }
      this.editingObjectiveId = null
      this.objectiveForm = { name: '', description: '', weight: 0.5, targetScore: 100 }
      this.objectiveDialogVisible = true
    },
    editObjective(row) {
      this.editingObjectiveId = row.id
      this.objectiveForm = {
        name: row.name,
        description: row.description,
        weight: row.weight,
        targetScore: row.targetScore
      }
      this.objectiveDialogVisible = true
    },
    async submitObjective() {
      const data = {
        ...this.objectiveForm,
        cycleId: this.currentCycleId,
        employeeId: this.selectedEmployee,
        actualScore: 0,
        status: 'PENDING'
      }
      if (this.editingObjectiveId) {
        await api.put(`/objectives/${this.editingObjectiveId}`, data)
        ElMessage.success('更新成功')
      } else {
        await api.post('/objectives', data)
        ElMessage.success('添加成功')
      }
      this.objectiveDialogVisible = false
      this.loadEmployeeObjectives()
    },
    async deleteObjective(id) {
      await api.delete(`/objectives/${id}`)
      this.loadEmployeeObjectives()
      ElMessage.success('删除成功')
    },
    async startCycle(id) {
      await api.post(`/cycles/${id}/start`)
      this.loadCycles()
      ElMessage.success('周期已启动')
    },
    async closeCycle(id) {
      await api.post(`/cycles/${id}/close`)
      this.loadCycles()
      ElMessage.success('周期已关闭')
    },
    async deleteCycle(id) {
      await api.delete(`/cycles/${id}`)
      this.loadCycles()
      ElMessage.success('删除成功')
    },
    formatDate(date) {
      return date ? new Date(date).toLocaleString() : '-'
    },
    getStatusType(status) {
      const map = { DRAFT: 'info', ACTIVE: 'success', CLOSED: 'danger', PENDING: 'info', IN_PROGRESS: 'warning', COMPLETED: 'success' }
      return map[status] || ''
    }
  }
}
</script>
