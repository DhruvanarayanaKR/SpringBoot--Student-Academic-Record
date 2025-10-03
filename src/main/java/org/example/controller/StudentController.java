package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.model.Student;
import org.example.model.User;
import org.example.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equalsIgnoreCase("STUDENT")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Student student = studentRepository.findByUserId(user.getUserId());
        if (student == null) {
            // return an "empty" profile with just user info
            Student placeholder = new Student();
            placeholder.setUserId(user.getUserId());
            return ResponseEntity.ok(placeholder);
        }
        return ResponseEntity.ok(student);
    }


    @PostMapping("/setup")
    public ResponseEntity<?> setupProfile(@RequestBody Student studentData, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equalsIgnoreCase("STUDENT")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Student existing = studentRepository.findByUserId(user.getUserId());
        if (existing != null) {
            // If profile exists, update instead of rejecting
            existing.setName(studentData.getName());
            existing.setDepartment(studentData.getDepartment());
            existing.setMentorId(studentData.getMentorId());
            studentRepository.save(existing);
            return ResponseEntity.ok(existing);
        }

        // New student profile
        studentData.setUserId(user.getUserId());
        Student saved = studentRepository.save(studentData);
        return ResponseEntity.ok(saved);
    }

}
