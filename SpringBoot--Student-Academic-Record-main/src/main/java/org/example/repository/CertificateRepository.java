package org.example.repository;

import org.example.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CertificateRepository
        extends JpaRepository<Certificate, Long> {

    List<Certificate> findByMentorUsnAndStatus(String mentorUsn, String status);
    List<Certificate> findByStudentUsn(String studentUsn);
    
    @Query("""
            SELECT COALESCE(SUM(c.activityPoints), 0)
            FROM Certificate c
            WHERE c.studentUsn = :usn
            AND c.status = 'APPROVED'
        """)
    int getTotalApprovedActivityPoints(@Param("usn") String usn);


}
