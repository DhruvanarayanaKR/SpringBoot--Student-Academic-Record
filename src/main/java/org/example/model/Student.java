package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @Column(name = "user_id", nullable = false)
    private Long userId;
    private String name;
    private Long mentorId;
    private String department;
    // Rename this field
    @Column(name = "academic_year")
    private Integer academicYear;

    // --- Getters and Setters ---
    public Long getStudentId() {
        return studentId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getMentorId() {
        return mentorId;
    }

    public void setMentorId(Long mentorId) {
        this.mentorId = mentorId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getYear() {
        return academicYear;
    }

    public void setYear(Integer year) {
        this.academicYear = year;
    }
}
