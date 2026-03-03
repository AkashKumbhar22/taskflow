import React, { useState, useEffect, useCallback } from 'react';
import { taskAPI } from './services/api';
import TaskForm from './components/TaskForm';
import TaskItem from './components/TaskItem';
import FilterBar from './components/FilterBar';

function App() {
  // State management
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [filterPriority, setFilterPriority] = useState('');
  const [searchKeyword, setSearchKeyword] = useState('');
  const [backendHealth, setBackendHealth] = useState(null);
  const [stats, setStats] = useState({
    total: 0,
    queued: 0,
    inProgress: 0,
    completed: 0,
    failed: 0,
  });

  // Load tasks on component mount
  useEffect(() => {
    loadTasks();
    checkBackendHealth();
    
    // Check health every 30 seconds
    const healthInterval = setInterval(checkBackendHealth, 30000);
    
    return () => clearInterval(healthInterval);
  }, []);

  // Reload tasks when filters change
  useEffect(() => {
    if (filterStatus) {
      loadTasksByStatus(filterStatus);
    } else if (filterPriority) {
      loadTasksByPriority(filterPriority);
    } else if (searchKeyword) {
      searchTasks(searchKeyword);
    } else {
      loadTasks();
    }
  }, [filterStatus, filterPriority, searchKeyword]);

  // Check backend health
  const checkBackendHealth = async () => {
    try {
      const response = await taskAPI.getHealth();
      setBackendHealth(response.data.status);
    } catch (err) {
      console.error('Health check failed:', err);
      setBackendHealth('DOWN');
    }
  };

  // Calculate statistics from tasks
  const calculateStats = useCallback((taskList) => {
    const stats = {
      total: taskList.length,
      queued: taskList.filter(t => t.status === 'QUEUED').length,
      inProgress: taskList.filter(t => t.status === 'IN_PROGRESS').length,
      completed: taskList.filter(t => t.status === 'COMPLETED').length,
      failed: taskList.filter(t => t.status === 'FAILED').length,
    };
    setStats(stats);
  }, []);

  // Load all tasks
  const loadTasks = async () => {
    try {
      setLoading(true);
      setError('');
      const response = await taskAPI.getAllTasks();
      setTasks(response.data);
      calculateStats(response.data);
    } catch (err) {
      console.error('Error loading tasks:', err);
      setError('Failed to load tasks. Make sure backend is running on http://localhost:8080');
    } finally {
      setLoading(false);
    }
  };

  // Load tasks by status
  const loadTasksByStatus = async (status) => {
    try {
      setLoading(true);
      setError('');
      const response = await taskAPI.getTasksByStatus(status);
      setTasks(response.data);
      calculateStats(response.data);
    } catch (err) {
      console.error('Error filtering by status:', err);
      setError('Failed to filter tasks by status');
    } finally {
      setLoading(false);
    }
  };

  // Load tasks by priority
  const loadTasksByPriority = async (priority) => {
    try {
      setLoading(true);
      setError('');
      const response = await taskAPI.getTasksByPriority(priority);
      setTasks(response.data);
      calculateStats(response.data);
    } catch (err) {
      console.error('Error filtering by priority:', err);
      setError('Failed to filter tasks by priority');
    } finally {
      setLoading(false);
    }
  };

  // Search tasks
  const searchTasks = async (keyword) => {
    try {
      setLoading(true);
      setError('');
      const response = await taskAPI.searchTasks(keyword);
      setTasks(response.data);
      calculateStats(response.data);
    } catch (err) {
      console.error('Error searching tasks:', err);
      setError('Failed to search tasks');
    } finally {
      setLoading(false);
    }
  };

  // Create new task
  const handleCreateTask = async (taskData) => {
    await taskAPI.createTask(taskData);
    loadTasks(); // Reload all tasks after creation
  };

  // Update task
  const handleUpdateTask = async (id, taskData) => {
    await taskAPI.updateTask(id, taskData);
    loadTasks(); // Reload all tasks after update
  };

  // Delete task
  const handleDeleteTask = async (id) => {
    if (window.confirm('Are you sure you want to delete this task?')) {
      try {
        await taskAPI.deleteTask(id);
        loadTasks(); // Reload all tasks after deletion
      } catch (err) {
        console.error('Error deleting task:', err);
        alert('Failed to delete task. Please try again.');
      }
    }
  };

  // Handle filter change
  const handleFilterChange = (filterType, value) => {
    if (filterType === 'status') {
      setFilterStatus(value);
      setFilterPriority('');
      setSearchKeyword('');
    } else if (filterType === 'priority') {
      setFilterPriority(value);
      setFilterStatus('');
      setSearchKeyword('');
    }
  };

  // Handle search
  const handleSearch = (keyword) => {
    setSearchKeyword(keyword);
    setFilterStatus('');
    setFilterPriority('');
  };

  // Reset all filters
  const handleReset = () => {
    setSearchKeyword('');
    setFilterStatus('');
    setFilterPriority('');
    loadTasks();
  };

  return (
    <div className="min-h-screen py-8 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        {/* HEADER */}
        <div className="text-center mb-12">
          <div className="flex items-center justify-center mb-4">
            <svg 
              className="w-16 h-16 text-white mr-4" 
              fill="none" 
              stroke="currentColor" 
              viewBox="0 0 24 24"
            >
              <path 
                strokeLinecap="round" 
                strokeLinejoin="round" 
                strokeWidth={2} 
                d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01" 
              />
            </svg>
            <h1 className="text-6xl font-bold text-white drop-shadow-lg">
              TaskFlow
            </h1>
          </div>
          <p className="text-2xl text-white opacity-90 mb-6">
            Manage Your Tasks Efficiently
          </p>
          
          {/* Backend Health Status */}
          <div className="flex items-center justify-center gap-4">
            <span className={`inline-flex items-center px-4 py-2 rounded-full text-sm font-semibold shadow-lg ${
              backendHealth === 'UP' 
                ? 'bg-green-100 text-green-800 border-2 border-green-300' 
                : 'bg-red-100 text-red-800 border-2 border-red-300'
            }`}>
              <span className={`w-3 h-3 rounded-full mr-2 animate-pulse ${
                backendHealth === 'UP' ? 'bg-green-600' : 'bg-red-600'
              }`}></span>
              Backend: {backendHealth || 'Checking...'}
            </span>
            
            <button
              onClick={checkBackendHealth}
              className="bg-white bg-opacity-20 hover:bg-opacity-30 text-white px-4 py-2 rounded-full text-sm font-semibold transition-all"
              title="Refresh health status"
            >
              🔄 Refresh
            </button>
          </div>
        </div>

        {/* GLOBAL ERROR MESSAGE */}
        {error && (
          <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-4 rounded-lg mb-8 shadow-lg">
            <div className="flex items-center">
              <svg 
                className="w-6 h-6 mr-3" 
                fill="currentColor" 
                viewBox="0 0 20 20"
              >
                <path 
                  fillRule="evenodd" 
                  d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" 
                  clipRule="evenodd" 
                />
              </svg>
              <div>
                <p className="font-bold">Error</p>
                <p className="text-sm">{error}</p>
              </div>
            </div>
          </div>
        )}

        {/* STATISTICS DASHBOARD */}
        <div className="grid grid-cols-2 md:grid-cols-5 gap-4 mb-8">
          <div className="bg-white rounded-lg shadow-lg p-4 text-center hover:shadow-xl transition-shadow">
            <p className="text-3xl font-bold text-purple-600">{stats.total}</p>
            <p className="text-gray-600 font-semibold">Total Tasks</p>
          </div>
          <div className="bg-white rounded-lg shadow-lg p-4 text-center hover:shadow-xl transition-shadow">
            <p className="text-3xl font-bold text-blue-600">{stats.queued}</p>
            <p className="text-gray-600 font-semibold">Queued</p>
          </div>
          <div className="bg-white rounded-lg shadow-lg p-4 text-center hover:shadow-xl transition-shadow">
            <p className="text-3xl font-bold text-yellow-600">{stats.inProgress}</p>
            <p className="text-gray-600 font-semibold">In Progress</p>
          </div>
          <div className="bg-white rounded-lg shadow-lg p-4 text-center hover:shadow-xl transition-shadow">
            <p className="text-3xl font-bold text-green-600">{stats.completed}</p>
            <p className="text-gray-600 font-semibold">Completed</p>
          </div>
          <div className="bg-white rounded-lg shadow-lg p-4 text-center hover:shadow-xl transition-shadow">
            <p className="text-3xl font-bold text-red-600">{stats.failed}</p>
            <p className="text-gray-600 font-semibold">Failed</p>
          </div>
        </div>

        {/* MAIN CONTENT GRID */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* LEFT COLUMN - CREATE FORM */}
          <div className="lg:col-span-1">
            <TaskForm onTaskCreated={handleCreateTask} />
          </div>

          {/* RIGHT COLUMN - TASK LIST */}
          <div className="lg:col-span-2">
            {/* Filter Bar */}
            <FilterBar
              onFilterChange={handleFilterChange}
              onSearch={handleSearch}
              onReset={handleReset}
            />

            {/* Loading State */}
            {loading ? (
              <div className="bg-white rounded-lg shadow-lg p-12 text-center">
                <div className="flex flex-col items-center">
                  <svg 
                    className="animate-spin h-16 w-16 text-purple-600 mb-4" 
                    viewBox="0 0 24 24"
                  >
                    <circle 
                      className="opacity-25" 
                      cx="12" 
                      cy="12" 
                      r="10" 
                      stroke="currentColor" 
                      strokeWidth="4"
                      fill="none"
                    />
                    <path 
                      className="opacity-75" 
                      fill="currentColor" 
                      d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                    />
                  </svg>
                  <p className="text-xl font-semibold text-gray-600">Loading tasks...</p>
                  <p className="text-sm text-gray-500 mt-2">Please wait</p>
                </div>
              </div>
            ) : tasks.length === 0 ? (
              /* Empty State */
              <div className="bg-white rounded-lg shadow-lg p-12 text-center">
                <svg
                  className="mx-auto h-24 w-24 text-gray-400 mb-4"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"
                  />
                </svg>
                <h3 className="text-2xl font-bold text-gray-900 mb-2">No tasks found</h3>
                <p className="text-gray-500 mb-6">
                  {searchKeyword || filterStatus || filterPriority
                    ? 'Try adjusting your filters or search terms'
                    : 'Get started by creating your first task'}
                </p>
                {(searchKeyword || filterStatus || filterPriority) && (
                  <button
                    onClick={handleReset}
                    className="bg-purple-600 text-white px-6 py-3 rounded-lg hover:bg-purple-700 transition-colors font-semibold"
                  >
                    Clear Filters
                  </button>
                )}
              </div>
            ) : (
              /* Task List */
              <div className="space-y-4">
                {tasks.map((task) => (
                  <TaskItem
                    key={task.id}
                    task={task}
                    onUpdate={handleUpdateTask}
                    onDelete={handleDeleteTask}
                  />
                ))}
              </div>
            )}
          </div>
        </div>

        {/* FOOTER */}
        <div className="mt-16 text-center">
          <div className="bg-white bg-opacity-20 backdrop-blur-lg rounded-lg p-6 text-white">
            <p className="text-lg font-semibold mb-2">
              🚀 TaskFlow - Your Task Management Solution
            </p>
            <p className="text-sm opacity-90">
              Built with React, Spring Boot, PostgreSQL, Redis & Docker
            </p>
            <p className="text-xs opacity-75 mt-2">
              © 2026 TaskFlow | Developed for Software Engineering Interview
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;