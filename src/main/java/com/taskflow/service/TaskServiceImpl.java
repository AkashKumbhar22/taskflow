package com.taskflow.service;

import com.taskflow.dto.TaskRequest;
import com.taskflow.dto.TaskResponse;
import com.taskflow.entity.Task;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskResponse createTask(TaskRequest request) {
        logger.info("Creating new task with name: {}", request.getName());

        Task task = new Task();
        task.setName(request.getName());
        task.setPriority(request.getPriority());
        task.setStatus("QUEUED");   // default status

        Task savedTask = taskRepository.save(task);

        logger.info("Task created successfully with ID: {}", savedTask.getId());

        return mapToResponse(savedTask);
    }

    @Override
    @Cacheable(value = "tasks", key = "'all'")
    public List<TaskResponse> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();

        return tasks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<TaskResponse> getAllTasksPaginated(Pageable pageable) {
        Page<Task> taskPage = taskRepository.findAll(pageable);
        return taskPage.map(this::mapToResponse);
    }

    @Override
    @Cacheable(value = "tasks", key = "#id")
    public TaskResponse getTaskById(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task", "id", id)
                );

        return mapToResponse(task);
    }

    @Override
    @CachePut(value = "tasks", key = "#id")
    @CacheEvict(value = "tasks", key = "'all'")
    public TaskResponse updateTask(Long id, TaskRequest request) {

        logger.info("Updating task with ID: {}", id);

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task", "id", id)
                );

        // 🔥 FIXED PART
        task.setName(request.getName());
        task.setPriority(request.getPriority());
        task.setStatus(request.getStatus());   // IMPORTANT LINE

        Task updatedTask = taskRepository.save(task);

        logger.info("Task updated successfully with ID: {}", updatedTask.getId());

        return mapToResponse(updatedTask);
    }

    // ================= DELETE =================
    @Override
    @CacheEvict(value = "tasks", allEntries = true)
    public void deleteTask(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Task", "id", id)
                );

        taskRepository.delete(task);
    }

    // ================= FILTER BY STATUS =================
    @Override
    @Cacheable(value = "tasks", key = "'status-' + #status")
    public List<TaskResponse> getTasksByStatus(String status) {

        List<Task> tasks = taskRepository.findByStatus(status);

        return tasks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "tasks", key = "'priority-' + #priority")
    public List<TaskResponse> getTasksByPriority(String priority) {

        List<Task> tasks = taskRepository.findByPriority(priority);

        return tasks.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> searchTasksByName(String keyword) {

        List<Task> tasks = taskRepository.findByNameContainingIgnoreCase(keyword);

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