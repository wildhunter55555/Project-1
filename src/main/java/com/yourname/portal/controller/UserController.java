package com.yourname.portal.controller;

import com.yourname.portal.entity.User;
import com.yourname.portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.security.Principal;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/available")
    public ResponseEntity<List<User>> getAvailableStaff() {
        return ResponseEntity.ok(userRepository.findByAvailableTrue());
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build(); // Not logged in
        }

        // principal.getName() returns the email used to log in
        Optional<User> userOptional = userRepository.findByEmail(principal.getName());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setPassword(null); // Never send the password hash to the frontend!
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}