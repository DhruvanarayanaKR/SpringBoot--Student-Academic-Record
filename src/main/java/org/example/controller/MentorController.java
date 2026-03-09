package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mentor")
public class MentorController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equalsIgnoreCase("LECTURER")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return ResponseEntity.ok("Mentor dashboard access granted");
    }

    @PostMapping("/setup")
    public ResponseEntity<?> setupProfile(@RequestBody User updatedUser, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equalsIgnoreCase("MENTOR")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // Update only allowed fields
        if (updatedUser.getName() != null) user.setName(updatedUser.getName());
        if (updatedUser.getEmail() != null) user.setEmail(updatedUser.getEmail());
        if (updatedUser.getDepartment() != null) user.setDepartment(updatedUser.getDepartment());
        if (updatedUser.getUsn() != null) user.setUsn(updatedUser.getUsn()); // if you want USN too

        userRepository.save(user);

        // also update session so changes reflect immediately
        session.setAttribute("user", user);

        return ResponseEntity.ok(user);
    }



    // ✅ Optionally, get current mentor details
    @GetMapping("/me")
    public ResponseEntity<?> getProfile(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equalsIgnoreCase("MENTOR")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return ResponseEntity.ok(user);
    }
}
