package com.example.tasks.api;

import com.example.tasks.core.Task;
import com.example.tasks.db.TaskDAO;
import io.dropwizard.auth.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "tasks", description = "Task management operations")
public class TaskResource {
    
    private final TaskDAO taskDAO;
    
    public TaskResource(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }
    
    @GET
    @ApiOperation(value = "Get all tasks", notes = "Retrieve a list of all tasks")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Successfully retrieved tasks"),
        @ApiResponse(code = 401, message = "Authentication required")
    })
    public List<Task> getAllTasks(@Auth Principal principal) {
        return taskDAO.findAll();
    }
    
    @GET
    @Path("/{id}")
    @ApiOperation(value = "Get task by ID", notes = "Retrieve a specific task by its ID")
    public Response getTask(@Auth Principal principal, 
                           @ApiParam(value = "Task ID", required = true) @PathParam("id") Long id) {
        Optional<Task> task = taskDAO.findById(id);
        if (task.isPresent()) {
            return Response.ok(task.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    
    @POST
    @ApiOperation(value = "Create new task", notes = "Create a new task")
    public Response createTask(@Auth Principal principal, Task task) {
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Title is required"))
                    .build();
        }
        
        Task createdTask = taskDAO.create(task);
        return Response.status(Response.Status.CREATED).entity(createdTask).build();
    }
    
    @PUT
    @Path("/{id}")
    @ApiOperation(value = "Update task", notes = "Update an existing task")
    public Response updateTask(@Auth Principal principal,
                              @ApiParam(value = "Task ID", required = true) @PathParam("id") Long id,
                              Task task) {
        Optional<Task> existingTask = taskDAO.findById(id);
        if (!existingTask.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        Task taskToUpdate = existingTask.get();
        
        if (task.getTitle() != null && !task.getTitle().trim().isEmpty()) {
            taskToUpdate.setTitle(task.getTitle());
        }
        
        if (task.getDescription() != null) {
            taskToUpdate.setDescription(task.getDescription());
        }
        
        if (task.getStatus() != null) {
            taskToUpdate.setStatus(task.getStatus());
        }
        
        if (task.getPriority() != null) {
            taskToUpdate.setPriority(task.getPriority());
        }
        
        if (task.getAssigneeId() != null) {
            taskToUpdate.setAssigneeId(task.getAssigneeId());
        }
        
        if (task.getDueDate() != null) {
            taskToUpdate.setDueDate(task.getDueDate());
        }
        
        Task updatedTask = taskDAO.update(taskToUpdate);
        return Response.ok(updatedTask).build();
    }
    
    @DELETE
    @Path("/{id}")
    @ApiOperation(value = "Delete task", notes = "Delete a task by ID")
    public Response deleteTask(@Auth Principal principal,
                              @ApiParam(value = "Task ID", required = true) @PathParam("id") Long id) {
        Optional<Task> task = taskDAO.findById(id);
        if (!task.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        taskDAO.delete(id);
        return Response.noContent().build();
    }
    
    @GET
    @Path("/user/{userId}")
    @ApiOperation(value = "Get tasks by user", notes = "Retrieve tasks assigned to a specific user")
    public List<Task> getTasksByUser(@Auth Principal principal,
                                    @ApiParam(value = "User ID", required = true) @PathParam("userId") String userId) {
        return taskDAO.findByAssigneeId(userId);
    }
    
    @GET
    @Path("/status/{status}")
    @ApiOperation(value = "Get tasks by status", notes = "Retrieve tasks with a specific status")
    public List<Task> getTasksByStatus(@Auth Principal principal,
                                      @ApiParam(value = "Task status", required = true) @PathParam("status") String status) {
        try {
            Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(status.toUpperCase());
            return taskDAO.findByStatus(taskStatus);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException("Invalid status: " + status, Response.Status.BAD_REQUEST);
        }
    }
    
    public static class ErrorResponse {
        private final String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() {
            return error;
        }
    }
}