package com.techelevator.dao;

import com.techelevator.model.CheckInOut;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JdbcCheckInOutDao implements CheckInOutDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcCheckInOutDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int checkIn(int userId) {
        String sql = "INSERT INTO user_gym_visits (user_id, check_in_time) VALUES (?, ?) RETURNING visit_id";
        return jdbcTemplate.queryForObject(sql, Integer.class, userId, LocalDateTime.now());
    }

    @Override
    public void checkOut(int visitId) {
        String sql = "UPDATE user_gym_visits SET check_out_time = ? WHERE visit_id = ?";
        jdbcTemplate.update(sql, LocalDateTime.now(), visitId);
    }

    @Override
    public int findOngoingVisitId(int userId) {
        String sql = "SELECT visit_id FROM user_gym_visits WHERE user_id = ? AND check_out_time IS NULL LIMIT 1";
        Integer visitId = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return (visitId != null) ? visitId : -1;
    }

    @Override
    public int findUserIdByUsername(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, Integer.class, username);
    }

    @Override
    public List<CheckInOut> getUserVisits(int userId) {
        String sql = "SELECT visit_id, user_id, check_in_time, check_out_time FROM user_gym_visits WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CheckInOut visit = new CheckInOut();
            visit.setVisitId(rs.getInt("visit_id"));
            visit.setUserId(rs.getInt("user_id"));
            visit.setCheckInTime(rs.getTimestamp("check_in_time").toLocalDateTime());
            if (rs.getTimestamp("check_out_time") != null) {
                visit.setCheckOutTime(rs.getTimestamp("check_out_time").toLocalDateTime());
            }
            return visit;
        }, userId);
    }
}
