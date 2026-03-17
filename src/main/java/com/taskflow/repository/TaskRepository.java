package com.taskflow.repository;

import com.taskflow.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
 
    // Find by status
    List<Task> findByStatus(String status);
    
    // Find by priority
    List<Task> findByPriority(String priority);
    
    // Find by status AND priority
    List<Task> findByStatusAndPriority(String status, String priority);
    
    // Find by date range
    List<Task> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // Search by name (case-insensitive, partial match)
    List<Task> findByNameContainingIgnoreCase(String keyword);
    
    // Find by status, ordered by priority
    List<Task> findByStatusOrderByPriorityDesc(String status);
  
    // Find tasks by status with custom ordering
    @Query("SELECT t FROM Task t WHERE t.status = :status ORDER BY t.createdAt DESC")
    List<Task> findRecentTasksByStatus(@Param("status") String status);
    
    // Find high priority tasks
    @Query("SELECT t FROM Task t WHERE t.priority = 'HIGH' AND t.status = 'QUEUED'")
    List<Task> findHighPriorityQueuedTasks();
    
    // Count tasks by status
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    long countTasksByStatus(@Param("status") String status);
    
    // Find tasks created in last N days
    @Query(value = "SELECT * FROM tasks WHERE created_at > NOW() - INTERVAL ':days days'", 
           nativeQuery = true)
    List<Task> findTasksCreatedInLastDays(@Param("days") int days);
  
    // Check if task exists by name
    boolean existsByName(String name);
    
    // Count by priority
    long countByPriority(String priority);
    
    // Delete by status (bulk delete)
    void deleteByStatus(String status);
}