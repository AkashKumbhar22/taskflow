package com.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class TaskRequest {

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
    private String name;

    @Pattern(regexp = "HIGH|MEDIUM|LOW",
            message = "Priority must be HIGH, MEDIUM, or LOW")
    private String priority;

    @Pattern(regexp = "QUEUED|COMPLETED|FAILED|IN_PROGRESS",
            message = "Status must be QUEUED, COMPLETED, FAILED, or IN_PROGRESS")
    private String status;   // 🔥 ADDED

    // ================= CONSTRUCTORS =================
    public TaskRequest() {
    }

    public TaskRequest(String name, String priority, String status) {
        this.name = name;
        this.priority = priority;
        this.status = status;
    }

    // ================= GETTERS & SETTERS =================

    public String getName() {
        return name;
    }

    // 🔥 FIXED (capital N)
    public void setName(String name) {
        this.name = name;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    // 🔥 ADDED
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TaskRequest{" +
                "name='" + name + '\'' +
                ", priority='" + priority + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}