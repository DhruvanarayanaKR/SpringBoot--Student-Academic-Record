package org.example.controller;
import org.example.dto.MentorPendingResponse;
import org.example.model.*;
import jakarta.servlet.http.HttpSession;
import org.example.constants.CertificateStatus;

import org.example.model.Certificate;
import org.example.repository.MarksRepository;
import org.example.repository.CertificateRepository;
import org.example.repository.StudentRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mentor")
public class MentorController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MarksRepository marksRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private StudentRepository studentRepository;

    // ===============================
    // DASHBOARD
    // ===============================
    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"MENTOR".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return ResponseEntity.ok("Mentor dashboard access granted");
    }

    // ===============================
    // GET PENDING (CERTS + MARKS)
    // ===============================
    @GetMapping("/pending")
    public ResponseEntity<?> pendingAll(HttpSession session) {

        User mentor = (User) session.getAttribute("user");
        if (mentor == null || !"MENTOR".equalsIgnoreCase(mentor.getRole())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<Certificate> certs =
                certificateRepository.findByMentorUsnAndStatus(
                        mentor.getUsn(),
                        CertificateStatus.PENDING
                );

        List<Marks> marks =
                marksRepository.findByValidatedFalseAndMentorUsn(
                        mentor.getUsn()
                );

        System.out.println("Mentor USN: " + mentor.getUsn());
        System.out.println("Certificates: " + certs.size());
        System.out.println("Marks: " + marks.size());
        return ResponseEntity.ok(
                new MentorPendingResponse(certs, marks)
        );

    }
    @GetMapping("/me")
    public ResponseEntity<?> getMentor(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null || !"MENTOR".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        return ResponseEntity.ok(user);
    }
    @GetMapping("/mentees")
    public ResponseEntity<?> getMentees(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Not logged in");
        }

        if (!"MENTOR".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Not a mentor");
        }

        if (user.getUsn() == null || user.getUsn().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Mentor USN missing");
        }

        List<Student> students =
                studentRepository.findByMentorUsn(user.getUsn());

        return ResponseEntity.ok(students);
    }
    @GetMapping("/students")
    public ResponseEntity<?> getFilteredStudents(
            @RequestParam(required = false) Integer academicYear,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String status,
            HttpSession session) {

        User mentor = (User) session.getAttribute("user");

        if (mentor == null || !"MENTOR".equalsIgnoreCase(mentor.getRole())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<Student> students = studentRepository.findByMentorUsn(mentor.getUsn());

        List<Map<String,Object>> result = students.stream()

                .filter(s -> academicYear == null || academicYear.equals(s.getAcademicYear()))
                .filter(s -> department == null || department.isEmpty() || department.equalsIgnoreCase(s.getDepartment()))

                .map(s -> {

                    Map<String,Object> map = new HashMap<>();

                    map.put("name", s.getName());
                    map.put("usn", s.getUsn());
                    map.put("department", s.getDepartment());
                    map.put("academicYear", s.getAcademicYear());

                    // compute submission status
                    boolean hasMarks = !marksRepository.findByStudentUsn(s.getUsn()).isEmpty();
                    boolean hasCerts = !certificateRepository.findByStudentUsn(s.getUsn()).isEmpty();

                    String computedStatus;

                    if(hasMarks || hasCerts){
                        computedStatus = "APPROVED";
                    }else{
                        computedStatus = "PENDING";
                    }

                    map.put("status", computedStatus);

                    return map;

                })

                .filter(m -> status == null || status.isEmpty() || status.equalsIgnoreCase((String)m.get("status")))

                .toList();

        return ResponseEntity.ok(result);
    }
    @GetMapping("/student/{id}")
    public Map<String, Object> getStudentProfile(@PathVariable Long id, HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null || !"MENTOR".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Unauthorized");
        }

        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String usn = student.getUsn();


        List<Marks> marks = marksRepository.findByStudentUsn(usn);

        List<Certificate> certs = certificateRepository.findByStudentUsn(usn);

        Map<String, Object> response = new HashMap<>();

        response.put("student", student);
        response.put("marks", marks);
        response.put("certificates", certs);

        return response;
    }
    @GetMapping("/monitor")
    public ResponseEntity<?> monitorStats(HttpSession session) {

        User mentor = (User) session.getAttribute("user");

        if (mentor == null || !"MENTOR".equalsIgnoreCase(mentor.getRole())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String mentorUsn = mentor.getUsn();

        List<Student> students = studentRepository.findByMentorUsn(mentorUsn);

        int totalStudents = students.size();

        int setupCompleted = 0;

        for(Student s : students){
            if(s.getDob()!=null && s.getDepartment()!=null){
                setupCompleted++;
            }
        }

        int setupPending = totalStudents - setupCompleted;

        int pendingCertificates =
                certificateRepository.findByMentorUsnAndStatus(
                        mentorUsn,"PENDING"
                ).size();

        int pendingMarks =
                marksRepository.findByValidatedFalseAndMentorUsn(
                        mentorUsn
                ).size();

        Map<String,Object> stats = new HashMap<>();

        stats.put("totalStudents", totalStudents);
        stats.put("setupCompleted", setupCompleted);
        stats.put("setupPending", setupPending);
        stats.put("pendingCertificates", pendingCertificates);
        stats.put("pendingMarks", pendingMarks);

        return ResponseEntity.ok(stats);
    }
    @PostMapping("/setup")
    public ResponseEntity<?> setupProfile(@RequestBody User updatedUser,
                                          HttpSession session) {

        User mentor = (User) session.getAttribute("user");

        if (mentor == null || !"MENTOR".equalsIgnoreCase(mentor.getRole())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        // update only editable fields
        if (updatedUser.getName() != null)
            mentor.setName(updatedUser.getName());

        if (updatedUser.getEmail() != null)
            mentor.setEmail(updatedUser.getEmail());

        if (updatedUser.getDepartment() != null)
            mentor.setDepartment(updatedUser.getDepartment());

        userRepository.save(mentor);

        // update session also
        session.setAttribute("user", mentor);

        return ResponseEntity.ok("Profile updated");
    }
    @PostMapping("/marks/{id}/approve")
    public ResponseEntity<?> approveMarks(@PathVariable Long id, HttpSession session) {

        User mentor = (User) session.getAttribute("user");

        if (mentor == null || !"MENTOR".equalsIgnoreCase(mentor.getRole())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Marks marks = marksRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Marks not found"));

        marks.setValidated(true);

        marksRepository.save(marks);

        return ResponseEntity.ok("Marks approved");
    }
    @PostMapping("/marks/{id}/reject")
    public ResponseEntity<?> rejectMarks(@PathVariable Long id, HttpSession session) {

        User mentor = (User) session.getAttribute("user");

        if (mentor == null || !"MENTOR".equalsIgnoreCase(mentor.getRole())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        marksRepository.deleteById(id);

        return ResponseEntity.ok("Marks rejected");
    }
    @PostMapping("/certificate/{id}/approve")
    public ResponseEntity<?> approveCertificate(@PathVariable Long id, HttpSession session) {

        User mentor = (User) session.getAttribute("user");

        if (mentor == null || !"MENTOR".equalsIgnoreCase(mentor.getRole())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Certificate cert = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        cert.setStatus(CertificateStatus.APPROVED);

        certificateRepository.save(cert);

        return ResponseEntity.ok("Certificate approved");
    }
    @PostMapping("/certificate/{id}/reject")
    public ResponseEntity<?> rejectCertificate(@PathVariable Long id, HttpSession session) {

        User mentor = (User) session.getAttribute("user");

        if (mentor == null || !"MENTOR".equalsIgnoreCase(mentor.getRole())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        certificateRepository.deleteById(id);

        return ResponseEntity.ok("Certificate rejected");
    }

}

