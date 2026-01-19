package org.example.repository;

import org.example.model.Achievement;
import org.example.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    List<Achievement> findByStudentOrderByFromDateDesc(Student student);
    
    
}
