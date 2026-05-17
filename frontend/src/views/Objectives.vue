<template>
  <div>
    <el-alert
      title="操作流程：先在「绩效周期」页面创建周期并拆分员工目标，也可以在此页面直接管理所有目标"
      type="info"
      :closable="false"
      style="margin-bottom: 20px;"
    />

    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>筛选条件</span>
          <el-button type="primary" @click="showAddDialog">新建目标</el-button>
        </div>
      </template>
      <el-row :gutter="10">
        <el-col :span="8">
          <el-select v-model="selectedCycle" placeholder="选择绩效周期" style="width: 100%" @change="loadObjectives">
            <el-option v-for="cycle in cycles" :key="cycle.id" :label="cycle.name" :value="cycle.id" />
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-select v-model="selectedEmployee" placeholder="选择员工（可选）" style="width: 100%" @change="loadObjectives" clearable>
            <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id" />
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-button @click="loadObjectives" type="success">查询</el-button>
        </el-col>
      </el-row>
    </el-card>

    <div v-for="(group, employeeId) in groupedObjectives" :key="employeeId">
      <el-card style="margin-bottom: 20px;">
        <template #header>
          <div style="display: flex; justify-content: space-between; align-items: center;">
            <div>
              <span style="font-weight: bold; font-size: 16px;">{{ getEmployeeName(parseInt(employeeId)) }}</span>
              <el-tag style="margin-left: 10px;">目标数量: {{ group.length }}</el-tag>
            </div>
            <div>
              <el-tag :type="getWeightAlertType(group)" style="margin-right: 10px;">
                权重总计: {{ getTotalWeight(group) }}%
              </el-tag>
              <el-tag type="primary" style="margin-right: 10px;">
                目标总分: {{ getTotalTargetScore(group) }}
              </el-tag>
              <el-tag type="success">
                加权实际分: {{ getWeightedScore(group) }}
              </el-tag>
            </div>
          </div>
        </template>

        <el-alert
          v-if="getTotalWeight(group) !== 100"
          :title="'权重总计为 ' + getTotalWeight(group) + '%，建议调整为 100%'"
          type="warning"
          :closable="false"
          style="margin-bottom: 15px;"
        />

        <el-table :data="group" border>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="目标名称" />
          <el-table-column prop="description" label="描述" />
          <el-table-column prop="weight" label="权重" width="120">
            <template #default="{ row }">
              <el-tag>{{ (row.weight * 100).toFixed(0) }}%</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="targetScore" label="目标分" width="100" />
          <el-table-column prop="actualScore" label="实际分" width="100" />
          <el-table-column label="加权贡献分" width="120">
            <template #default="{ row }">
              <span style="color: #67C23A; font-weight: bold;">
                {{ (row.actualScore * row.weight).toFixed(2) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120">
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
      </el-card>
    </div>

    <el-empty v-if="Object.keys(groupedObjectives).length === 0" description="暂无目标数据，请点击上方「新建目标」按钮创建" />

    <el-dialog v-model="dialogVisible" :title="editId ? '编辑目标' : '新建目标'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="绩效周期">
          <el-select v-model="form.cycleId" style="width: 100%">
            <el-option v-for="cycle in cycles" :key="cycle.id" :label="cycle.name" :value="cycle.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="员工">
          <el-select v-model="form.employeeId" style="width: 100%">
            <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="权重(0-1)">
          <el-input-number v-model="form.weight" :min="0" :max="1" :step="0.1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="目标分数">
          <el-input-number v-model="form.targetScore" :min="0" :max="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="实际分数">
          <el-input-number v-model="form.actualScore" :min="0" :max="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="PENDING" value="PENDING" />
            <el-option label="IN_PROGRESS" value="IN_PROGRESS" />
            <el-option label="COMPLETED" value="COMPLETED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
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
      objectives: [],
      selectedCycle: null,
      selectedEmployee: null,
      dialogVisible: false,
      editId: null,
      form: {
        cycleId: null,
        employeeId: null,
        name: '',
        description: '',
        weight: 0.5,
        targetScore: 100,
        actualScore: 0,
        status: 'PENDING'
      }
    }
  },
  computed: {
    groupedObjectives() {
      const groups = {}
      for (const obj of this.objectives) {
        const key = obj.employeeId
        if (!groups[key]) {
          groups[key] = []
        }
        groups[key].push(obj)
      }
      return groups
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
        this.loadObjectives()
      }
    },
    async loadEmployees() {
      this.employees = await api.get('/employees')
    },
    async loadObjectives() {
      let url = '/objectives'
      if (this.selectedCycle && this.selectedEmployee) {
        url = `/objectives/cycle/${this.selectedCycle}/employee/${this.selectedEmployee}`
      } else if (this.selectedCycle) {
        url = `/objectives/cycle/${this.selectedCycle}`
      } else if (this.selectedEmployee) {
        url = `/objectives/employee/${this.selectedEmployee}`
      }
      this.objectives = await api.get(url)
    },
    getEmployeeName(id) {
      const emp = this.employees.find(e => e.id === id)
      return emp ? emp.name : '-'
    },
    getTotalWeight(group) {
      const sum = group.reduce((acc, obj) => acc + (obj.weight || 0), 0)
      return (sum * 100).toFixed(0)
    },
    getTotalTargetScore(group) {
      return group.reduce((acc, obj) => acc + (obj.targetScore || 0), 0)
    },
    getWeightedScore(group) {
      const sum = group.reduce((acc, obj) => acc + (obj.actualScore || 0) * (obj.weight || 0), 0)
      return sum.toFixed(2)
    },
    getWeightAlertType(group) {
      const total = this.getTotalWeight(group)
      if (total === '100') return 'success'
      if (total > 100) return 'danger'
      return 'warning'
    },
    showAddDialog() {
      this.editId = null
      this.form = { cycleId: this.selectedCycle, employeeId: null, name: '', description: '', weight: 0.5, targetScore: 100, actualScore: 0, status: 'PENDING' }
      this.dialogVisible = true
    },
    editObjective(row) {
      this.editId = row.id
      this.form = { ...row }
      this.dialogVisible = true
    },
    async submitForm() {
      if (this.editId) {
        await api.put(`/objectives/${this.editId}`, this.form)
      } else {
        await api.post('/objectives', this.form)
      }
      this.dialogVisible = false
      this.loadObjectives()
      ElMessage.success('保存成功')
    },
    async deleteObjective(id) {
      await api.delete(`/objectives/${id}`)
      this.loadObjectives()
      ElMessage.success('删除成功')
    },
    getStatusType(status) {
      const map = { PENDING: 'info', IN_PROGRESS: 'warning', COMPLETED: 'success' }
      return map[status] || ''
    }
  }
}
</script>
