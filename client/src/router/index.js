import Vue from 'vue'
import VueRouter from 'vue-router'
import DeviceDetail from '../views/DeviceDetail.vue'
import Devices from '../views/Devices.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'DeviceDetail',
    component: DeviceDetail
  },
  {
    path: '/device',
    name: 'DeviceDetail',
    component: DeviceDetail
  }
]

const router = new VueRouter({
  routes
})

export default router
