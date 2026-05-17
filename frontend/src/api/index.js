import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.response.use(
  response => {
    if (response.data.code === 200) {
      return response.data.data
    }
    return Promise.reject(response.data.message)
  },
  error => Promise.reject(error.message)
)

export default api
