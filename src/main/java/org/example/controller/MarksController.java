package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.model.Marks;
import org.example.model.Student;
import org.example.model.User;
import org.example.repository.MarksRepository;
import org.example.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marks")
public class MarksController {

    @Autowired
    private MarksRepository marksRepository;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public ResponseEntity<?> getMarks(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equalsIgnoreCase("STUDENT")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Student student = studentRepository.findByUserId(user.getUserId());
        if (student == null) {
            return ResponseEntity.status(404).body("Student profile not found");
        }

        List<Marks> marks = marksRepository.findAll().stream()
                .filter(m -> m.getStudentId().equals(student.getStudentId()))
                .toList();
        return ResponseEntity.ok(marks);
    }

    @PostMapping
    public ResponseEntity<?> addMarks(@RequestBody Marks marks, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.getRole().equalsIgnoreCase("STUDENT")) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Student student = studentRepository.findByUserId(user.getUserId());
        if (student == null) {
            return ResponseEntity.status(404).body("Student profile not found");
        }

        marks.setStudentId(student.getStudentId());
        marks.setValidated(false);
        return ResponseEntity.ok(marksRepository.save(marks));
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return ResponseEntity.status(401).body("Not logged in");
        return ResponseEntity.ok(user);
    }

}
