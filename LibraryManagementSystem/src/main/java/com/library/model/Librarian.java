package com.library.model;

/** A librarian: users row + librarians row joined together. */
public class Librarian {
    private int librarianId;
    private int userId;
    private String employeeCode;
    private String fullName;
    private String email;
    private String username;
    private String phone;

    public int getLibrarianId() { return librarianId; }
    public void setLibrarianId(int librarianId) { this.librarianId = librarianId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
