package com.techelevator.model;

import java.util.ArrayList;
import java.util.List;

public class ClassEvent {
    private int classId;
    private String name;
    private String date;       // stored as a String "YYYY-MM-DD" for convenience or LocalDate
    private String time;       // stored as a String "HH:MM" or LocalTime
    private String instructor;
    private String duration;
    private String caloriesBurned;
    private List<String> registeredMembers = new ArrayList<>();

    // Getters and setters

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(String caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public List<String> getRegisteredMembers() {
        return registeredMembers;
    }

    public void setRegisteredMembers(List<String> registeredMembers) {
        this.registeredMembers = registeredMembers;
    }
}
