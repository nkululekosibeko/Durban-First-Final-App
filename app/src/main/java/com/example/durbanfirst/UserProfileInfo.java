package com.example.durbanfirst;

public class UserProfileInfo {
    private String full_name;
    private String email;

    // Default constructor required for calls to DataSnapshot.getValue(UserProfileInfo.class)
    public UserProfileInfo() {}

    // Constructor with parameters
    public UserProfileInfo(String full_name, String email) {
        this.full_name = full_name;
        this.email = email;
    }

    // Getter and Setter methods
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
}
