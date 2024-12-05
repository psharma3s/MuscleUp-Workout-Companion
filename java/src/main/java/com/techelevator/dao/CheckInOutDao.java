package com.techelevator.dao;

import com.techelevator.model.CheckInOut;

import java.util.List;

public interface CheckInOutDao {
    int checkIn(int userId);
    void checkOut(int visitId);
    int findOngoingVisitId(int userId);
    int findUserIdByUsername(String username);
    List<CheckInOut> getUserVisits(int userId);
}
