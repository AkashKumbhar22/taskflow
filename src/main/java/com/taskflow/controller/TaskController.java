package com.taskflow.controller;

import com.taskflow.dto.TaskRequest;
import com.taskflow.dto.TaskResponse;
import com.taskflow.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;

    // CREATE
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        logger.info("POST /api/tasks - Creating task: {}", request.getName());
        TaskResponse response = taskService.createTask(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        logger.info("GET /api/tasks - Fetching all tasks");
        List<TaskResponse> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    // READ ALL PAGINATED
    @GetMapping("/paginated")
    public ResponseEntity<Page<TaskResponse>> getAllTasksPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        
        logger.info("GET /api/tasks/paginated - Page: {}, Size: {}, SortBy: {}, Direction: {}", 
                    page, size, sortBy, direction);
        
        Sort sort = direction.equalsIgnoreCase("ASC") 
                    ? Sort.by(sortBy).ascending() 
                    : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TaskResponse> taskPage = taskService.getAllTasksPaginated(pageable);
        
        logger.info("Returning {} tasks out of {} total", 
                    taskPage.getNumberOfElements(), taskPage.getTotalElements());
        
        return ResponseEntity.ok(taskPage);
    }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        logger.info("GET /api/tasks/{} - Fetching task", id);
        TaskResponse task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    // READ BY STATUS
    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(@PathVariable String status) {
        logger.info("GET /api/tasks/status/{} - Fetching tasks by status", status);
        List<TaskResponse> tasks = taskService.getTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    // READ BY PRIORITY
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TaskResponse>> getTasksByPriority(@PathVariable String priority) {
        logger.info("GET /api/tasks/priority/{} - Fetching tasks by priority", priority);
        List<TaskResponse> tasks = taskService.getTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }

    // SEARCH BY NAME
    @GetMapping("/search")
    public ResponseEntity<List<TaskResponse>> searchTasks(@RequestParam String keyword) {
        logger.info("GET /api/tasks/search?keyword={} - Searching tasks", keyword);
        List<TaskResponse> tasks = taskService.searchTasksByName(keyword);
        return ResponseEntity.ok(tasks);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        logger.info("PUT /api/tasks/{} - Updating task", id);
        TaskResponse updated = taskService.updateTask(id, request);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        logger.info("DELETE /api/tasks/{} - Deleting task", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}