package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // Show login page
    @GetMapping("/")
    public String index() {
        return "index";  // renders index.html
    }

    // Handle login
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam String role,
                        HttpSession session) {

        User user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            if (user.getRole().equalsIgnoreCase(role)) {
                session.setAttribute("username", user.getUsername()); // store logged-in user
                return "redirect:/tasks";
            }
        }

        return "index"; // failed login
    }

    // Handle signup
    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String password,
                         @RequestParam String role) {

        if (userRepository.findByUsername(username) != null) {
            return "index"; // user already exists
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password); // ⚠️ plain text for now
        newUser.setRole(role);

        userRepository.save(newUser);

        return "index"; // back to login page
    }

    // Handle logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
