package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.model.*;
import org.example.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/student/profile")
public class StudentProfileController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    @GetMapping("/full")
    public ResponseEntity<?> getFullProfile(HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null || !"STUDENT".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(401).build();
        }

        Student student = studentRepository.findByUserId(user.getUserId());
        if (student == null) {
            return ResponseEntity.status(400).body("Student profile not found");
        }

        int totalPoints =
                certificateRepository.getTotalApprovedActivityPoints(student.getUsn());

        List<Achievement> achievements =
                achievementRepository.findByStudentOrderByFromDateDesc(student);

        Map<String, Object> response = new HashMap<>();
        response.put("student", student);
        response.put("totalActivityPoints", totalPoints);
        response.put("achievements", achievements);

        return ResponseEntity.ok(response);
    }
}
