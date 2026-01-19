
package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long certificateId;

    private String studentUsn;
    private String mentorUsn;

    private String certificateType;
    private String filePath;

    private Integer activityPoints;
    private String status; // PENDING / APPROVED / REJECTED
    
    
    // getters & setters 
	public Long getCertificateId() {
		return certificateId;
	}
	public void setCertificateId(Long certificateId) {
		this.certificateId = certificateId;
	}
	public String getStudentUsn() {
		return studentUsn;
	}
	public void setStudentUsn(String studentUsn) {
		this.studentUsn = studentUsn;
	}
	public String getMentorUsn() {
		return mentorUsn;
	}
	public void setMentorUsn(String mentorUsn) {
		this.mentorUsn = mentorUsn;
	}
	public String getCertificateType() {
		return certificateType;
	}
	public void setCertificateType(String certificateType) {
		this.certificateType = certificateType;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public Integer getActivityPoints() {
		return activityPoints;
	}
	public void setActivityPoints(Integer activityPoints) {
		this.activityPoints = activityPoints;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

   
    
}
