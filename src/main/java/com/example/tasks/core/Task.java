package com.example.tasks.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;
import java.util.Objects;

@ApiModel(description = "Task entity")
public class Task {
    
    @JsonProperty
    @ApiModelProperty(value = "Unique task identifier", example = "1")
    private Long id;
    
    @JsonProperty
    @ApiModelProperty(value = "Task title", example = "Complete project documentation", required = true)
    private String title;
    
    @JsonProperty
    @ApiModelProperty(value = "Task description", example = "Write comprehensive documentation for the API")
    private String description;
    
    @JsonProperty
    @ApiModelProperty(value = "Task status", example = "pending")
    private TaskStatus status;
    
    @JsonProperty
    @ApiModelProperty(value = "Task priority", example = "high")
    private TaskPriority priority;
    
    @JsonProperty
    @ApiModelProperty(value = "Assigned user ID", example = "user123")
    private String assigneeId;
    
    @JsonProperty
    @ApiModelProperty(value = "Task creation timestamp")
    private LocalDateTime createdAt;
    
    @JsonProperty
    @ApiModelProperty(value = "Task last update timestamp")
    private LocalDateTime updatedAt;
    
    @JsonProperty
    @ApiModelProperty(value = "Task due date")
    private LocalDateTime dueDate;
    
    public Task() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = TaskStatus.PENDING;
        this.priority = TaskPriority.MEDIUM;
    }
    
    public Task(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { 
        this.title = title; 
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { 
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { 
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { 
        this.priority = priority;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getAssigneeId() { return assigneeId; }
    public void setAssigneeId(String assigneeId) { 
        this.assigneeId = assigneeId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { 
        this.dueDate = dueDate;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id) && 
               Objects.equals(title, task.title);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }
    
    @Override
    public String toString() {
        return "Task{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", status=" + status +
               ", priority=" + priority +
               ", assigneeId='" + assigneeId + '\'' +
               ", createdAt=" + createdAt +
               '}';
    }
    
    public enum TaskStatus {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    }
    
    public enum TaskPriority {
        LOW, MEDIUM, HIGH, URGENT
    }
}