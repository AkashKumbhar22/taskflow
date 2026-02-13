package com.taskflow.service;

import com.taskflow.dto.TaskRequest;
import com.taskflow.dto.TaskResponse;
import com.taskflow.entity.Task;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.TaskRepository;
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

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public TaskResponse createTask(TaskRequest request) {
        // Convert DTO to Entity
        Task task = new Task();
        task.setName(request.getName());
        task.setPriority(request.getPriority());
        task.setStatus("QUEUED");
        
        // Save to database
        Task savedTask = taskRepository.save(task);
        
        // Convert Entity to DTO
        return mapToResponse(savedTask);
    }

    @Override
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
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        return mapToResponse(task);
    }

    @Override
    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        
        task.setName(request.getName());
        task.setPriority(request.getPriority());
        
        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
    }

    @Override
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        taskRepository.delete(task);
    }

    @Override
    public List<TaskResponse> getTasksByStatus(String status) {
        List<Task> tasks = taskRepository.findByStatus(status);
        return tasks.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getTasksByPriority(String priority) {
        List<Task> tasks = taskRepository.findByPriority(priority);
        return tasks.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
    }

    // Helper method: Convert Entity to DTO
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