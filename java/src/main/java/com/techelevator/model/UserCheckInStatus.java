package com.techelevator.model;

import java.time.LocalDateTime;

public class UserCheckInStatus {
    private int userId;
    private String username;
    private LocalDateTime lastCheckIn;
    private LocalDateTime lastCheckOut;
    private boolean checkedIn;

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getLastCheckIn() {
        return lastCheckIn;
    }

    public void setLastCheckIn(LocalDateTime lastCheckIn) {
        this.lastCheckIn = lastCheckIn;
    }

    public LocalDateTime getLastCheckOut() {
        return lastCheckOut;
    }

    public void setLastCheckOut(LocalDateTime lastCheckOut) {
        this.lastCheckOut = lastCheckOut;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }
}
