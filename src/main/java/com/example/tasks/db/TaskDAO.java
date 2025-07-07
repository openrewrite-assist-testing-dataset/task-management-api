package com.example.tasks.db;

import com.example.tasks.core.Task;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.Optional;

@RegisterBeanMapper(Task.class)
public interface TaskDAO {
    
    @SqlUpdate("INSERT INTO tasks (title, description, status, priority, assignee_id, created_at, updated_at, due_date) " +
               "VALUES (:title, :description, :status, :priority, :assigneeId, :createdAt, :updatedAt, :dueDate)")
    @GetGeneratedKeys("id")
    Long insert(@BindBean Task task);
    
    default Task create(Task task) {
        Long id = insert(task);
        task.setId(id);
        return task;
    }
    
    @SqlQuery("SELECT * FROM tasks WHERE id = :id")
    Optional<Task> findById(@Bind("id") Long id);
    
    @SqlQuery("SELECT * FROM tasks ORDER BY created_at DESC")
    List<Task> findAll();
    
    @SqlQuery("SELECT * FROM tasks WHERE assignee_id = :assigneeId ORDER BY created_at DESC")
    List<Task> findByAssigneeId(@Bind("assigneeId") String assigneeId);
    
    @SqlQuery("SELECT * FROM tasks WHERE status = :status ORDER BY created_at DESC")
    List<Task> findByStatus(@Bind("status") Task.TaskStatus status);
    
    @SqlQuery("SELECT * FROM tasks WHERE priority = :priority ORDER BY created_at DESC")
    List<Task> findByPriority(@Bind("priority") Task.TaskPriority priority);
    
    @SqlUpdate("UPDATE tasks SET title = :title, description = :description, status = :status, " +
               "priority = :priority, assignee_id = :assigneeId, updated_at = :updatedAt, due_date = :dueDate " +
               "WHERE id = :id")
    void updateTask(@BindBean Task task);
    
    default Task update(Task task) {
        updateTask(task);
        return task;
    }
    
    @SqlUpdate("DELETE FROM tasks WHERE id = :id")
    void delete(@Bind("id") Long id);
    
    @SqlQuery("SELECT COUNT(*) FROM tasks")
    int count();
    
    @SqlQuery("SELECT COUNT(*) FROM tasks WHERE status = :status")
    int countByStatus(@Bind("status") Task.TaskStatus status);
    
    @SqlQuery("SELECT COUNT(*) FROM tasks WHERE assignee_id = :assigneeId")
    int countByAssigneeId(@Bind("assigneeId") String assigneeId);
}