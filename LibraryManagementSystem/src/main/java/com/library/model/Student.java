package com.library.model;

/** A student: users row + students row joined together. */
public class Student {
    private int studentId;
    private int userId;
    private String rollNo;
    private String fullName;
    private String email;
    private String username;
    private String course;
    private int semester;
    private String phone;

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
