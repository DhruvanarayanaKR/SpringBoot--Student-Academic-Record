/*package org.example.controller;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HelloController {
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/")
    public String index() {
        return "index"; // renders index.html (login page)
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam String role) {

        User user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            // check role (you need a role field in User entity)
            if (user.getRole().equalsIgnoreCase(role)) {
                return "tasks"; // ✅ success
            }
        }

        return "index"; // ❌ failed
    }
}
*/






