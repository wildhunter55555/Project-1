package com.yourname.portal.controller;

import com.yourname.portal.entity.Assignment;
import com.yourname.portal.entity.Task;
import com.yourname.portal.repository.AssignmentRepository;
import com.yourname.portal.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*") // Allows your frontend to call this API
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private AssignmentRepository assignmentRepository;

    // Endpoint to DELETE a task
    @DeleteMapping("/{taskId}")
    public ResponseEntity<String> deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok("Task deleted successfully");
    }

    // Endpoint to view stored/completed tasks
    @GetMapping("/history")
    public ResponseEntity<java.util.List<Task>> getCompletedTasks() {
        return ResponseEntity.ok(taskService.getCompletedTasks());
    }

    // Endpoint to get ALL tasks
    @GetMapping("/all")
    public ResponseEntity<java.util.List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    // Endpoint for Admin to assign a new task
    @PostMapping("/assign")
    public ResponseEntity<Assignment> assignTask(
            @RequestBody Task task,
            @RequestParam Long staffId,
            @RequestParam(required = false) String adminNotes) {

        Assignment newAssignment = taskService.assignTask(task, staffId, adminNotes);
        return ResponseEntity.ok(newAssignment);
    }

    // Endpoint for Staff to update their task status
    @PutMapping("/{taskId}/status")
    public ResponseEntity<Task> updateStatus(
            @PathVariable Long taskId,
            @RequestParam String newStatus) {

        Task updatedTask = taskService.updateTaskStatus(taskId, newStatus);
        return ResponseEntity.ok(updatedTask);
    }

    // Endpoint to fetch tasks for a specific staff member
    @GetMapping("/my-tasks")
    public ResponseEntity<java.util.List<Assignment>> getMyTasks(@RequestParam Long staffId) {
        java.util.List<Assignment> myAssignments = assignmentRepository.findByAssignedToId(staffId);
        return ResponseEntity.ok(myAssignments);
    }
}