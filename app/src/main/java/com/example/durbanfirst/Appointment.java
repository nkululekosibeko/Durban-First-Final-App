package com.example.durbanfirst;

public class Appointment {
    private String id;
    private String userId;
    private String date;
    private String time;
    private String reason;
    private String status;

    // Default constructor
    public Appointment() {}

    // Parameterized constructor
    public Appointment(String id, String userId, String date, String time, String reason, String status) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.status = status;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getReason() {
        return reason;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
