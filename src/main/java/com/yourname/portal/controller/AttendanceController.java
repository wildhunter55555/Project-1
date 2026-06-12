package com.yourname.portal.controller;

import com.yourname.portal.entity.Attendance;
import com.yourname.portal.entity.User;
import com.yourname.portal.repository.AttendanceRepository;
import com.yourname.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/clock-in")
    public ResponseEntity<String> clockIn(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();

        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setDate(LocalDate.now());
        attendance.setClockInTime(LocalTime.now());
        attendance.setStatus("PRESENT");
        attendanceRepository.save(attendance);

        // Mark staff as available for task assignment
        user.setAvailable(true);
        userRepository.save(user);

        return ResponseEntity.ok("Clocked in successfully at " + LocalTime.now());
    }

    @PutMapping("/clock-out")
    public ResponseEntity<String> clockOut(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();

        // Mark staff as unavailable
        user.setAvailable(false);
        userRepository.save(user);

        // In a real app, you would find today's attendance record and update it.
        // For simplicity, we are just updating the user's availability here.
        return ResponseEntity.ok("Clocked out successfully. You are now unavailable for tasks.");
    }
}