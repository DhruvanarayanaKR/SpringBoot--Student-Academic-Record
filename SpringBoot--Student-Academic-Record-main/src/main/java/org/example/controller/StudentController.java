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

    // ===============================
    // GET STUDENT PROFILE
    // ===============================
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null || !"STUDENT".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Student student = studentRepository.findByUserId(user.getUserId());

        if (student == null) {
            return ResponseEntity.status(404).body("Profile not created");
        }

        return ResponseEntity.ok(student);
    }

    // ===============================
    // CREATE / UPDATE PROFILE
    // ===============================
    @PostMapping("/setup")
    public ResponseEntity<?> setupProfile(
            @RequestBody Student studentData,
            HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null || !"STUDENT".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Student student = studentRepository.findByUserId(user.getUserId());

        if (student != null) {
            // Update existing
            student.setName(studentData.getName());
            student.setUsn(studentData.getUsn());
            student.setDepartment(studentData.getDepartment());
            student.setAcademicYear(studentData.getAcademicYear());
            student.setMentorUsn(studentData.getMentorUsn());
            student.setMentorName(studentData.getMentorName());
            student.setDob(studentData.getDob());

            return ResponseEntity.ok(studentRepository.save(student));
        }

        // Create new
        studentData.setUserId(user.getUserId());

        return ResponseEntity.ok(studentRepository.save(studentData));
    }
}
