package org.example.dto;

import org.example.model.Certificate;
import org.example.model.Marks;

import java.util.List;

public class MentorPendingResponse {
    private List<Certificate> certificates;
    private List<Marks> marks;

    public MentorPendingResponse(List<Certificate> certificates, List<Marks> marks) {
        this.certificates = certificates;
        this.marks = marks;
    }

    public List<Certificate> getCertificates() { return certificates; }
    public List<Marks> getMarks() { return marks; }
}

