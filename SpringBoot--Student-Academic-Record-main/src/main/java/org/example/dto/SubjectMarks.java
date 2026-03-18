package org.example.dto;

public class SubjectMarks {

    private String name;
    private Integer mse1;
    private Integer mse2;
    private Integer task;
    private Integer see;
    private Integer total;
    private String grade;

    public SubjectMarks() {}

    // Constructor for analytics query
    public SubjectMarks(String name, Double avgTotal) {
        this.name = name;
        this.total = avgTotal != null ? avgTotal.intValue() : 0;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getMse1() { return mse1; }
    public void setMse1(Integer mse1) { this.mse1 = mse1; }

    public Integer getMse2() { return mse2; }
    public void setMse2(Integer mse2) { this.mse2 = mse2; }

    public Integer getTask() { return task; }
    public void setTask(Integer task) { this.task = task; }

    public Integer getSee() { return see; }
    public void setSee(Integer see) { this.see = see; }

    public Integer getTotal() { return total; }
    public void setTotal(Integer total) { this.total = total; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
}