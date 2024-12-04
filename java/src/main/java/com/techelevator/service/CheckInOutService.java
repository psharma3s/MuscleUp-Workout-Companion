package com.techelevator.service;

import com.techelevator.dao.JdbcCheckInOutDao;
import com.techelevator.model.CheckInOut;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckInOutService {
    private final JdbcCheckInOutDao jdbcCheckInOutDao;

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
        int visitId = jdbcCheckInOutDao.findOngoingVisitId(userId);
        return visitId != -1;
    }

    public String calculateTotalTime(int userId) {
        List<CheckInOut> visits = jdbcCheckInOutDao.getUserVisits(userId);

        long totalMinutes = 0;

        for (CheckInOut visit : visits) {
            if (visit.getCheckOutTime() != null) {
                LocalDateTime checkInTime = visit.getCheckInTime();
                LocalDateTime checkOutTime = visit.getCheckOutTime();

                long days = checkOutTime.toLocalDate().toEpochDay() - checkInTime.toLocalDate().toEpochDay();
                long hours = checkOutTime.getHour() - checkInTime.getHour();
                long minutes = checkOutTime.getMinute() - checkInTime.getMinute();

                totalMinutes += days * 24 * 60 + hours * 60 + minutes;
            }
        }

        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        return String.format("%d hours, %d minutes", hours, minutes);
    }
}
