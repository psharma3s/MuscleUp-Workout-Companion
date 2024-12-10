package com.techelevator.dao;

import com.techelevator.model.CheckInOut;
import com.techelevator.model.UserCheckInStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface CheckInOutDao {
    int checkIn(int userId);
    void checkOut(int visitId);
    int findOngoingVisitId(int userId);
    int findUserIdByUsername(String username);
    List<CheckInOut> getUserVisits(int userId);
    boolean isUserCheckedIn(int userId);
    List<LocalDate> getCheckInDates(int userId);
    LocalDateTime findOngoingCheckInTime(int userId);
    List<UserCheckInStatus> getCheckedInUsers();


}
