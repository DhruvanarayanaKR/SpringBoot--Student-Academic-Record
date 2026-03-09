package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam String role,
                                   HttpSession session) {

        // Login logic remains the same
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password) || !user.getRole().equalsIgnoreCase(role)) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        session.setAttribute("user", user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestParam String username,
                                    @RequestParam String email,
                                    @RequestParam String password,
                                    @RequestParam String confirmPassword, // 1. Added confirmPassword
                                    @RequestParam String role) {

        // 2. USN Length Check
        // Assuming 'username' is the USN
        if (username == null || username.length() != 10) {
            return ResponseEntity.badRequest().body("USN must be exactly 10 characters long.");
        }

        // 3. Password Match Check
        if (!password.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("Password and Confirm Password must match.");
        }

        if (userRepository.findByUsername(username) != null) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password); // Save the main password
        newUser.setRole(role);
        newUser.setEmail(email);
        newUser.setUsn(username);
        userRepository.save(newUser);

        return ResponseEntity.ok("Signup successful");
    }


    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logged out");

    }
}