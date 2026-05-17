<template>
  <el-container style="height: 100vh">
    <el-header style="background: #409EFF; color: white; display: flex; align-items: center; padding: 0 20px;">
      <h2 style="margin: 0">员工绩效任务系统</h2>
      <el-menu
        mode="horizontal"
        :default-active="$route.path"
        router
        style="margin-left: 30px; background: #409EFF; border: none"
        text-color="white"
        active-text-color="#ffd04b"
      >
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/cycles">绩效周期</el-menu-item>
        <el-menu-item index="/objectives">员工目标</el-menu-item>
        <el-menu-item index="/tasks">任务管理</el-menu-item>
        <el-menu-item index="/appeals">申诉管理</el-menu-item>
        <el-menu-item index="/statistics">统计分析</el-menu-item>
        <el-menu-item index="/bonus">奖金分配</el-menu-item>
      </el-menu>
    </el-header>
    <el-main style="padding: 20px;">
      <router-view />
    </el-main>
  </el-container>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
body { margin: 0; }
</style>
