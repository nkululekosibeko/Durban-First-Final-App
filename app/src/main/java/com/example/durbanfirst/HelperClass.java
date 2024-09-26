package com.example.durbanfirst;

public class HelperClass {
    private String full_name;
    private String email;
    private String role;

    // Constructor
    public HelperClass(String full_name, String email, String role) {
        this.full_name = full_name;
        this.email = email;
        this.role = role;
    }

    // No-argument constructor for Firebase
    public HelperClass() {
    }

    // Getters and setters
    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
