package com.yourname.portal.service;

import com.yourname.portal.entity.Assignment;
import com.yourname.portal.entity.Task;
import com.yourname.portal.entity.User;
import com.yourname.portal.repository.AssignmentRepository;
import com.yourname.portal.repository.TaskRepository;
import com.yourname.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private EmailService emailService;

    // Create a new task and assign it to a staff member
    public Assignment assignTask(Task task, Long staffId, String adminNotes) {
        // 1. Save the new task
        task.setStatus("PENDING");
        Task savedTask = taskRepository.save(task);

        // 2. Find the staff member
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff member not found"));

        // 3. Create the assignment record
        Assignment assignment = new Assignment();
        assignment.setTask(savedTask);
        assignment.setAssignedTo(staff);
        assignment.setAssignedAt(LocalDateTime.now());
        assignment.setAdminNotes(adminNotes);

        Assignment savedAssignment = assignmentRepository.save(assignment);

        // 4. Send the email notification
        emailService.sendTaskNotification(staff.getEmail(), task.getTitle(), task.getLocation());

        return savedAssignment;
    }

    // Update the status of an existing task
    public Task updateTaskStatus(Long taskId, String newStatus) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    // Method to delete a task
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    // Method to get all completed tasks
    public List<Task> getCompletedTasks() {
        return taskRepository.findByStatus("COMPLETED");
    }

    // Method to get ALL tasks
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}