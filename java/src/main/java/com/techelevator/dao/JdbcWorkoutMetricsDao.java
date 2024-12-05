package com.techelevator.dao;

import com.techelevator.model.WorkoutMetrics;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcWorkoutMetricsDao implements WorkoutMetricsDao {

    private final JdbcTemplate jdbcTemplate;

    public JdbcWorkoutMetricsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addMetric(WorkoutMetrics metric) {
        String sql = "INSERT INTO workout_metrics (user_id, equipment_used, weights, reps, date) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, metric.getUserId(), metric.getEquipmentUsed(),
                metric.getWeights(), metric.getReps(), metric.getDate());
    }

    @Override
    public List<WorkoutMetrics> getMetricsByUserId(int userId) {
        List<WorkoutMetrics> metrics = new ArrayList<>();
        String sql = "SELECT * FROM workout_metrics WHERE user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        while (results.next()) {
            metrics.add(mapRowToWorkoutMetrics(results));
        }
        return metrics;
    }

    @Override
    public List<WorkoutMetrics> getAllMetrics() {
        List<WorkoutMetrics> metrics = new ArrayList<>();
        String sql = "SELECT * FROM workout_metrics";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            metrics.add(mapRowToWorkoutMetrics(results));
        }
        return metrics;
    }

    private WorkoutMetrics mapRowToWorkoutMetrics(SqlRowSet rs) {
        WorkoutMetrics metric = new WorkoutMetrics();
        metric.setMetricId(rs.getInt("metric_id"));
        metric.setUserId(rs.getInt("user_id"));
        metric.setEquipmentUsed(rs.getString("equipment_used"));
        metric.setWeights(rs.getInt("weights"));
        metric.setReps(rs.getInt("reps"));
        metric.setDate(rs.getTimestamp("date").toLocalDateTime());
        return metric;
    }
}
