package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.model.Achievement;
import org.example.model.Student;
import org.example.model.User;
import org.example.repository.AchievementRepository;
import org.example.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/achievements")
public class AchievementController {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private StudentRepository studentRepository;

    // =========================
    // GET ALL ACHIEVEMENTS
    // =========================
    @GetMapping
    public ResponseEntity<?> getAchievements(HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null || !"STUDENT".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Student student = studentRepository.findByUserId(user.getUserId());
        if (student == null) {
            return ResponseEntity.status(400).body("Student profile not found");
        }

        List<Achievement> achievements =
                achievementRepository.findByStudentOrderByFromDateDesc(student);

        return ResponseEntity.ok(achievements);
    }

    // =========================
    // ADD ACHIEVEMENT
    // =========================
    @PostMapping
    public ResponseEntity<?> addAchievement(
            @RequestBody Achievement achievement,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null || !"STUDENT".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Student student = studentRepository.findByUserId(user.getUserId());
        if (student == null) {
            return ResponseEntity.status(400).body("Student profile not found");
        }

        // 🔐 OWNERSHIP BINDING
        achievement.setStudent(student);

        return ResponseEntity.ok(achievementRepository.save(achievement));
    }

    // =========================
    // DELETE ACHIEVEMENT
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAchievement(
            @PathVariable Long id,
            HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null || !"STUDENT".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(401).build();
        }

        Student student = studentRepository.findByUserId(user.getUserId());
        if (student == null) {
            return ResponseEntity.status(400).build();
        }

        Achievement achievement = achievementRepository.findById(id).orElse(null);
        if (achievement == null) {
            return ResponseEntity.notFound().build();
        }

        // 🔒 OWNERSHIP CHECK
        if (!achievement.getStudent().getStudentId()
                .equals(student.getStudentId())) {
            return ResponseEntity.status(403).build();
        }

        achievementRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
