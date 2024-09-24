package com.example.durbanfirst;

public class Appointment {
    private String id;
    private String userId;
    private String date;
    private String time;
    private String reason;
    private String status;

    public Appointment() {}

    public Appointment(String id, String userId, String date, String time, String reason, String status) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.status = status;
    }

}
