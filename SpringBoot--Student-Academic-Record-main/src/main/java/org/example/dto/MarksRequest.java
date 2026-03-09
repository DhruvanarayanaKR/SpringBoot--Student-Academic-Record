package org.example.dto;

import java.util.List;

public class MarksRequest {
    private Integer semester;
    private List<SubjectMarks> subjects;

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public List<SubjectMarks> getSubjects() { return subjects; }
    public void setSubjects(List<SubjectMarks> subjects) { this.subjects = subjects; }
}
