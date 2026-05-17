import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', component: () => import('../views/Dashboard.vue') },
  { path: '/cycles', component: () => import('../views/Cycles.vue') },
  { path: '/objectives', component: () => import('../views/Objectives.vue') },
  { path: '/tasks', component: () => import('../views/Tasks.vue') },
  { path: '/appeals', component: () => import('../views/Appeals.vue') },
  { path: '/statistics', component: () => import('../views/Statistics.vue') },
  { path: '/bonus', component: () => import('../views/Bonus.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
