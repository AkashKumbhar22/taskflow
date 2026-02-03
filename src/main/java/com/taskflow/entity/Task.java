package com.taskflow.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable=false)
    private String status = "QUEUED";

    @Column(nullable=false)
    private String priority;

    @Column(name = "created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    //Constructors
    public Task() {
    }

    public Task(String name,String priority){
        this.name = name;
        this.priority = priority;
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

    public void setCeatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }
}







