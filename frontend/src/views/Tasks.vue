<template>
  <div>
    <el-alert
      title="操作流程：先创建目标 → 创建任务 → 完成任务 → 跨部门任务需要多人确认"
      type="info"
      :closable="false"
      style="margin-bottom: 20px;"
    />

    <el-card style="margin-bottom: 20px;">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>筛选条件</span>
          <el-button type="primary" @click="showAddDialog" :disabled="objectives.length === 0">新建任务</el-button>
        </div>
      </template>
      <el-row :gutter="10">
        <el-col :span="6">
          <el-select v-model="selectedObjective" placeholder="选择目标" style="width: 100%" @change="loadTasks" clearable>
            <el-option v-for="obj in objectives" :key="obj.id" :label="obj.name + ' (' + getEmployeeName(obj.employeeId) + ')'" :value="obj.id" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="selectedStatus" placeholder="任务状态" style="width: 100%" @change="loadTasks" clearable>
            <el-option label="待处理" value="PENDING" />
            <el-option label="进行中" value="IN_PROGRESS" />
            <el-option label="待确认" value="PENDING_CONFIRM" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="currentUser" placeholder="当前登录用户" style="width: 100%" @change="loadTasks">
            <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-checkbox v-model="onlyPendingMyConfirm" @change="loadTasks" style="margin-top: 5px;">只看待我确认</el-checkbox>
          <el-button type="success" @click="loadTasks" style="margin-left: 10px;">查询</el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card style="margin-bottom: 20px;" v-if="pendingMyConfirmTasks.length > 0">
      <template #header>
        <el-tag type="danger" size="large">待我确认的任务 ({{ pendingMyConfirmTasks.length }})</el-tag>
      </template>
      <el-table :data="pendingMyConfirmTasks" border size="small">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="任务名称" />
        <el-table-column label="负责人" width="100">
          <template #default="{ row }">{{ getEmployeeName(row.ownerId) }}</template>
        </el-table-column>
        <el-table-column label="确认进度" width="200">
          <template #default="{ row }">
            <el-progress :percentage="getConfirmProgress(row)" :status="getConfirmProgress(row) === 100 ? 'success' : ''" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" type="success" @click="quickConfirm(row, 'CONFIRMED')">确认通过</el-button>
            <el-button size="small" type="danger" @click="quickConfirm(row, 'REJECTED')">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>任务列表</span>
          <div>
            <el-tag style="margin-right: 10px;">总数: {{ tasks.length }}</el-tag>
            <el-tag type="warning" style="margin-right: 10px;">待确认: {{ pendingConfirmCount }}</el-tag>
            <el-tag type="success">已完成: {{ completedCount }}</el-tag>
          </div>
        </div>
      </template>
      <el-table :data="tasks" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="objectiveId" label="目标ID" width="80" />
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
        <el-table-column prop="dueDate" label="截止日期" width="170">
          <template #default="{ row }">
            <span :style="{ color: isOverdue(row) ? '#f56c6c' : '' }">{{ formatDate(row.dueDate) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="progress" label="进度" width="120">
          <template #default="{ row }">
            <el-progress :percentage="row.progress || 0" :status="row.progress >= 100 ? 'success' : ''" />
          </template>
        </el-table-column>
        <el-table-column label="确认状态" width="180">
          <template #default="{ row }">
            <div v-if="row.status === 'PENDING_CONFIRM' || row.status === 'COMPLETED'">
              <div style="margin-bottom: 5px;">
                <el-tag size="small">{{ getConfirmedCount(row) }}/{{ getAllConfirmerCount(row) }} 已确认</el-tag>
              </div>
              <el-progress :percentage="getConfirmProgress(row)" :status="getConfirmProgress(row) === 100 ? 'success' : 'warning'" :stroke-width="8" />
            </div>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="weight" label="权重" width="80" />
        <el-table-column prop="status" label="状态" width="130">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isCrossDepartment" label="跨部门" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isCrossDepartment" type="warning">是</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="delayPenalty" label="延期扣分" width="100">
          <template #default="{ row }">
            <span v-if="row.delayPenalty > 0" style="color: #f56c6c;">-{{ row.delayPenalty }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="320">
          <template #default="{ row }">
            <el-button size="small" type="primary" v-if="row.status !== 'COMPLETED'" @click="updateProgress(row)">更新进度</el-button>
            <el-button size="small" type="success" v-if="row.status !== 'COMPLETED'" @click="completeTask(row.id)">完成</el-button>
            <el-button size="small" type="warning" v-if="row.status === 'PENDING_CONFIRM' && isMyPendingConfirm(row)" @click="viewConfirmations(row.id)">
              待我确认
            </el-button>
            <el-button size="small" type="info" v-if="row.status === 'PENDING_CONFIRM' && !isMyPendingConfirm(row)" @click="viewConfirmations(row.id)">
              查看确认
            </el-button>
            <el-button size="small" type="danger" @click="deleteTask(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="tasks.length === 0" description="暂无任务数据" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editId ? '编辑任务' : '新建任务'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="所属目标">
          <el-select v-model="form.objectiveId" style="width: 100%">
            <el-option v-for="obj in objectives" :key="obj.id" :label="obj.name" :value="obj.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-select v-model="form.ownerId" style="width: 100%">
            <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="协作人">
          <el-select v-model="form.collaboratorIds" multiple style="width: 100%" placeholder="选择协作人，跨部门任务需要多方确认">
            <el-option v-for="emp in employees" :key="emp.id" :label="emp.name" :value="emp.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker v-model="form.dueDate" type="datetime" style="width: 100%" />
        </el-form-item>
        <el-form-item label="权重">
          <el-input-number v-model="form.weight" :min="0" :max="1" :step="0.1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="跨部门">
          <el-switch v-model="form.isCrossDepartment" />
          <span style="margin-left: 10px; color: #909399; font-size: 12px;">开启后需要负责人和所有协作人确认</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="progressDialogVisible" title="更新进度" width="400px">
      <el-form label-width="80px">
        <el-form-item label="进度">
          <el-slider v-model="progressValue" :min="0" :max="100" show-input />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="progressDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProgress">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="confirmDialogVisible" title="任务确认详情" width="700px">
      <el-descriptions :column="2" border style="margin-bottom: 20px;">
        <el-descriptions-item label="任务名称">{{ currentTask?.name }}</el-descriptions-item>
        <el-descriptions-item label="负责人">{{ getEmployeeName(currentTask?.ownerId) }}</el-descriptions-item>
        <el-descriptions-item label="跨部门">
          <el-tag v-if="currentTask?.isCrossDepartment" type="warning">是</el-tag>
          <span v-else>否</span>
        </el-descriptions-item>
        <el-descriptions-item label="确认进度">
          {{ getConfirmedCount(currentTask) }}/{{ getAllConfirmerCount(currentTask) }} 已确认
        </el-descriptions-item>
      </el-descriptions>
      <el-table :data="confirmations" border>
        <el-table-column label="确认人" width="120">
          <template #default="{ row }">
            <div style="display: flex; align-items: center;">
              <el-avatar size="small" style="margin-right: 8px; background: #409EFF;">
                {{ getEmployeeName(row.confirmerId)?.charAt(0) }}
              </el-avatar>
              {{ getEmployeeName(row.confirmerId) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 'CONFIRMED' ? 'success' : row.status === 'REJECTED' ? 'danger' : 'warning'" size="large">
              {{ row.status === 'PENDING' ? '待确认' : row.status === 'CONFIRMED' ? '已确认' : '已驳回' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="意见" />
        <el-table-column prop="confirmTime" label="确认时间" width="170">
          <template #default="{ row }">{{ formatDate(row.confirmTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <template v-if="row.status === 'PENDING' && row.confirmerId === currentUser">
              <el-button size="small" type="success" @click="openConfirmDialog(row, 'CONFIRMED')">确认</el-button>
              <el-button size="small" type="danger" @click="openConfirmDialog(row, 'REJECTED')">驳回</el-button>
            </template>
            <span v-else-if="row.status === 'PENDING'" style="color: #909399;">等待他人确认</span>
            <span v-else>已处理</span>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="confirmDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="quickConfirmDialogVisible" title="确认意见" width="400px">
      <el-form label-width="80px">
        <el-form-item label="意见">
          <el-input v-model="confirmComment" type="textarea" placeholder="请输入确认意见（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="quickConfirmDialogVisible = false">取消</el-button>
        <el-button :type="confirmStatus === 'CONFIRMED' ? 'success' : 'danger'" @click="submitQuickConfirm">
          {{ confirmStatus === 'CONFIRMED' ? '确认通过' : '驳回' }}
        </el-button>
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
      objectives: [],
      employees: [],
      tasks: [],
      allTasks: [],
      selectedObjective: null,
      selectedStatus: null,
      currentUser: 1,
      onlyPendingMyConfirm: false,
      dialogVisible: false,
      progressDialogVisible: false,
      confirmDialogVisible: false,
      quickConfirmDialogVisible: false,
      editId: null,
      editingTaskId: null,
      progressValue: 0,
      confirmations: [],
      currentTask: null,
      quickConfirmTask: null,
      confirmStatus: '',
      confirmComment: '',
      form: {
        objectiveId: null,
        name: '',
        description: '',
        ownerId: null,
        collaboratorIds: [],
        dueDate: '',
        weight: 0.5,
        isCrossDepartment: false
      }
    }
  },
  computed: {
    pendingConfirmCount() {
      return this.tasks.filter(t => t.status === 'PENDING_CONFIRM').length
    },
    completedCount() {
      return this.tasks.filter(t => t.status === 'COMPLETED').length
    },
    pendingMyConfirmTasks() {
      return this.allTasks.filter(t => this.isMyPendingConfirm(t))
    }
  },
  mounted() {
    this.loadObjectives()
    this.loadEmployees()
    this.loadTasks()
  },
  methods: {
    async loadObjectives() {
      this.objectives = await api.get('/objectives')
    },
    async loadEmployees() {
      this.employees = await api.get('/employees')
    },
    async loadTasks() {
      let url = '/tasks'
      if (this.selectedObjective) {
        url = `/tasks/objective/${this.selectedObjective}`
      }
      this.allTasks = await api.get(url)
      this.filterTasks()
    },
    filterTasks() {
      let filtered = [...this.allTasks]
      
      if (this.selectedStatus) {
        filtered = filtered.filter(t => t.status === this.selectedStatus)
      }
      
      if (this.onlyPendingMyConfirm) {
        filtered = filtered.filter(t => this.isMyPendingConfirm(t))
      }
      
      this.tasks = filtered
    },
    getEmployeeName(id) {
      if (!id) return '-'
      const emp = this.employees.find(e => e.id === id)
      return emp ? emp.name : '-'
    },
    showAddDialog() {
      this.editId = null
      this.form = { objectiveId: this.selectedObjective, name: '', description: '', ownerId: null, collaboratorIds: [], dueDate: '', weight: 0.5, isCrossDepartment: false }
      this.dialogVisible = true
    },
    async submitForm() {
      await api.post('/tasks', this.form)
      this.dialogVisible = false
      this.loadTasks()
      ElMessage.success('保存成功')
    },
    async updateProgress(row) {
      this.editingTaskId = row.id
      this.progressValue = row.progress || 0
      this.progressDialogVisible = true
    },
    async submitProgress() {
      await api.put(`/tasks/${this.editingTaskId}/progress?progress=${this.progressValue}`)
      this.progressDialogVisible = false
      this.loadTasks()
      ElMessage.success('进度已更新')
    },
    async completeTask(id) {
      await api.post(`/tasks/${id}/complete`)
      this.loadTasks()
      ElMessage.success('任务已完成，进入确认流程')
    },
    async deleteTask(id) {
      await api.delete(`/tasks/${id}`)
      this.loadTasks()
      ElMessage.success('删除成功')
    },
    async viewConfirmations(taskId) {
      this.currentTask = this.allTasks.find(t => t.id === taskId)
      this.confirmations = await api.get(`/tasks/${taskId}/confirmations`)
      this.confirmDialogVisible = true
    },
    async quickConfirm(task, status) {
      this.quickConfirmTask = task
      this.confirmStatus = status
      this.confirmComment = ''
      this.quickConfirmDialogVisible = true
    },
    async submitQuickConfirm() {
      const confirmations = await api.get(`/tasks/${this.quickConfirmTask.id}/confirmations`)
      const myConfirmation = confirmations.find(c => c.confirmerId === this.currentUser && c.status === 'PENDING')
      if (myConfirmation) {
        await api.post(`/tasks/confirmations/${myConfirmation.id}/confirm`, { 
          status: this.confirmStatus, 
          comment: this.confirmComment 
        })
        ElMessage.success('操作成功')
      } else {
        ElMessage.warning('没有找到待您确认的记录')
      }
      this.quickConfirmDialogVisible = false
      this.loadTasks()
    },
    openConfirmDialog(row, status) {
      this.confirmStatus = status
      this.confirmComment = ''
      this.quickConfirmTask = { id: this.currentTask.id }
      this.quickConfirmDialogVisible = true
    },
    getConfirmedCount(task) {
      if (!task) return 0
      const confirmations = task._confirmations || []
      return confirmations.filter(c => c.status === 'CONFIRMED').length
    },
    getAllConfirmerCount(task) {
      if (!task) return 0
      let count = 1
      if (task.collaboratorIds) {
        count += task.collaboratorIds.length
      }
      return count
    },
    getConfirmProgress(task) {
      const total = this.getAllConfirmerCount(task)
      if (total === 0) return 0
      return Math.round((this.getConfirmedCount(task) / total) * 100)
    },
    isMyPendingConfirm(task) {
      if (!task || task.status !== 'PENDING_CONFIRM') return false
      const confirmations = task._confirmations || []
      return confirmations.some(c => c.confirmerId === this.currentUser && c.status === 'PENDING')
    },
    isOverdue(task) {
      if (!task.dueDate || task.status === 'COMPLETED') return false
      return new Date(task.dueDate) < new Date()
    },
    formatDate(date) {
      return date ? new Date(date).toLocaleString() : '-'
    },
    getStatusType(status) {
      const map = { PENDING: 'info', IN_PROGRESS: 'warning', PENDING_CONFIRM: 'warning', COMPLETED: 'success' }
      return map[status] || ''
    }
  },
  watch: {
    allTasks: {
      async handler(tasks) {
        for (const task of tasks) {
          if (task.status === 'PENDING_CONFIRM' || task.status === 'COMPLETED') {
            try {
              task._confirmations = await api.get(`/tasks/${task.id}/confirmations`)
            } catch (e) {
              task._confirmations = []
            }
          } else {
            task._confirmations = []
          }
        }
      },
      deep: true,
      immediate: true
    }
  }
}
</script>
