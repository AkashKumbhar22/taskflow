package com.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public class TaskRequest {
   
    @NotBlank(message = "name cannot be empty")
    @Size(min = 3, max = 255, message="name must be between 3 and 255 characters")
    private String name;


    @Pattern(regexp = "HIGH|MEDIUM|LOW",
            message = "Priority must be HIGH, MEDIUM, or LOW")
    private String priority;
    

    //Constructors
    public TaskRequest(){

    }  
    
    public TaskRequest(String name, String priority){
        this.name = name;
        this.priority = priority;
    }

    //Getters and Setters
    public String getName(){
        return name;
    }

    public void setname(String name){
        this.name = name;
    }

    public String getPriority(){
        return priority;
    }


    @Override
    public String toString(){
        return "TaskRequest{" +
                "name='" + name + '\'' +
                ", priority='" +priority + '\'' +
                '}';
    }
}
