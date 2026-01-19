package org.example.controller;

import jakarta.servlet.http.HttpSession;

import java.io.File;

import org.example.model.Certificate;
import org.example.model.Student;
import org.example.model.User;
import org.example.repository.CertificateRepository;
import org.example.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    
    @Autowired
    private CertificateRepository certificateRepository;

    @PostMapping(value = "/certificate/upload", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadCertificate(
            @RequestParam("certificateType") String type,
            @RequestParam("file") MultipartFile file,
            HttpSession session) {

        try {
            User user = (User) session.getAttribute("user");

            if (user == null || !"STUDENT".equalsIgnoreCase(user.getRole())) {
                return ResponseEntity.status(401).body("Unauthorized");
            }

            Student student = studentRepository.findByUserId(user.getUserId());

            if (student == null) {
                return ResponseEntity.badRequest()
                        .body("Please complete student setup before uploading certificates");
            }

            if (student.getMentorUsn() == null || student.getMentorUsn().isBlank()) {
                return ResponseEntity.badRequest()
                        .body("Mentor not assigned. Complete student setup.");
            }

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("No file selected");
            }

            // ===== FILE SAVE =====
            String uploadDir = System.getProperty("user.dir") + "/uploads/";
            new File(uploadDir).mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);

            // ===== SAVE CERTIFICATE =====
            Certificate cert = new Certificate();
            cert.setStudentUsn(student.getUsn());
            cert.setMentorUsn(student.getMentorUsn());
            cert.setCertificateType(type);
            cert.setFilePath("uploads/" + fileName);
            cert.setActivityPoints(calculatePoints(type));
            cert.setStatus("PENDING");

            certificateRepository.save(cert);

            return ResponseEntity.ok("Certificate uploaded successfully");

        } catch (Exception e) {
            e.printStackTrace(); // 👈 IMPORTANT FOR DEBUG
            return ResponseEntity.status(500).body("Server error during upload");
        }
        
    }
    private int calculatePoints(String type) {
        return switch (type) {
            case "NSS" -> 10;
            case "Sports" -> 15;
            case "Workshop" -> 5;
            case "Internship" -> 20;
            default -> 0;
        };
    }



}
