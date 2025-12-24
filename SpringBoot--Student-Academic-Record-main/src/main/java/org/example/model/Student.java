package org.example.model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentId;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    private String name;

    @Column(nullable = false, unique = true)
    private String usn;

    private String department;

    @Column(name = "academic_year")
    private Integer academicYear;

    @Column(name = "mentor_usn")
    private String mentorUsn;

    @Column(name = "mentor_name")
    private String mentorName;

    private LocalDate dob;

    // ---------- Getters & Setters ----------

    public Long getStudentId() {
        return studentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsn() {
        return usn;
    }

    public void setUsn(String usn) {
        this.usn = usn;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(Integer academicYear) {
        this.academicYear = academicYear;
    }

    public String getMentorUsn() {
        return mentorUsn;
    }

    public void setMentorUsn(String mentorUsn) {
        this.mentorUsn = mentorUsn;
    }

    public String getMentorName() {
        return mentorName;
    }

    public void setMentorName(String mentorName) {
        this.mentorName = mentorName;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
}
