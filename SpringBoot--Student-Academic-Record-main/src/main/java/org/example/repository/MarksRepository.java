package org.example.repository;

import org.example.model.Marks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarksRepository extends JpaRepository<Marks, Long> {


    List<Marks> findByValidatedFalseAndStudentUsnIn(List<String> studentUsns);
    List<Marks> findByValidatedFalseAndMentorUsn(String mentorUsn);
    List<Marks> findByStudentUsn(String studentUsn);
    List<Marks> findByStudentUsnAndSemester(String studentUsn, Integer semester);
    void deleteByStudentUsnAndSemester(String studentUsn, Integer semester);


    List<Marks> findByValidatedFalse();
}


