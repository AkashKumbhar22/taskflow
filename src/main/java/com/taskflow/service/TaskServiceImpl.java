package com.taskflow.service;

import com.taskflow.dto.TaskRequest;
import com.taskflow.dto.TaskResponse;
import com.taskflow.entity.Task;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public TaskResponse createTask(TaskRequest request) {
        logger.info("Creating new task with name: {}", request.getName());
        
        Task task = new Task();
        task.setName(request.getName());
        task.setPriority(request.getPriority());
        task.setStatus("QUEUED");
        
        Task savedTask = taskRepository.save(task);
        
        logger.info("Task created successfully with ID: {}", savedTask.getId());
        return mapToResponse(savedTask);
    }

    @Override
    public List<TaskResponse> getAllTasks() {
        logger.debug("Fetching all tasks from database");
        List<Task> tasks = taskRepository.findAll();
        logger.info("Retrieved {} tasks", tasks.size());
        return tasks.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
    }

    @Override
    public Page<TaskResponse> getAllTasksPaginated(Pageable pageable) {
        logger.debug("Fetching paginated tasks - Page: {}, Size: {}", 
                     pageable.getPageNumber(), pageable.getPageSize());
        Page<Task> taskPage = taskRepository.findAll(pageable);
        logger.info("Retrieved {} tasks out of {} total", 
                    taskPage.getNumberOfElements(), taskPage.getTotalElements());
        return taskPage.map(this::mapToResponse);
    }

    @Override
    public TaskResponse getTaskById(Long id) {
        logger.debug("Fetching task with ID: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Task not found with ID: {}", id);
                    return new ResourceNotFoundException("Task", "id", id);
                });
        logger.info("Task found: {}", task.getName());
        return mapToResponse(task);
    }

    @Override
    public TaskResponse updateTask(Long id, TaskRequest request) {
        logger.info("Updating task with ID: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Task not found for update with ID: {}", id);
                    return new ResourceNotFoundException("Task", "id", id);
                });
        
        logger.debug("Old task name: {}, New task name: {}", task.getName(), request.getName());
        task.setName(request.getName());
        task.setPriority(request.getPriority());
        
        Task updatedTask = taskRepository.save(task);
        logger.info("Task updated successfully with ID: {}", updatedTask.getId());
        return mapToResponse(updatedTask);
    }

    @Override
    public void deleteTask(Long id) {
        logger.info("Deleting task with ID: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Task not found for deletion with ID: {}", id);
                    return new ResourceNotFoundException("Task", "id", id);
                });
        taskRepository.delete(task);
        logger.info("Task deleted successfully with ID: {}", id);
    }

    @Override
    public List<TaskResponse> getTasksByStatus(String status) {
        logger.debug("Fetching tasks with status: {}", status);
        List<Task> tasks = taskRepository.findByStatus(status);
        logger.info("Found {} tasks with status: {}", tasks.size(), status);
        return tasks.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getTasksByPriority(String priority) {
        logger.debug("Fetching tasks with priority: {}", priority);
        List<Task> tasks = taskRepository.findByPriority(priority);
        logger.info("Found {} tasks with priority: {}", tasks.size(), priority);
        return tasks.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> searchTasksByName(String keyword) {
        logger.debug("Searching tasks with keyword: {}", keyword);
        List<Task> tasks = taskRepository.findByNameContainingIgnoreCase(keyword);
        logger.info("Found {} tasks matching keyword: {}", tasks.size(), keyword);
        return tasks.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
    }

    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getName(),
            task.getStatus(),
            task.getPriority(),
            task.getCreatedAt(),
            task.getUpdatedAt()
        );
    }
}


