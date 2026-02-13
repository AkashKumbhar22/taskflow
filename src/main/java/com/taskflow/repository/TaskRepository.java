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
    
    // Method name query - Spring generates SQL automatically
    List<Task> findByStatus(String status);
    
    List<Task> findByPriority(String priority);
    
    List<Task> findByStatusAndPriority(String status, String priority);
    
    List<Task> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<Task> findByNameContainingIgnoreCase(String keyword);
    
    // Custom JPQL query
    @Query("SELECT t FROM Task t WHERE t.status = :status ORDER BY t.priority DESC")
    List<Task> findTasksByStatusOrderedByPriority(@Param("status") String status);
    
    // Native SQL query
    @Query(value = "SELECT * FROM tasks WHERE priority = ?1 AND created_at > NOW() - INTERVAL '7 days'", 
           nativeQuery = true)
    List<Task> findRecentTasksByPriority(String priority);
    
    // Count query
    long countByStatus(String status);
    
    // Exists query
    boolean existsByName(String name);
    
    // Delete query
    void deleteByStatus(String status);
}
