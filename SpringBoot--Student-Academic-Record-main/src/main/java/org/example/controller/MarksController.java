package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.dto.MarksRequest;
import org.example.model.Marks;
import org.example.model.Student;
import org.example.model.User;
import org.example.repository.MarksRepository;
import org.example.repository.StudentRepository;
import org.example.service.MarksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/marks")
public class MarksController {

    @Autowired
    private MarksService marksService;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public ResponseEntity<?> getMarks(
            @RequestParam Integer sem,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null || !"STUDENT".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Student student = studentRepository.findByUserId(user.getUserId());

        return ResponseEntity.ok(
                marksService.getMarksForSemester(student.getUsn(), sem)
        );
    }

    @PostMapping
    public ResponseEntity<?> addMarks(
            @RequestBody MarksRequest request,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null || !"STUDENT".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Student student = studentRepository.findByUserId(user.getUserId());

        marksService.saveOrUpdateMarks(
                student.getUsn(),
                request
        );

        return ResponseEntity.ok("Marks saved successfully");
    }
}
