//Creating Service Interface to define contracts(what methods service must have)
package com.taskflow.service;

import com.taskflow.dto.TaskRequest;
import com.taskflow.dto.TaskResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {
    
    TaskResponse createTask(TaskRequest request);
    
    List<TaskResponse> getAllTasks();
    
    Page<TaskResponse> getAllTasksPaginated(Pageable pageable);
    
    TaskResponse getTaskById(Long id);
    
    TaskResponse updateTask(Long id, TaskRequest request);
    
    void deleteTask(Long id);
    
    List<TaskResponse> getTasksByStatus(String status);
    
    List<TaskResponse> getTasksByPriority(String priority);
}