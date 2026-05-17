<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span style="font-size: 18px; font-weight: bold;">欢迎使用员工绩效任务系统</span>
          </template>
          <el-steps :active="currentStep" finish-status="success" align-center>
            <el-step title="创建绩效周期" description="主管设置周期时间和奖金池" />
            <el-step title="拆分员工目标" description="为每个员工设置绩效目标和权重" />
            <el-step title="创建任务" description="将目标拆分为可执行的任务" />
            <el-step title="任务执行与确认" description="员工完成任务，跨部门任务需多方确认" />
            <el-step title="绩效统计与申诉" description="系统自动统计，员工可申诉" />
            <el-step title="奖金分配" description="根据绩效结果分配奖金池" />
          </el-steps>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="8">
        <el-card shadow="hover" style="height: 200px;">
          <div style="text-align: center;">
            <div style="font-size: 48px; color: #409EFF;">📅</div>
            <h3>绩效周期管理</h3>
            <p style="color: #909399; margin-bottom: 15px;">创建和管理绩效周期</p>
            <el-button type="primary" @click="$router.push('/cycles')">立即开始</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" style="height: 200px;">
          <div style="text-align: center;">
            <div style="font-size: 48px; color: #67C23A;">🎯</div>
            <h3>员工目标管理</h3>
            <p style="color: #909399; margin-bottom: 15px;">为员工拆分目标，设置权重</p>
            <el-button type="success" @click="$router.push('/objectives')">去设置</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" style="height: 200px;">
          <div style="text-align: center;">
            <div style="font-size: 48px; color: #E6A23C;">📋</div>
            <h3>任务管理</h3>
            <p style="color: #909399; margin-bottom: 15px;">创建任务，跟踪进度</p>
            <el-button type="warning" @click="$router.push('/tasks')">去管理</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>当前绩效周期</span>
          </template>
          <el-table :data="activeCycles" border size="small">
            <el-table-column prop="name" label="周期名称" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag type="success">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="bonusPool" label="奖金池" width="120">
              <template #default="{ row }">¥ {{ row.bonusPool }}</template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button size="small" type="primary" @click="$router.push('/cycles')">拆分目标</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="activeCycles.length === 0" description="暂无进行中的绩效周期" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>待办事项</span>
          </template>
          <div style="line-height: 2;">
            <p v-if="pendingConfirmCount > 0">
              <el-tag type="warning" style="margin-right: 10px;">{{ pendingConfirmCount }}</el-tag>
              个任务等待确认
            </p>
            <p v-if="pendingAppealCount > 0">
              <el-tag type="danger" style="margin-right: 10px;">{{ pendingAppealCount }}</el-tag>
              个申诉待处理
            </p>
            <p v-if="delayedTaskCount > 0">
              <el-tag type="danger" style="margin-right: 10px;">{{ delayedTaskCount }}</el-tag>
              个任务已延期
            </p>
            <p v-if="pendingConfirmCount === 0 && pendingAppealCount === 0 && delayedTaskCount === 0" style="color: #67C23A;">
              ✅ 暂无待办事项
            </p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import api from '../api'

export default {
  data() {
    return {
      currentStep: 0,
      activeCycles: [],
      pendingConfirmCount: 0,
      pendingAppealCount: 0,
      delayedTaskCount: 0
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    async loadData() {
      const cycles = await api.get('/cycles')
      this.activeCycles = cycles.filter(c => c.status === 'ACTIVE')
      
      try {
        const pendingTasks = await api.get('/statistics/tasks/pending-confirmation')
        this.pendingConfirmCount = pendingTasks.length
      } catch(e) {}
      
      try {
        const appeals = await api.get('/appeals/pending')
        this.pendingAppealCount = appeals.length
      } catch(e) {}
      
      try {
        const delayed = await api.get('/tasks/delayed')
        this.delayedTaskCount = delayed.length
      } catch(e) {}

      if (this.activeCycles.length > 0) {
        this.currentStep = 1
      }
    }
  }
}
</script>
