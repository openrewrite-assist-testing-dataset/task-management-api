package com.example.tasks.db;

import com.example.tasks.core.Task;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RawJdbcTaskDAO {
    
    private final DataSource dataSource;
    
    public RawJdbcTaskDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    public Task createWithRawJdbc(Task task) throws SQLException {
        String sql = "INSERT INTO tasks (title, description, status, priority, assignee_id, created_at, updated_at, due_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, task.getTitle());
            stmt.setString(2, task.getDescription());
            stmt.setString(3, task.getStatus().name());
            stmt.setString(4, task.getPriority().name());
            stmt.setString(5, task.getAssigneeId());
            stmt.setTimestamp(6, Timestamp.valueOf(task.getCreatedAt()));
            stmt.setTimestamp(7, Timestamp.valueOf(task.getUpdatedAt()));
            stmt.setTimestamp(8, task.getDueDate() != null ? Timestamp.valueOf(task.getDueDate()) : null);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating task failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    task.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating task failed, no ID obtained.");
                }
            }
        }
        
        return task;
    }
    
    public List<Task> findOverdueTasksWithRawJdbc() throws SQLException {
        String sql = "SELECT * FROM tasks WHERE due_date < ? AND status != 'COMPLETED' ORDER BY due_date ASC";
        List<Task> tasks = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapResultSetToTask(rs));
                }
            }
        }
        
        return tasks;
    }
    
    public Optional<Task> findByIdWithRawJdbc(Long id) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTask(rs));
                }
            }
        }
        
        return Optional.empty();
    }
    
    public int updateTaskStatusWithRawJdbc(Long taskId, Task.TaskStatus newStatus) throws SQLException {
        String sql = "UPDATE tasks SET status = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus.name());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(3, taskId);
            
            return stmt.executeUpdate();
        }
    }
    
    public List<Task> searchTasksByTitleWithRawJdbc(String searchTerm) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE title LIKE ? ORDER BY created_at DESC";
        List<Task> tasks = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + searchTerm + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapResultSetToTask(rs));
                }
            }
        }
        
        return tasks;
    }
    
    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getLong("id"));
        task.setTitle(rs.getString("title"));
        task.setDescription(rs.getString("description"));
        task.setStatus(Task.TaskStatus.valueOf(rs.getString("status")));
        task.setPriority(Task.TaskPriority.valueOf(rs.getString("priority")));
        task.setAssigneeId(rs.getString("assignee_id"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            task.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            task.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        Timestamp dueDate = rs.getTimestamp("due_date");
        if (dueDate != null) {
            task.setDueDate(dueDate.toLocalDateTime());
        }
        
        return task;
    }
}