package com.techelevator.service;

import com.techelevator.dao.JdbcCheckInOutDao;
import com.techelevator.model.CheckInOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckInOutService {
    private final JdbcCheckInOutDao jdbcCheckInOutDao;

    @Autowired
    public CheckInOutService(JdbcCheckInOutDao jdbcCheckInOutDao) {
        this.jdbcCheckInOutDao = jdbcCheckInOutDao;
    }

    public int getUserIdByUsername(String username) {
        return jdbcCheckInOutDao.findUserIdByUsername(username);
    }

    public int checkInUser(int userId) {
        return jdbcCheckInOutDao.checkIn(userId);
    }

    public void checkOutUser(int userId) {
        int visitId = jdbcCheckInOutDao.findOngoingVisitId(userId);

        if (visitId != -1) {
            jdbcCheckInOutDao.checkOut(visitId);
        }
    }

    public boolean isUserCheckedIn(int userId) {
        return jdbcCheckInOutDao.isUserCheckedIn(userId);
    }

    public List<LocalDate> getCheckInDates(int userId) {
        return jdbcCheckInOutDao.getCheckInDates(userId);
    }

    public LocalDateTime getOngoingCheckInTime(int userId) {
        return jdbcCheckInOutDao.findOngoingCheckInTime(userId);
    }

    public String calculateAverageTime(int userId) {
        List<CheckInOut> visits = jdbcCheckInOutDao.getUserVisits(userId);

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
        List<CheckInOut> visits = jdbcCheckInOutDao.getUserVisits(userId);

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

}
