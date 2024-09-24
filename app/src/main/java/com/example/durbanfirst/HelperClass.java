package com.example.durbanfirst;

public class HelperClass {
    String full_name, email, password, role;

    public HelperClass(String full_name, String email, String password, String role) {
        this.full_name = full_name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public HelperClass() {
    }
}
