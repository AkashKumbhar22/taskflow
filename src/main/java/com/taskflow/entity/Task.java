package com.taskflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank(message="Name cannot be empty")
    @Size(min = 3, max = 255, message = "name must be between 3 and 255 characters")
    @Column(nullable = false)
    private String name;


    @Pattern(regexp = "QUEUED|IN_PROGRESS|COMPLETED|FAILED",
            message = "Status must be QUEUED,IN_PROGRESS,COMPLETED, or FAILED")
    @Column(nullable=false)
    private String status = "QUEUED";


    @Pattern(regexp = "HIGH|MEDIUM|LOW",
        message = "Priority must be HIGH , MEDIUM , or LOW")
    @Column(nullable=false)
    private String priority;


    @Column(name = "created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    @Column(name="updated_at")
    private LocalDateTime updatedAt;


    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdated(){
        updatedAt = LocalDateTime.now();
    }


    //Constructors
    public Task() {
    }


    public Task(String name,String priority){
        this.name = name;
        this.priority = priority;
        this.status = "QUEUED";
    }


    //Getters and Setters
    public Long getId(){
        return id;
    }


    public void SetId(Long id){
        this.id = id;
    }


    public String getName(){
        return name;
    }


    public void setName(String name){
        this.name = name;
    }


    public String getStatus(){
        return status;
    }


    public void setStatus(String status){
        this.status=status;
    }


    public String getPriority(){
        return priority;
    }


    public void setPriority(String priority){
        this.priority = priority;
    }


    public LocalDateTime getCreatedAt() {
       return createdAt;
    }


    public void setCeatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }


    public LocalDateTime getUpdatedAt(){
        return updatedAt;
    }


    public void setUpdatedAt(LocalDateTime updatedAt){
        this.updatedAt = updatedAt;
    }


    @Override
    public String toString(){
        return "Task{" +
                "id=" + id +
                ", name=' " + name + '\'' +
                ", status=' " + status + '\'' +
                ", priority=' " + priority + '\'' +
                ", createdAt=" + createdAt +  
                ",updatedAt=" + updatedAt +
                '}';
 }
}







