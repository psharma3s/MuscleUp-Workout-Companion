package com.techelevator.service;

import com.techelevator.dao.CheckInOutDao;
import com.techelevator.dao.JdbcCheckInOutDao;
import com.techelevator.model.CheckInOut;
import com.techelevator.model.UserCheckInStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckInOutService {

    private final CheckInOutDao checkInOutDao; // Use the interface instead of implementation

    @Autowired
    public CheckInOutService(CheckInOutDao checkInOutDao) {
        this.checkInOutDao = checkInOutDao;
    }

    public int getUserIdByUsername(String username) {
        return checkInOutDao.findUserIdByUsername(username);
    }

    public void checkInUser(int userId) {
        if (checkInOutDao.isUserCheckedIn(userId)) {
            throw new IllegalStateException("User is already checked in.");
        }
        checkInOutDao.checkIn(userId);
    }

    public void checkOutUser(int userId) {
        int visitId = checkInOutDao.findOngoingVisitId(userId);
        if (visitId == -1) {
            throw new IllegalStateException("No ongoing visit found for user.");
        }
        checkInOutDao.checkOut(visitId);
    }

    public boolean isUserCheckedIn(int userId) {
        return checkInOutDao.isUserCheckedIn(userId);
    }

    public List<LocalDate> getCheckInDates(int userId) {
        return checkInOutDao.getCheckInDates(userId);
    }

    public LocalDateTime getOngoingCheckInTime(int userId) {
        return checkInOutDao.findOngoingCheckInTime(userId);
    }

    public String calculateAverageTime(int userId) {
        List<CheckInOut> visits = checkInOutDao.getUserVisits(userId);

        long totalMinutes = 0;
        int count = 0;

        for (CheckInOut visit : visits) {
            if (visit.getCheckOutTime() != null) {
                LocalDateTime checkInTime = visit.getCheckInTime();
                LocalDateTime checkOutTime = visit.getCheckOutTime();

                long minutesBetween = Duration.between(checkInTime, checkOutTime).toMinutes();
                totalMinutes += minutesBetween;
                count++;
            }
        }

        if (count == 0) {
            return "No visits recorded";
        }

        long averageMinutes = totalMinutes / count;
        long hours = averageMinutes / 60;
        long minutes = averageMinutes % 60;

        return String.format("%d hours, %d minutes", hours, minutes);
    }

    public String calculateTotalTime(int userId) {
        List<CheckInOut> visits = checkInOutDao.getUserVisits(userId);

        long totalMinutes = 0;

        for (CheckInOut visit : visits) {
            if (visit.getCheckOutTime() != null) {
                LocalDateTime checkInTime = visit.getCheckInTime();
                LocalDateTime checkOutTime = visit.getCheckOutTime();

                totalMinutes += Duration.between(checkInTime, checkOutTime).toMinutes();
            }
        }

        long days = totalMinutes / (24 * 60);
        long remainingMinutes = totalMinutes % (24 * 60);
        long hours = remainingMinutes / 60;
        long minutes = remainingMinutes % 60;

        return String.format("%d days, %d hours, %d minutes", days, hours, minutes);
    }

    public List<UserCheckInStatus> getCheckedInUsers() {
        return checkInOutDao.getCheckedInUsers(); // Call the method for all users
    }

    public List<CheckInOut> getUserVisits(int userId) {
        return checkInOutDao.getUserVisits(userId); // DAO method implementation
    }
}
