package com.taskflow.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity   //Will create a table for this class automatically
@Table(name = "tasks") //Specifies the table name in database is "tasks"
public class Task{

    @Id  //Marks this feild as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Tells database to auto-generate the Id
    private Long id;

    @Column(nullable = false)  //maps ths to a database con nullable= 'false' means this field cannot be zero
    private String name;

    @Column(nullable=false)
    private String status = "QUEUED";  //Default value is Queued

    @Column(nullable=false)
    private String priority;

    @Column(name = "created_at",nullable = false,updatable = false) //Updateable = 'false' means this value cant be changed afte
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

    public void setPriority(String Priority){
        this.priority = priority;
    }

    public void setCeatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }
}







