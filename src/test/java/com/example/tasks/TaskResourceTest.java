package com.example.tasks;

import com.example.tasks.api.TaskResource;
import com.example.tasks.core.Task;
import com.example.tasks.db.TaskDAO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaskResourceTest {
    
    @Mock
    private TaskDAO taskDAO;
    
    @Mock
    private Principal principal;
    
    private TaskResource taskResource;
    
    @Before
    public void setUp() {
        taskResource = new TaskResource(taskDAO);
    }
    
    @Test
    public void testGetAllTasks() {
        // Given
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1L);
        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(2L);
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskDAO.findAll()).thenReturn(tasks);
        
        // When
        List<Task> result = taskResource.getAllTasks(principal);
        
        // Then
        assertEquals(2, result.size());
        assertEquals("Task 1", result.get(0).getTitle());
        assertEquals("Task 2", result.get(1).getTitle());
        verify(taskDAO, times(1)).findAll();
    }
    
    @Test
    public void testGetTaskById() {
        // Given
        Long taskId = 1L;
        Task task = new Task("Test Task", "Test Description");
        task.setId(taskId);
        when(taskDAO.findById(taskId)).thenReturn(Optional.of(task));
        
        // When
        Response response = taskResource.getTask(principal, taskId);
        
        // Then
        assertEquals(200, response.getStatus());
        Task returnedTask = (Task) response.getEntity();
        assertEquals("Test Task", returnedTask.getTitle());
        verify(taskDAO, times(1)).findById(taskId);
    }
    
    @Test
    public void testGetTaskByIdNotFound() {
        // Given
        Long taskId = 999L;
        when(taskDAO.findById(taskId)).thenReturn(Optional.empty());
        
        // When
        Response response = taskResource.getTask(principal, taskId);
        
        // Then
        assertEquals(404, response.getStatus());
        verify(taskDAO, times(1)).findById(taskId);
    }
    
    @Test
    public void testCreateTask() {
        // Given
        Task task = new Task("New Task", "New Description");
        Task createdTask = new Task("New Task", "New Description");
        createdTask.setId(1L);
        when(taskDAO.create(any(Task.class))).thenReturn(createdTask);
        
        // When
        Response response = taskResource.createTask(principal, task);
        
        // Then
        assertEquals(201, response.getStatus());
        Task returnedTask = (Task) response.getEntity();
        assertEquals("New Task", returnedTask.getTitle());
        verify(taskDAO, times(1)).create(any(Task.class));
    }
    
    @Test
    public void testCreateTaskWithEmptyTitle() {
        // Given
        Task task = new Task("", "Description");
        
        // When
        Response response = taskResource.createTask(principal, task);
        
        // Then
        assertEquals(400, response.getStatus());
        TaskResource.ErrorResponse error = (TaskResource.ErrorResponse) response.getEntity();
        assertEquals("Title is required", error.getError());
        verify(taskDAO, never()).create(any(Task.class));
    }
    
    @Test
    public void testCreateTaskWithNullTitle() {
        // Given
        Task task = new Task(null, "Description");
        
        // When
        Response response = taskResource.createTask(principal, task);
        
        // Then
        assertEquals(400, response.getStatus());
        TaskResource.ErrorResponse error = (TaskResource.ErrorResponse) response.getEntity();
        assertEquals("Title is required", error.getError());
        verify(taskDAO, never()).create(any(Task.class));
    }
    
    @Test
    public void testUpdateTask() {
        // Given
        Long taskId = 1L;
        Task existingTask = new Task("Old Title", "Old Description");
        existingTask.setId(taskId);
        Task updateData = new Task("New Title", "New Description");
        updateData.setStatus(Task.TaskStatus.IN_PROGRESS);
        updateData.setPriority(Task.TaskPriority.HIGH);
        
        when(taskDAO.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskDAO.update(any(Task.class))).thenReturn(existingTask);
        
        // When
        Response response = taskResource.updateTask(principal, taskId, updateData);
        
        // Then
        assertEquals(200, response.getStatus());
        verify(taskDAO, times(1)).findById(taskId);
        verify(taskDAO, times(1)).update(any(Task.class));
    }
    
    @Test
    public void testUpdateTaskNotFound() {
        // Given
        Long taskId = 999L;
        Task updateData = new Task("New Title", "New Description");
        when(taskDAO.findById(taskId)).thenReturn(Optional.empty());
        
        // When
        Response response = taskResource.updateTask(principal, taskId, updateData);
        
        // Then
        assertEquals(404, response.getStatus());
        verify(taskDAO, times(1)).findById(taskId);
        verify(taskDAO, never()).update(any(Task.class));
    }
    
    @Test
    public void testDeleteTask() {
        // Given
        Long taskId = 1L;
        Task task = new Task("Test Task", "Test Description");
        task.setId(taskId);
        when(taskDAO.findById(taskId)).thenReturn(Optional.of(task));
        
        // When
        Response response = taskResource.deleteTask(principal, taskId);
        
        // Then
        assertEquals(204, response.getStatus());
        verify(taskDAO, times(1)).findById(taskId);
        verify(taskDAO, times(1)).delete(taskId);
    }
    
    @Test
    public void testDeleteTaskNotFound() {
        // Given
        Long taskId = 999L;
        when(taskDAO.findById(taskId)).thenReturn(Optional.empty());
        
        // When
        Response response = taskResource.deleteTask(principal, taskId);
        
        // Then
        assertEquals(404, response.getStatus());
        verify(taskDAO, times(1)).findById(taskId);
        verify(taskDAO, never()).delete(anyLong());
    }
    
    @Test
    public void testGetTasksByUser() {
        // Given
        String userId = "user123";
        Task task1 = new Task("Task 1", "Description 1");
        task1.setAssigneeId(userId);
        Task task2 = new Task("Task 2", "Description 2");
        task2.setAssigneeId(userId);
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskDAO.findByAssigneeId(userId)).thenReturn(tasks);
        
        // When
        List<Task> result = taskResource.getTasksByUser(principal, userId);
        
        // Then
        assertEquals(2, result.size());
        assertEquals(userId, result.get(0).getAssigneeId());
        assertEquals(userId, result.get(1).getAssigneeId());
        verify(taskDAO, times(1)).findByAssigneeId(userId);
    }
    
    @Test
    public void testGetTasksByStatus() {
        // Given
        String status = "pending";
        Task task1 = new Task("Task 1", "Description 1");
        task1.setStatus(Task.TaskStatus.PENDING);
        Task task2 = new Task("Task 2", "Description 2");
        task2.setStatus(Task.TaskStatus.PENDING);
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskDAO.findByStatus(Task.TaskStatus.PENDING)).thenReturn(tasks);
        
        // When
        List<Task> result = taskResource.getTasksByStatus(principal, status);
        
        // Then
        assertEquals(2, result.size());
        assertEquals(Task.TaskStatus.PENDING, result.get(0).getStatus());
        assertEquals(Task.TaskStatus.PENDING, result.get(1).getStatus());
        verify(taskDAO, times(1)).findByStatus(Task.TaskStatus.PENDING);
    }
}