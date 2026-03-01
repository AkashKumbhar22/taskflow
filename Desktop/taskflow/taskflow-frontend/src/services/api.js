import axios from 'axios';

// Backend API base URL
const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance with default config
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // 10 seconds timeout
});

// Request interceptor (for adding auth tokens in future)
api.interceptors.request.use(
  (config) => {
    console.log(`API Request: ${config.method.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => {
    console.error('API Request Error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor (for handling errors globally)
api.interceptors.response.use(
  (response) => {
    console.log(`API Response: ${response.status} ${response.config.url}`);
    return response;
  },
  (error) => {
    console.error('API Response Error:', error);
    if (error.response) {
      // Server responded with error status
      console.error('Error Response:', error.response.data);
    } else if (error.request) {
      // Request made but no response received
      console.error('No response received:', error.request);
    } else {
      // Error in request setup
      console.error('Error:', error.message);
    }
    return Promise.reject(error);
  }
);

// Task API endpoints
export const taskAPI = {
  /**
   * Get all tasks
   * @returns {Promise} Array of tasks
   */
  getAllTasks: () => {
    return api.get('/tasks');
  },

  /**
   * Get task by ID
   * @param {number} id - Task ID
   * @returns {Promise} Task object
   */
  getTaskById: (id) => {
    return api.get(`/tasks/${id}`);
  },

  /**
   * Create new task
   * @param {Object} task - Task object {name, priority}
   * @returns {Promise} Created task
   */
  createTask: (task) => {
    return api.post('/tasks', task);
  },

  /**
   * Update existing task
   * @param {number} id - Task ID
   * @param {Object} task - Updated task data
   * @returns {Promise} Updated task
   */
  updateTask: (id, task) => {
    return api.put(`/tasks/${id}`, task);
  },

  /**
   * Delete task
   * @param {number} id - Task ID
   * @returns {Promise} Empty response
   */
  deleteTask: (id) => {
    return api.delete(`/tasks/${id}`);
  },

  /**
   * Get paginated tasks
   * @param {number} page - Page number (0-based)
   * @param {number} size - Page size
   * @param {string} sortBy - Sort field
   * @param {string} direction - Sort direction (ASC/DESC)
   * @returns {Promise} Page object with tasks
   */
  getPaginatedTasks: (page = 0, size = 10, sortBy = 'id', direction = 'ASC') => {
    return api.get(`/tasks/paginated`, {
      params: { page, size, sortBy, direction }
    });
  },

  /**
   * Get tasks by status
   * @param {string} status - Task status (QUEUED/IN_PROGRESS/COMPLETED/FAILED)
   * @returns {Promise} Array of tasks
   */
  getTasksByStatus: (status) => {
    return api.get(`/tasks/status/${status}`);
  },

  /**
   * Get tasks by priority
   * @param {string} priority - Task priority (HIGH/MEDIUM/LOW)
   * @returns {Promise} Array of tasks
   */
  getTasksByPriority: (priority) => {
    return api.get(`/tasks/priority/${priority}`);
  },

  /**
   * Search tasks by name
   * @param {string} keyword - Search keyword
   * @returns {Promise} Array of matching tasks
   */
  searchTasks: (keyword) => {
    return api.get(`/tasks/search`, {
      params: { keyword }
    });
  },

  /**
   * Get backend health status
   * @returns {Promise} Health status
   */
  getHealth: () => {
    return axios.get('http://localhost:8080/actuator/health');
  },

  /**
   * Get cache statistics
   * @returns {Promise} Cache stats
   */
  getCacheStats: () => {
    return api.get('/cache/stats');
  },

  /**
   * Clear cache
   * @returns {Promise} Success message
   */
  clearCache: () => {
    return api.delete('/cache/clear');
  },
};

export default api;